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
  selectedArea: string = '';
  selectedType: string = '';
  selectedPrice: string = '';
  selectedAcreage: string = '';
  sortOption: string = '';
  minPrice?: number;
  maxPrice?: number;

  areaOptions = [10, 15, 20, 25, 30, 35, 40];
  roomTypes = ['Phòng trọ', 'Chung cư mini', 'Phòng cao cấp'];

  constructor(private roomService: RoomService) {}

  ngOnInit(): void {
    this.loadAllRooms();
    this.loadAmenities();
  }

  loadAllRooms(): void {
    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = data;
      },
      error: (err) => console.error('Lỗi khi tải danh sách phòng:', err)
    });
  }

  loadAmenities(): void {
    this.roomService.getAmenities().subscribe({
      next: (data) => {
        this.amenities = data.map((a: any) => ({ ...a, selected: false }));
      },
      error: (err) => console.error('Lỗi khi tải tiện nghi:', err)
    });
  }

  onSearch(): void {
    const filters = {
      area: this.selectedArea,
      type: this.selectedType,
      priceRange: this.selectedPrice,
      acreage: this.selectedAcreage
    };
    this.roomService.searchRooms(filters).subscribe({
      next: (data) => (this.rooms = data),
      error: (err) => console.error('Lỗi tìm kiếm phòng:', err)
    });
  }

  applyFilters(): void {
    const filters = {
      minPrice: this.minPrice,
      maxPrice: this.maxPrice,
      type: this.selectedType,
      area: this.selectedArea,
      amenities: this.amenities
        .filter(a => a.selected)
        .map(a => a.id)
    };

    this.roomService.filterRooms(filters).subscribe({
      next: (data) => (this.rooms = data),
      error: (err) => console.error('Lỗi khi áp dụng bộ lọc:', err)
    });
  }

  onSortChange(event: any): void {
    const value = event.target.value;
    if (value.includes('tăng')) {
      this.rooms.sort((a, b) => a.price - b.price);
    } else if (value.includes('giảm')) {
      this.rooms.sort((a, b) => b.price - a.price);
    }
  }
}
