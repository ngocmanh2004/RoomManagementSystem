import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe } from '@angular/common';
import { Room, RoomImage, RoomService, RoomStatus } from '../../../services/room.service';
import { registerLocaleData } from '@angular/common';
import localeVi from '@angular/common/locales/vi';
import {
  ReactiveFormsModule,
  FormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Building, BuildingService } from '../../../services/building.service';
import { Amenity, AmenityService } from '../../../services/amenity.service';
import { Observable, of } from 'rxjs';
import { concatMap } from 'rxjs/operators';

@Component({
  selector: 'app-room-management',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    CurrencyPipe,
    DecimalPipe,
  ],
  templateUrl: './room-management.component.html',
  styleUrls: ['./room-management.component.css'],
})
export class RoomManagementComponent implements OnInit {
  buildings: Building[] = [];
  allRooms: Room[] = [];
  filteredRooms: Room[] = [];
  
  // Stats
  totalRooms = 0;
  availableRooms = 0;
  occupiedRooms = 0;
  repairingRooms = 0;

  // Search & Filter
  searchTerm = '';
  currentStatusFilter: RoomStatus | 'all' = 'all';

  // Modal
  isModalOpen = false;
  isEditMode = false;
  currentRoomId: number | null = null;
  roomForm: FormGroup;
  formError: string | null = null;

  // Delete Modal
  isDeleteModalOpen = false;
  roomToDelete: Room | null = null;
  deleteError: string | null = null;

  //Thời gian thực
  currentDate: Date = new Date();

  // Trạng thái
  roomStatuses: { value: RoomStatus; text: string }[] = [
    { value: 'AVAILABLE', text: 'Trống' },
    { value: 'OCCUPIED', text: 'Đã thuê' },
    { value: 'REPAIRING', text: 'Đang sửa chữa' },
  ];

  //Tiện ích
  allAmenities: Amenity[] = []; // Tất cả tiện ích từ CSDL
  selectedAmenities: Amenity[] = []; // Tiện ích đã chọn cho phòng này
  isAddingNewAmenity = false; // Cờ bật/tắt ô input thêm mới
  newAmenityName = ''; // Tên tiện ích mới (cho nút "+")
  
  // Ảnh
  selectedFiles: File[] = [];
  imagePreviews: string[] = []; // Xem trước ảnh mới chọn
  currentRoomForEdit: Room | null = null; // Giữ ảnh cũ khi sửa

  constructor(private roomService: RoomService,private buildingService: BuildingService, private amenityService: AmenityService, private fb: FormBuilder) {
    registerLocaleData(localeVi, 'vi-VN');
    // Khởi tạo form
    this.roomForm = this.fb.group({
      name: ['', Validators.required],
      buildingId: [null, Validators.required],
      // Không có "tầng" theo yêu cầu
      area: [0, [Validators.required, Validators.min(1), Validators.max(999.99)]],
      price: [0, [Validators.required, Validators.min(100000)]],
      status: ['AVAILABLE', Validators.required],
      description: [''],
    });
  }

  ngOnInit(): void {
    this.loadRooms();
    this.loadBuildings();
    this.loadAmenities();
  }

  loadBuildings(): void {
    this.buildingService.getAllBuildings().subscribe(data => {
      this.buildings = data;
    });
  }

  loadAmenities(): void {
    this.amenityService.getAmenities().subscribe(data => {
      this.allAmenities = data;
    });
  }

  loadRooms(): void {
    this.roomService.getAllRooms().subscribe((data) => {
      // Logic xử lý tên khách thuê (Tạm thời)
      this.allRooms = data.map(room => {
        let tenantName = '-';
        if (room.status === 'OCCUPIED') {
          // VÍ DỤ GIẢ LẬP (dựa theo ảnh):
          if(room.name.includes('A101')) tenantName = 'Nguyễn Văn B';
          if(room.name.includes('B201')) tenantName = 'Trần Thị C';
          if(room.name.includes('B203')) tenantName = 'Lê Văn D';
          if(room.name.includes('C302')) tenantName = 'Phạm Thị E';
        }
        return { ...room, tenantName };
      });

      this.applyFilters();
      this.updateStats();
    });
  }

  updateStats(): void {
    this.totalRooms = this.allRooms.length;
    this.availableRooms = this.allRooms.filter(r => r.status === 'AVAILABLE').length;
    this.occupiedRooms = this.allRooms.filter(r => r.status === 'OCCUPIED').length;
    this.repairingRooms = this.allRooms.filter(r => r.status === 'REPAIRING').length;
  }

