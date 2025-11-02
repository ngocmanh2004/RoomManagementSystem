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
  sortOption = '';
  minPrice?: number;
  maxPrice?: number;

  areaOptions = [10, 15, 20, 25, 30, 35, 40];
  roomTypes = ['Phòng trọ', 'Chung cư mini', 'Phòng cao cấp'];

  constructor(private roomService: RoomService) {}

  ngOnInit(): void {
    this.loadAllRooms();
    this.loadAmenities();
  }

  private normalizeRoomImages(rooms: any[]): any[] {
    return rooms.map(r => ({
      ...r,
      // mainImage “vớt” từ các field backend trả về
      mainImage:
        r.imageUrl ||
        r.mainImage ||
        (r.images?.[0]?.imageUrl ?? ''),
    }));
  }

  loadAllRooms(): void {
    this.roomService.getAllRooms().subscribe({
      next: (data) => { this.rooms = this.normalizeRoomImages(data); },
      error: (err) => console.error('Lỗi khi tải danh sách phòng:', err)
    });
  }

  loadAmenities(): void {
    this.roomService.getAmenities().subscribe({
      next: (data) => { this.amenities = data.map((a: any) => ({ ...a, selected: false })); },
      error: (err) => console.error('Lỗi khi tải tiện nghi:', err)
    });
  }

  onSearch(evt?: Event): void {
    evt?.preventDefault();
    const filters = {
      area: this.selectedArea,
      type: this.selectedType,
      priceRange: this.selectedPrice,
      acreage: this.selectedAcreage
    };
    this.roomService.searchRooms(filters).subscribe({
      next: (data) => { this.rooms = this.normalizeRoomImages(data); },
      error: (err) => console.error('Lỗi tìm kiếm phòng:', err)
    });
  }

  applyFilters(): void {
    const filters = {
      area: this.selectedArea,
      type: this.selectedType,
      minPrice: this.minPrice,
      maxPrice: this.maxPrice,
      amenities: this.amenities.filter(a => a.selected).map(a => a.id)
    };
    this.roomService.filterRooms(filters).subscribe({
      next: (data) => { this.rooms = this.normalizeRoomImages(data); },
      error: (err) => console.error('Lỗi khi áp dụng bộ lọc:', err)
    });
  }

  onSortChange(event: any): void {
    const value = event.target.value || '';
    const toNum = (v: any) => Number(v ?? 0);

    if (value.includes('tăng'))      this.rooms = [...this.rooms].sort((a, b) => toNum(a.price) - toNum(b.price));
    else if (value.includes('giảm')) this.rooms = [...this.rooms].sort((a, b) => toNum(b.price) - toNum(a.price));
  }
}
