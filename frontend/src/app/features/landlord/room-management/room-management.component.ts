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
import { concatMap, tap, catchError } from 'rxjs/operators';

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

  // Thời gian thực
  currentDate: Date = new Date();

  // Trạng thái
  roomStatuses: { value: RoomStatus; text: string }[] = [
    { value: 'AVAILABLE', text: 'Trống' },
    { value: 'OCCUPIED', text: 'Đã thuê' },
    { value: 'REPAIRING', text: 'Đang sửa chữa' },
  ];

  // Tiện ích
  allAmenities: Amenity[] = [];
  selectedAmenities: Amenity[] = [];
  isAddingNewAmenity = false;
  newAmenityName = '';
  
  // Ảnh
  selectedFiles: File[] = [];
  imagePreviews: string[] = [];
  currentRoomForEdit: Room | null = null;

  constructor(
    private roomService: RoomService,
    private buildingService: BuildingService,
    private amenityService: AmenityService,
    private fb: FormBuilder
  ) {
    registerLocaleData(localeVi, 'vi-VN');
    this.roomForm = this.fb.group({
      name: ['', Validators.required],
      buildingId: [null, Validators.required],
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
      this.allRooms = data.map(room => {
        let tenantName = '-';
        if (room.status === 'OCCUPIED') {
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

    if (this.currentStatusFilter !== 'all') {
      rooms = rooms.filter(r => r.status === this.currentStatusFilter);
    }

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

  getAvailableAmenities(): Amenity[] {
    const selectedIds = new Set(this.selectedAmenities.map(a => a.id));
    return this.allAmenities.filter(a => !selectedIds.has(a.id));
  }

  onAddAmenity(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const amenityId = parseInt(select.value, 10);
    
    if (amenityId) {
      const amenity = this.allAmenities.find(a => a.id === amenityId);
      if (amenity && !this.selectedAmenities.includes(amenity)) {
        this.selectedAmenities.push(amenity);
      }
    }
    select.value = '';
  }

  onRemoveAmenity(amenity: Amenity): void {
    this.selectedAmenities = this.selectedAmenities.filter(a => a.id !== amenity.id);
  }

  onAddNewAmenity(): void {
    if (!this.newAmenityName.trim()) {
      alert('Vui lòng nhập tên tiện ích.');
      return;
    }

    this.amenityService.addAmenity(this.newAmenityName).subscribe(newAmenity => {
      this.allAmenities.push(newAmenity);
      this.selectedAmenities.push(newAmenity);
      this.newAmenityName = '';
      this.isAddingNewAmenity = false;
    });
  }

  openAddModal(): void {
    this.isEditMode = false;
    this.formError = null;
    this.currentRoomForEdit = null;
    this.selectedFiles = [];
    this.imagePreviews = [];
    this.selectedAmenities = [];
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
    this.currentRoomForEdit = JSON.parse(JSON.stringify(room));
    this.selectedFiles = [];
    this.imagePreviews = [];
    
    // Đảm bảo buildingId được set đúng
    const buildingId = room.building?.id || null;
    
    this.roomForm.patchValue({
      name: room.name,
      buildingId: buildingId,
      area: room.area,
      price: room.price,
      status: room.status,
      description: room.description || ''
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
    this.selectedAmenities = [];
    this.isAddingNewAmenity = false;
  }

  onSubmit(): void {
    if (this.roomForm.invalid) {
      this.roomForm.markAllAsTouched();
      return;
    }

    this.formError = null;
    const { buildingId, ...otherData } = this.roomForm.value;
    
    // ✅ CHỈ GỬI ID CỦA AMENITIES, KHÔNG GỬI TOÀN BỘ OBJECT
    const roomData: any = {
      ...otherData,
      building: { id: buildingId },
      amenities: this.selectedAmenities.map(a => ({ id: a.id }))
    };

    if (this.isEditMode && this.currentRoomId) {
      // SỬA PHÒNG
      this.roomService.updateRoom(this.currentRoomId, roomData).pipe(
        concatMap(updatedRoom => {
          // Upload ảnh mới (nếu có)
          if (this.selectedFiles.length > 0) {
            return this.uploadImages(updatedRoom.id!).pipe(
              tap(() => {
                this.showSuccessMessage('Cập nhật phòng thành công!');
              })
            );
          } else {
            this.showSuccessMessage('Cập nhật phòng thành công!');
            return of(updatedRoom);
          }
        }),
        catchError(err => {
          console.error('Lỗi khi cập nhật phòng:', err);
          this.formError = 'Lỗi khi cập nhật phòng: ' + (err.error?.message || err.message);
          return of(null);
        })
      ).subscribe({
        next: (result) => {
          if (result !== null) {
            this.loadRooms();
            this.closeModal();
          }
        }
      });

    } else {
      // THÊM PHÒNG MỚI
      this.roomService.addRoom(roomData).pipe(
        concatMap(newRoom => {
          if (this.selectedFiles.length > 0) {
            return this.uploadImages(newRoom.id!).pipe(
              tap(() => {
                this.showSuccessMessage('Thêm phòng mới thành công!');
              })
            );
          } else {
            this.showSuccessMessage('Thêm phòng mới thành công!');
            return of(newRoom);
          }
        }),
        catchError(err => {
          console.error('Lỗi khi thêm phòng:', err);
          this.formError = 'Lỗi khi thêm phòng: ' + (err.error?.message || err.message);
          return of(null);
        })
      ).subscribe({
        next: (result) => {
          if (result !== null) {
            this.loadRooms();
            this.closeModal();
          }
        }
      });
    }
  }

  openDeleteModal(room: Room): void {
    this.deleteError = null;
    
    if (room.status === 'OCCUPIED') {
      this.showErrorMessage('Không thể xóa phòng đang có người thuê!');
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
        this.showSuccessMessage('Xóa phòng thành công!');
        this.loadRooms();
        this.closeDeleteModal();
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 409) {
          this.deleteError = err.error?.error || 'Không thể xóa phòng đang có người thuê.';
        } else {
          this.deleteError = 'Lỗi khi xóa phòng: ' + (err.error?.message || err.message);
        }
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'OCCUPIED': return 'status-occupied';
      case 'AVAILABLE': return 'status-available';
      case 'REPAIRING': return 'status-repairing';
      default: return 'status-default';
    }
  }

  getStatusText(status: string): string {
    const s = this.roomStatuses.find(rs => rs.value === status);
    return s ? s.text : 'Không xác định';
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }

    for (let i = 0; i < input.files.length; i++) {
      const file = input.files[i];
      this.selectedFiles.push(file);

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreviews.push(e.target.result);
      };
      reader.readAsDataURL(file);
    }
    
    input.value = '';
  }

  onRemoveNewFile(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.imagePreviews.splice(index, 1);
  }

  onDeleteExistingImage(image: RoomImage, event: Event): void {
    event.stopPropagation();
    event.preventDefault();

    if (!confirm('Bạn có chắc muốn xóa ảnh này?')) {
      return;
    }

    this.roomService.deleteImage(image.id).subscribe({
      next: () => {
        if (this.currentRoomForEdit && this.currentRoomForEdit.images) {
          this.currentRoomForEdit.images = this.currentRoomForEdit.images.filter(
            (img) => img.id !== image.id
          );
        }
        this.showSuccessMessage('Xóa ảnh thành công!');
      },
      error: (err) => {
        this.showErrorMessage('Lỗi khi xóa ảnh: ' + (err.error?.message || err.message));
      },
    });
  }

  private uploadImages(roomId: number): Observable<any> {
    if (this.selectedFiles.length === 0) {
      return of(null);
    }
    return this.roomService.uploadImages(roomId, this.selectedFiles);
  }

  // HÀM THÔNG BÁO
  private showSuccessMessage(message: string): void {
    alert(message);
  }

  private showErrorMessage(message: string): void {
    alert(message);
  }
}