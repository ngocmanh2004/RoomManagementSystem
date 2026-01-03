import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RoomService } from '../../services/room.service';
import { BuildingService } from '../../services/building.service';
import { Building } from '../../models/building.model';
import { RoomCardComponent } from '../../shared/components/room-card/room-card.component';

@Component({
  selector: 'app-building-rooms',
  standalone: true,
  imports: [CommonModule, FormsModule, RoomCardComponent],
  templateUrl: './building-rooms.component.html',
  styleUrls: ['./building-rooms.component.css']
})
export class BuildingRoomsComponent implements OnInit {
  buildingId!: number;
  building: Building | null = null;
  rooms: any[] = [];
  filteredRooms: any[] = [];

  selectedPrice: string = '';
  selectedAcreage: string = '';
  selectedStatus: string = '';
  sortOption: string = 'Mới nhất';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private roomService: RoomService,
    private buildingService: BuildingService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.buildingId = +params['id'];
      this.loadBuildingInfo();
      this.loadRooms();
    });
  }

  loadBuildingInfo(): void {
    this.buildingService.getBuildingById(this.buildingId).subscribe({
      next: (data) => {
        this.building = data;
      },
      error: (err) => console.error('Lỗi tải thông tin dãy trọ:', err)
    });
  }

  loadRooms(): void {
    this.roomService.getRoomsByBuilding(this.buildingId).subscribe({
      next: (data) => {
        this.rooms = this.normalizeRoomData(data);
        this.filteredRooms = [...this.rooms];
        this.sortRooms();
      },
      error: (err) => console.error('Lỗi tải phòng:', err)
    });
  }

  normalizeRoomData(rooms: any[]): any[] {
    return rooms.map(room => ({
      id: room.id,
      name: room.name,
      price: room.price,
      area: room.area,
      status: room.status,
      description: room.description,
      address: room.building?.address || this.building?.address || 'Chưa có địa chỉ',
      mainImage: room.images?.[0]?.imageUrl || '/assets/images/default-room.jpg',
      createdAt: room.createdAt,
      building: room.building
    }));
  }

  applyFilter(): void {
    this.filteredRooms = this.rooms.filter(room => {
      let match = true;

      if (this.selectedPrice) {
        const [min, max] = this.selectedPrice.split('-').map(Number);
        match = match && room.price >= min && room.price <= max;
      }

      if (this.selectedAcreage) {
        const [minArea, maxArea] = this.selectedAcreage.split('-').map(Number);
        match = match && room.area >= minArea && room.area <= maxArea;
      }

      if (this.selectedStatus) {
        match = match && room.status === this.selectedStatus;
      }

      return match;
    });

    this.sortRooms();
  }

  sortRooms(): void {
    const toDate = (dateStr: string | number | Date): Date => {
      if (!dateStr) return new Date(0);
      return new Date(dateStr);
    };

    if (this.sortOption.includes('Giá thấp')) {
      this.filteredRooms.sort((a, b) => a.price - b.price);
    } else if (this.sortOption.includes('Giá cao')) {
      this.filteredRooms.sort((a, b) => b.price - a.price);
    } else if (this.sortOption.includes('Mới nhất')) {
      this.filteredRooms.sort((a, b) => toDate(b.createdAt).getTime() - toDate(a.createdAt).getTime());
    } else if (this.sortOption.includes('Cũ nhất')) {
      this.filteredRooms.sort((a, b) => toDate(a.createdAt).getTime() - toDate(b.createdAt).getTime());
    }
  }

  onSortChange(event: any): void {
    this.sortOption = event.target.value;
    this.sortRooms();
  }

  clearFilters(): void {
    this.selectedPrice = '';
    this.selectedAcreage = '';
    this.selectedStatus = '';
    this.filteredRooms = [...this.rooms];
    this.sortRooms();
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
