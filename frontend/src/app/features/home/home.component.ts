import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../services/room.service';
import { RoomCardComponent } from '../../shared/components/room-card/room-card.component';

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

  // Bộ lọc
  selectedArea = '';
  selectedType = '';
  selectedPrice = '';
  selectedAcreage = '';
  sortOption = 'Mặc định';
  minPrice?: number;
  maxPrice?: number;

  areaOptions = [10, 15, 20, 25, 30, 35, 40];
  roomTypes = ['Phòng trọ', 'Chung cư mini', 'Phòng cao cấp'];

  constructor(private roomService: RoomService) {}

  ngOnInit(): void {
    this.loadAllRooms();
    this.loadAmenities();
  }

  /**
   * ✅ Chuẩn hóa dữ liệu từ backend về format mà RoomCardComponent cần
   */
  private normalizeRoomData(rooms: any[]): any[] {
    return rooms.map(room => ({
      id: room.id,
      name: room.name,
      price: room.price,
      area: room.area,
      status: room.status,
      description: room.description,
      
      // ✅ Lấy địa chỉ từ building.address
      address: room.building?.address || 'Chưa có địa chỉ',
      
      // ✅ Lấy ảnh đầu tiên làm mainImage
      mainImage: room.images?.[0]?.imageUrl || '/assets/images/default-room.jpg',
      
      // ✅ Giữ nguyên mảng images để hiển thị gallery
      images: room.images || [],
      
      // ✅ Thông tin building
      building: room.building
    }));
  }

  loadAllRooms(): void {
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        console.log('✅ Dữ liệu từ API:', data); // Debug
        this.rooms = this.normalizeRoomData(data);
        console.log('✅ Dữ liệu sau khi chuẩn hóa:', this.rooms); // Debug
      },
      error: (err) => {
        console.error('❌ Lỗi khi tải danh sách phòng:', err);
        // ✅ Hiển thị thông báo lỗi cho user
        alert('Không thể tải danh sách phòng. Vui lòng kiểm tra kết nối!');
      }
    });
  }

  loadAmenities(): void {
    this.roomService.getAmenities().subscribe({
      next: (data) => {
        this.amenities = data.map((a: any) => ({ ...a, selected: false }));
        console.log('✅ Tiện nghi:', this.amenities); // Debug
      },
      error: (err) => console.error('❌ Lỗi khi tải tiện nghi:', err)
    });
  }

  onSearch(evt?: Event): void {
    evt?.preventDefault();
    
    const filters: any = {};
    
    // ✅ Xử lý khu vực
    if (this.selectedArea) {
      filters.area = this.selectedArea;
    }
    
    // ✅ Xử lý loại phòng
    if (this.selectedType) {
      filters.type = this.selectedType;
    }
    
    // ✅ Xử lý khoảng giá
    if (this.selectedPrice) {
      const [min, max] = this.selectedPrice.split('-').map(Number);
      filters.minPrice = min;
      filters.maxPrice = max;
    }
    
    // ✅ Xử lý diện tích
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

  applyFilters(): void {
    const filters: any = {
      area: this.selectedArea,
      type: this.selectedType,
      minPrice: this.minPrice ? this.minPrice * 1000000 : undefined, // ✅ Chuyển triệu VNĐ sang VNĐ
      maxPrice: this.maxPrice ? this.maxPrice * 1000000 : undefined,
    };

    // ✅ Xử lý diện tích từ select
    if (this.selectedAcreage) {
      filters.minArea = parseInt(this.selectedAcreage);
      filters.maxArea = parseInt(this.selectedAcreage) + 5; // Range 5m²
    }

    // ✅ Lấy danh sách amenities được chọn
    const selectedAmenities = this.amenities
      .filter(a => a.selected)
      .map(a => a.id);
    
    if (selectedAmenities.length > 0) {
      filters.amenities = selectedAmenities;
    }

    console.log('🎯 Áp dụng bộ lọc:', filters); // Debug

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
      // Mặc định: Sắp xếp theo ID (mới nhất trước)
      this.rooms = [...this.rooms].sort((a, b) => b.id - a.id);
    }
    
    console.log('🔄 Đã sắp xếp theo:', value);
  }

  /**
   * ✅ Xóa tất cả bộ lọc
   */
  clearFilters(): void {
    this.selectedArea = '';
    this.selectedType = '';
    this.selectedPrice = '';
    this.selectedAcreage = '';
    this.minPrice = undefined;
    this.maxPrice = undefined;
    this.amenities.forEach(a => a.selected = false);
    
    this.loadAllRooms(); // Tải lại tất cả phòng
  }
}