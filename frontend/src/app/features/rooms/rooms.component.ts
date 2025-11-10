import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../services/room.service';
import { RoomCardComponent } from '../../shared/components/room-card/room-card.component';

@Component({
  selector: 'app-rooms',
  standalone: true,
  imports: [CommonModule, FormsModule, RoomCardComponent],
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css'],
})
export class RoomsComponent implements OnInit {
  rooms: any[] = [];
  allRooms: any[] = []; // lưu để lọc/tìm kiếm lại
  areas: string[] = [];
  types: string[] = ['Phòng trọ', 'Chung cư mini', 'Nhà nguyên căn'];

  selectedArea = '';
  selectedType = '';
  priceRange = '';
  searchKeyword = '';
  sortOption = 'Mới nhất';

  constructor(private roomService: RoomService) {}

  ngOnInit(): void {
    this.loadRooms();
    this.loadAreas();
  }

  /** Lấy danh sách phòng từ DB */
  loadRooms(): void {
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = this.normalizeRoomData(data);
        this.allRooms = [...this.rooms];
        this.sortRooms(); // sắp xếp mặc định
      },
      error: (err) => console.error('Lỗi tải phòng:', err),
    });
  }

  /** Lấy danh sách khu vực từ DB */
  loadAreas(): void {
    this.roomService.getAreas().subscribe({
      next: (data) => (this.areas = data || []),
      error: (err) => console.error('Lỗi tải khu vực:', err),
    });
  }

  /** Tìm kiếm phòng */
  searchRooms(): void {
    const keyword = this.searchKeyword.trim();
    if (!keyword) {
      this.loadRooms();
      return;
    }

    this.roomService.searchRooms(keyword).subscribe({
      next: (data) => {
        this.rooms = (data || []).map((r) => ({
          ...r,
          imageUrl: this.normalizeImage(r.imageUrl || r.images?.[0]?.imageUrl),
        }));
        this.sortRooms(); // tự động sắp xếp nếu cần
      },
      error: (err) => console.error('Lỗi tìm kiếm phòng:', err),
    });
  }
  normalizeImage(raw: string): string {
    if (!raw) return 'assets/images/default-room.jpg';
    if (/^https?:\/\//i.test(raw)) return raw;
    if (raw.startsWith('/images/')) return raw;
    return `/images/${raw.replace(/^\/+/, '')}`;
  }

  /** Lọc phòng theo khu vực, loại, giá */
  applyFilter(): void {
    let filtered = [...this.allRooms];

    if (this.selectedArea) {
      filtered = filtered.filter((r) => r.address.includes(this.selectedArea));
    }

    if (this.selectedType) {
      filtered = filtered.filter((r) =>
        r.building?.name?.includes(this.selectedType)
      );
    }

    if (this.priceRange) {
      const [min, max] = this.priceRange.split('-').map(Number);
      filtered = filtered.filter((r) => r.price >= min && r.price <= max);
    }

    this.rooms = filtered;
    this.sortRooms();
  }

  /** Sắp xếp phòng */
  sortRooms(): void {
    if (this.sortOption.includes('Giá thấp')) {
      this.rooms.sort((a, b) => a.price - b.price);
    } else if (this.sortOption.includes('Giá cao')) {
      this.rooms.sort((a, b) => b.price - a.price);
    } else if (this.sortOption.includes('Mới nhất')) {
      this.rooms.sort((a, b) => (b.createdAt || b.id) - (a.createdAt || a.id));
    } else if (this.sortOption.includes('Cũ nhất')) {
      this.rooms.sort((a, b) => (a.createdAt || a.id) - (b.createdAt || b.id));
    }
  }

  /** Khi người dùng thay đổi sắp xếp */
  onSortChange(event: any): void {
    this.sortOption = event.target.value;
    this.sortRooms();
  }

  /** Chuẩn hóa dữ liệu phòng */
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
      createdAt: room.createdAt,
      building: room.building,
    }));
  }
}
