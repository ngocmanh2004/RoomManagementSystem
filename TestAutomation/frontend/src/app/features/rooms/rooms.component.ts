import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../services/room.service';
import { RoomCardComponent } from '../../shared/components/room-card/room-card.component';
import { ProvinceService } from '../../services/province.service'; // THÊM MỚI
import { Province, District } from '../../models/province.model'; // THÊM MỚI

@Component({
  selector: 'app-rooms',
  standalone: true,
  imports: [CommonModule, FormsModule, RoomCardComponent],
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css'],
})
export class RoomsComponent implements OnInit {
  rooms: any[] = [];
  
  // Biến cho Tỉnh/Huyện (MỚI)
  provinces: Province[] = [];
  districts: District[] = [];

  // Biến cho bộ lọc (MỚI)
  selectedProvinceCode: string = '';
  selectedDistrictCode: string = '';
  selectedPrice: string = '';
  selectedAcreage: string = '';
  searchKeyword: string = '';
  sortOption: string = 'Mới nhất'; // Giữ nguyên

  constructor(
    private roomService: RoomService,
    private provinceService: ProvinceService // THÊM MỚI
  ) {}

  ngOnInit(): void {
    this.loadAllRooms(); // Tải tất cả phòng khi vào trang
    this.loadProvinces(); // Tải danh sách tỉnh
  }

  /** (SỬA) Tải tất cả phòng */
  loadAllRooms(): void {
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = this.normalizeRoomData(data);
        this.sortRooms(); // Sắp xếp sau khi tải
      },
      error: (err) => console.error('Lỗi tải phòng:', err),
    });
  }

  /** (MỚI) Tải tất cả Tỉnh/Thành */
  loadProvinces(): void {
    this.provinceService.getAllProvinces().subscribe({
      next: (data) => (this.provinces = data),
      error: (err) => console.error('Lỗi tải tỉnh thành:', err),
    });
  }

  /** (MỚI) Tải Quận/Huyện khi Tỉnh thay đổi */
  onProvinceChange(): void {
    this.districts = [];
    this.selectedDistrictCode = '';
    const provinceCode = parseInt(this.selectedProvinceCode);
    if (provinceCode) {
      this.provinceService.getDistrictsByProvince(provinceCode).subscribe({
        next: (data) => (this.districts = data),
        error: (err) => console.error('Lỗi tải quận huyện:', err),
      });
    }
  }

  /** (MỚI) Xóa tất cả bộ lọc */
  clearFilters(): void {
    this.selectedProvinceCode = '';
    this.selectedDistrictCode = '';
    this.selectedPrice = '';
    this.selectedAcreage = '';
    this.searchKeyword = '';
    this.districts = [];
    this.loadAllRooms(); // Tải lại tất cả phòng
  }

  /** (VIẾT LẠI HOÀN TOÀN) Áp dụng bộ lọc và gọi API */
  applyFilter(): void {
    // 4. Lấy từ khóa tìm kiếm
    // Ưu tiên tìm kiếm trước
    if (this.searchKeyword.trim()) {
      this.roomService.searchRooms(this.searchKeyword.trim()).subscribe({
        next: (data) => {
          this.rooms = this.normalizeRoomData(data);
          this.sortRooms();
        },
        error: (err) => console.error('Lỗi tìm kiếm:', err)
      });
      return; // Dừng lại sau khi gọi API search
    }

    // Nếu không tìm kiếm, thì bắt đầu lọc
    const filters: any = {};

    // 1. Lấy Tỉnh/Huyện
    if (this.selectedProvinceCode) {
      filters.provinceCode = parseInt(this.selectedProvinceCode);
    }
    if (this.selectedDistrictCode) {
      filters.districtCode = parseInt(this.selectedDistrictCode);
    }

    // 2. Lấy Khoảng giá
    if (this.selectedPrice) {
      const [min, max] = this.selectedPrice.split('-').map(Number);
      filters.minPrice = min;
      filters.maxPrice = max;
    }

    // 3. Lấy Diện tích
    if (this.selectedAcreage) {
      const [minArea, maxArea] = this.selectedAcreage.split('-').map(Number);
      filters.minArea = minArea;
      filters.maxArea = maxArea;
    }
    
    // 5. Gọi API filterRooms
    this.roomService.filterRooms(filters).subscribe({
      next: (data) => {
        this.rooms = this.normalizeRoomData(data);
        this.sortRooms(); // Sắp xếp lại
      },
      error: (err) => console.error('Lỗi lọc phòng:', err),
    });
  }

  // --- CÁC HÀM GIỮ NGUYÊN HOẶC ÍT THAY ĐỔI ---

  /** (GIỮ NGUYÊN) Sắp xếp phòng (Sắp xếp ở frontend) */
  sortRooms(): void {
    const toDate = (dateStr: string | number | Date): Date => {
      if (!dateStr) return new Date(0); // Coi như ngày cổ nhất (01/01/1970)
      return new Date(dateStr);
    };

    if (this.sortOption.includes('Giá thấp')) {
      this.rooms.sort((a, b) => a.price - b.price);
    } else if (this.sortOption.includes('Giá cao')) {
      this.rooms.sort((a, b) => b.price - a.price);
    } else if (this.sortOption.includes('Mới nhất')) {
      // So sánh bằng .getTime() để sort theo ngày tháng
      this.rooms.sort((a, b) => toDate(b.createdAt).getTime() - toDate(a.createdAt).getTime());
    } else if (this.sortOption.includes('Cũ nhất')) {
      // So sánh bằng .getTime()
      this.rooms.sort((a, b) => toDate(a.createdAt).getTime() - toDate(b.createdAt).getTime());
    }
  }

  /** (GIỮ NGUYÊN) Khi người dùng thay đổi sắp xếp */
  onSortChange(event: any): void {
    this.sortOption = event.target.value;
    this.sortRooms();
  }

  /** (GIỮ NGUYÊN) Chuẩn hóa dữ liệu phòng */
  normalizeRoomData(rooms: any[]): any[] {
    return rooms.map((room) => ({
      id: room.id,
      name: room.name,
      price: room.price,
      area: room.area,
      status: room.status,
      description: room.description,
      address: room.building?.address || 'Chưa có địa chỉ',
      mainImage:
        room.images?.[0]?.imageUrl || '/assets/images/default-room.jpg',
      createdAt: room.createdAt, // Quan trọng để sắp xếp
      building: room.building,
    }));
  }
}