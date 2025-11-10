// src/app/features/home/home.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../services/room.service';
import { RoomCardComponent } from '../../shared/components/room-card/room-card.component';
import { ProvinceService } from '../../services/province.service'; // THÊM MỚI
import { Province, District } from '../../models/province.model'; // THÊM MỚI

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RoomCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  rooms: any[] = [];
  amenities: any[] = [];

  // Dữ liệu cho dropdown
  provinces: Province[] = []; // Mới
  districts: District[] = []; // Mới

  // Bộ lọc (thay đổi)
  selectedProvinceCode: string = ''; // Dùng string để 'value=""' hoạt động
  selectedDistrictCode: string = ''; // Dùng string
  
  // Giữ nguyên các bộ lọc cũ
  selectedType = '';
  selectedPrice = '';
  selectedAcreage = '';
  sortOption = 'Mặc định';
  minPrice?: number;
  maxPrice?: number;

  areaOptions = [10, 15, 20, 25, 30, 35, 40];
  roomTypes = ['Phòng trọ', 'Chung cư mini', 'Phòng cao cấp'];

  constructor(
    private roomService: RoomService,
    private provinceService: ProvinceService // THÊM MỚI
  ) {}

  ngOnInit(): void {
    this.loadAllRooms();
    this.loadAmenities();
    this.loadProvinces(); // THÊM MỚI
  }

  // ... (Hàm normalizeRoomData, loadAllRooms, loadAmenities giữ nguyên) ...
  // (Bạn có thể sao chép 3 hàm này từ file cũ)
  
  private normalizeRoomData(rooms: any[]): any[] {
    return rooms.map(room => ({
      id: room.id,
      name: room.name,
      price: room.price,
      area: room.area,
      status: room.status,
      description: room.description,
      address: room.building?.address || 'Chưa có địa chỉ',
      mainImage: room.images?.[0]?.imageUrl || '/assets/images/default-room.jpg',
      images: room.images || [],
      building: room.building
    }));
  }

  loadAllRooms(): void {
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = this.normalizeRoomData(data);
      },
      error: (err) => {
        console.error('❌ Lỗi khi tải danh sách phòng:', err);
      }
    });
  }

  loadAmenities(): void {
    this.roomService.getAmenities().subscribe({
      next: (data) => {
        this.amenities = data.map((a: any) => ({ ...a, selected: false }));
      },
      error: (err) => console.error('❌ Lỗi khi tải tiện nghi:', err)
    });
  }

  /**
   * HÀM MỚI: Tải tất cả Tỉnh/Thành
   */
  loadProvinces(): void {
    this.provinceService.getAllProvinces().subscribe({
      next: (data) => {
        this.provinces = data;
        console.log('✅ Tải tỉnh thành thành công:', this.provinces);
      },
      error: (err) => console.error('❌ Lỗi khi tải tỉnh thành:', err)
    });
  }

  /**
   * HÀM MỚI: Gọi khi chọn Tỉnh/Thành
   */
  onProvinceChange(): void {
    this.districts = []; // Xóa danh sách quận/huyện cũ
    this.selectedDistrictCode = ''; // Reset quận/huyện đã chọn

    const provinceCode = parseInt(this.selectedProvinceCode);
    if (provinceCode) {
      this.provinceService.getDistrictsByProvince(provinceCode).subscribe({
        next: (data) => {
          this.districts = data;
          console.log('✅ Tải quận huyện:', data);
        },
        error: (err) => console.error('❌ Lỗi khi tải quận huyện:', err)
      });
    }
  }

  /**
   * SỬA LẠI HÀM NÀY:
   */
  onSearch(evt?: Event): void {
    evt?.preventDefault();
    
    const filters: any = {};
    
    // ✅ Xử lý khu vực (ĐÃ SỬA)
    if (this.selectedProvinceCode) {
      filters.provinceCode = parseInt(this.selectedProvinceCode);
    }
    if (this.selectedDistrictCode) {
      filters.districtCode = parseInt(this.selectedDistrictCode);
    }
    
    // ✅ Xử lý loại phòng (Giữ nguyên)
    if (this.selectedType) {
      filters.type = this.selectedType;
    }
    
    // ✅ Xử lý khoảng giá (Giữ nguyên)
    if (this.selectedPrice) {
      const [min, max] = this.selectedPrice.split('-').map(Number);
      filters.minPrice = min;
      filters.maxPrice = max;
    }
    
    // ✅ Xử lý diện tích (Giữ nguyên)
    if (this.selectedAcreage) {
      const [minArea, maxArea] = this.selectedAcreage.split('-').map(Number);
      filters.minArea = minArea;
      filters.maxArea = maxArea;
    }

    console.log('🔍 Bộ lọc tìm kiếm:', filters); // Debug

    this.roomService.filterRooms(filters).subscribe({
      next: (data) => {
        this.rooms = this.normalizeRoomData(data);
        console.log(`✅ Tìm thấy ${this.rooms.length} phòng`);
      },
      error: (err) => console.error('❌ Lỗi tìm kiếm:', err)
    });
  }

  // ... (applyFilters, onSortChange, clearFilters giữ nguyên) ...
  // (Bạn có thể sao chép 3 hàm này từ file cũ)
  
  applyFilters(): void {
    const filters: any = {
      // Sửa lại chỗ này để dùng code nếu có
      provinceCode: this.selectedProvinceCode ? parseInt(this.selectedProvinceCode) : undefined,
      districtCode: this.selectedDistrictCode ? parseInt(this.selectedDistrictCode) : undefined,
      type: this.selectedType,
      minPrice: this.minPrice ? this.minPrice * 1000000 : undefined,
      maxPrice: this.maxPrice ? this.maxPrice * 1000000 : undefined,
    };

    if (this.selectedAcreage) {
      filters.minArea = parseInt(this.selectedAcreage);
      filters.maxArea = parseInt(this.selectedAcreage) + 5;
    }

    const selectedAmenities = this.amenities
      .filter(a => a.selected)
      .map(a => a.id);
    
    if (selectedAmenities.length > 0) {
      filters.amenities = selectedAmenities;
    }

    console.log('🎯 Áp dụng bộ lọc:', filters);

    this.roomService.filterRooms(filters).subscribe({
      next: (data) => {
        this.rooms = this.normalizeRoomData(data);
        console.log(`✅ Lọc được ${this.rooms.length} phòng`);
      },
      error: (err) => console.error('❌ Lỗi khi lọc:', err)
    });
  }

  onSortChange(event: any): void {
    const value = event.target.value || '';
    
    if (value === 'Giá tăng dần') {
      this.rooms = [...this.rooms].sort((a, b) => Number(a.price) - Number(b.price));
    } else if (value === 'Giá giảm dần') {
      this.rooms = [...this.rooms].sort((a, b) => Number(b.price) - Number(a.price));
    } else {
      this.rooms = [...this.rooms].sort((a, b) => b.id - a.id);
    }
  }

  clearFilters(): void {
    this.selectedProvinceCode = ''; // Sửa
    this.selectedDistrictCode = ''; // Sửa
    this.districts = []; // Sửa

    this.selectedType = '';
    this.selectedPrice = '';
    this.selectedAcreage = '';
    this.minPrice = undefined;
    this.maxPrice = undefined;
    this.amenities.forEach(a => a.selected = false);
    
    this.loadAllRooms();
  }
}