  applyFilters(): void {
    let rooms = [...this.allRooms];

    // Lọc theo trạng thái
    if (this.currentStatusFilter !== 'all') {
      rooms = rooms.filter(r => r.status === this.currentStatusFilter);
    }

    // Lọc theo tìm kiếm
    if (this.searchTerm) {
      const lowerTerm = this.searchTerm.toLowerCase();
      rooms = rooms.filter(
        (r) =>
          r.name.toLowerCase().includes(lowerTerm) ||
          (r.id + '').includes(lowerTerm) ||
          (r.tenantName && r.tenantName.toLowerCase().includes(lowerTerm))
      );
    }

    this.filteredRooms = rooms;
  }

  onFilterStatus(event: Event): void {
    this.currentStatusFilter = (event.target as HTMLSelectElement).value as RoomStatus | 'all';
    this.applyFilters();
  }

  onSearchChange(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value;
    this.applyFilters();
  }

  // Tiện ích

  /** Lấy danh sách tiện ích CHƯA ĐƯỢC CHỌN để hiển thị trong dropdown */
  getAvailableAmenities(): Amenity[] {
    const selectedIds = new Set(this.selectedAmenities.map(a => a.id));
    return this.allAmenities.filter(a => !selectedIds.has(a.id));
  }

  /** Thêm tiện ích từ dropdown vào danh sách (pills) */
  onAddAmenity(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const amenityId = parseInt(select.value, 10);
    
    if (amenityId) {
      const amenity = this.allAmenities.find(a => a.id === amenityId);
      if (amenity && !this.selectedAmenities.includes(amenity)) {
        this.selectedAmenities.push(amenity);
      }
    }
    // Reset dropdown về "Chọn tiện ích"
    select.value = '';
  }

  /** Xóa tiện ích khỏi danh sách (pills) */
  onRemoveAmenity(amenity: Amenity): void {
    this.selectedAmenities = this.selectedAmenities.filter(a => a.id !== amenity.id);
  }

  /** Xử lý nút "+" (Thêm tiện ích mới) */
  onAddNewAmenity(): void {
    if (!this.newAmenityName.trim()) {
      alert('Vui lòng nhập tên tiện ích.');
      return;
    }

    this.amenityService.addAmenity(this.newAmenityName).subscribe(newAmenity => {
      // 1. Thêm vào danh sách tổng
      this.allAmenities.push(newAmenity);
      // 2. Thêm luôn vào danh sách đã chọn
      this.selectedAmenities.push(newAmenity);
      // 3. Reset
      this.newAmenityName = '';
      this.isAddingNewAmenity = false;
    });
  }

  // ===========================================
  // Modal Thêm / Sửa (US 1.1 & 1.2)
  // ===========================================

  openAddModal(): void {
    this.isEditMode = false;
    this.formError = null;
    this.currentRoomForEdit = null; // Xóa ảnh cũ (nếu có)
    this.selectedFiles = [];       // Xóa file chờ
    this.imagePreviews = [];
    this.selectedAmenities = [];     // Xóa preview
    this.roomForm.reset({
      name: '',
      buildingId: null,
      area: 0,
      price: 0,
      status: 'AVAILABLE',
      description: ''
    });
    this.isModalOpen = true;
  }

  openEditModal(room: Room): void {
    this.isEditMode = true;
    this.formError = null;
    this.currentRoomId = room.id!;
    // Lưu lại room để hiển thị ảnh cũ (cần deep copy)
    this.currentRoomForEdit = JSON.parse(JSON.stringify(room)); 
    this.selectedFiles = [];    // Xóa file chờ
    this.imagePreviews = [];  // Xóa preview
    this.roomForm.patchValue({
      ...room,
      buildingId: room.building?.id 
    });
    this.selectedAmenities = room.amenities ? [...room.amenities] : [];
    
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.currentRoomId = null;
    this.currentRoomForEdit = null;
    this.selectedFiles = [];
    this.imagePreviews = [];
    this.selectedAmenities = []; // Thêm reset tiện ích
    this.isAddingNewAmenity = false; // Thêm reset nút "+"
  }

  onSubmit(): void {
    if (this.roomForm.invalid) {
      this.roomForm.markAllAsTouched();
      return;
    }

    this.formError = null;
    const { buildingId, ...otherData } = this.roomForm.value;
    const roomData: any = {
      ...otherData,
      building: { id: buildingId },
      amenities: this.selectedAmenities
    };

    if (this.isEditMode && this.currentRoomId) {
      // === LOGIC SỬA ===
      this.roomService.updateRoom(this.currentRoomId, roomData).pipe(
        // Sau khi sửa phòng, TIẾP TỤC upload ảnh
        concatMap(updatedRoom => this.uploadImages(updatedRoom.id!))
      ).subscribe({
        next: () => {
          this.loadRooms();
          this.closeModal();
        },
        error: (err) => this.formError = 'Lỗi khi cập nhật phòng hoặc tải ảnh.'
      });

    } else {
      // === LOGIC THÊM MỚI ===
      this.roomService.addRoom(roomData).pipe(
        // Sau khi thêm phòng, LẤY ID và upload ảnh
        concatMap(newRoom => this.uploadImages(newRoom.id!))
      ).subscribe({
        next: () => {
          this.loadRooms();
          this.closeModal();
        },
        error: (err) => this.formError = 'Lỗi khi thêm phòng hoặc tải ảnh.'
      });
    }
  }

  // ===========================================
  // Modal Xóa (US 1.3)
  // ===========================================

  openDeleteModal(room: Room): void {
    this.deleteError = null;
    
    // US 1.3: Kiểm tra ở frontend
    if (room.status === 'OCCUPIED') {
      alert('Không thể xóa phòng đang có người thuê!');
      return;
    }
    this.roomToDelete = room;
    this.isDeleteModalOpen = true;
  }

  closeDeleteModal(): void {
    this.isDeleteModalOpen = false;
    this.roomToDelete = null;
  }

  confirmDelete(): void {
    if (!this.roomToDelete) return;

    this.deleteError = null;
    this.roomService.deleteRoom(this.roomToDelete.id!).subscribe({
      next: () => {
        this.loadRooms();
        this.closeDeleteModal();
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 409) {
          // Lỗi 409 Conflict (do backend trả về)
          this.deleteError = err.error?.error || 'Không thể xóa phòng đang có người thuê.';
        } else {
          this.deleteError = 'Lỗi khi xóa phòng. Vui lòng thử lại.';
        }
      }
    });
  }

  // Helper (US 1.4)
  getStatusClass(status: string): string {
    switch (status) {
      case 'OCCUPIED':
        return 'status-occupied';
      case 'AVAILABLE':
        return 'status-available';
      case 'REPAIRING':
        return 'status-repairing';
      default:
        return 'status-default';
    }
  }

  getStatusText(status: string): string {
    const s = this.roomStatuses.find(rs => rs.value === status);
    return s ? s.text : 'Không xác định';
  }

  // ===========================================
  // THÊM CÁC HÀM XỬ LÝ ẢNH
  // ===========================================

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }

    for (let i = 0; i < input.files.length; i++) {
      const file = input.files[i];
      this.selectedFiles.push(file);

      // Tạo preview
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreviews.push(e.target.result);
      };
      reader.readAsDataURL(file);
    }
    
    // Reset input để có thể chọn lại file giống nhau
    input.value = '';
  }

  /** Xóa ảnh MỚI (chưa upload) khỏi hàng đợi */
  onRemoveNewFile(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.imagePreviews.splice(index, 1);
  }

  /** Xóa ảnh CŨ (đã có trên server) */
  onDeleteExistingImage(image: RoomImage, event: Event): void {
    event.stopPropagation(); // Ngăn modal đóng
    event.preventDefault(); // Ngăn hành vi mặc định

    if (!confirm('Bạn có chắc muốn xóa ảnh này? Hành động này không thể hoàn tác.')) {
      return;
    }

    this.roomService.deleteImage(image.id).subscribe({
      next: () => {
        // Xóa ảnh khỏi UI ngay lập tức
        if (this.currentRoomForEdit && this.currentRoomForEdit.images) {
          this.currentRoomForEdit.images = this.currentRoomForEdit.images.filter(
            (img) => img.id !== image.id
          );
        }
      },
      error: (err) => {
        alert('Lỗi khi xóa ảnh. Vui lòng thử lại.');
        console.error(err);
      },
    });
  }

  /** Hàm helper để upload file */
  private uploadImages(roomId: number): Observable<any> {
    if (this.selectedFiles.length === 0) {
      return of(null); // Không có gì để upload, trả về observable rỗng
    }
    return this.roomService.uploadImages(roomId, this.selectedFiles);
  }
}