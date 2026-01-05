import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../services/room.service';
import { PageResponse } from '../../models/page-response.model';
import { RoomCardComponent } from '../../shared/components/room-card/room-card.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { ProvinceService } from '../../services/province.service'; 
import { Province, District } from '../../models/province.model'; 

@Component({
  selector: 'app-rooms',
  standalone: true,
  imports: [CommonModule, FormsModule, RoomCardComponent, PaginationComponent],
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css'],
})
export class RoomsComponent implements OnInit {
  rooms: any[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  
  provinces: Province[] = [];
  districts: District[] = [];

  selectedProvinceCode: string = '';
  selectedDistrictCode: string = '';
  selectedPrice: string = '';
  selectedAcreage: string = '';
  searchKeyword: string = '';
  sortOption: string = 'Mới nhất'; 

  constructor(
    private roomService: RoomService,
    private provinceService: ProvinceService
  ) {}

  ngOnInit(): void {
    this.loadAllRooms(); 
    this.loadProvinces();
  }

  loadAllRooms(): void {
    this.roomService.getAllRoomsPaged(this.currentPage, this.pageSize).subscribe({
      next: (response: PageResponse<any>) => {
        this.rooms = this.normalizeRoomData(response.content);
        this.currentPage = response.number;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.sortRooms(); 
      },
      error: (err) => console.error('Lỗi tải phòng:', err),
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadAllRooms();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  loadProvinces(): void {
    this.provinceService.getAllProvinces().subscribe({
      next: (data) => (this.provinces = data),
      error: (err) => console.error('Lỗi tải tỉnh thành:', err),
    });
  }

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

  clearFilters(): void {
    this.selectedProvinceCode = '';
    this.selectedDistrictCode = '';
    this.selectedPrice = '';
    this.selectedAcreage = '';
    this.searchKeyword = '';
    this.districts = [];
    this.loadAllRooms(); 
  }

  applyFilter(): void {
    if (this.searchKeyword.trim()) {
      this.roomService.searchRooms(this.searchKeyword.trim()).subscribe({
        next: (data) => {
          this.rooms = this.normalizeRoomData(data);
          this.sortRooms();
        },
        error: (err) => console.error('Lỗi tìm kiếm:', err)
      });
      return; 
    }

    const filters: any = {};

    if (this.selectedProvinceCode) {
      filters.provinceCode = parseInt(this.selectedProvinceCode);
    }
    if (this.selectedDistrictCode) {
      filters.districtCode = parseInt(this.selectedDistrictCode);
    }

    if (this.selectedPrice) {
      const [min, max] = this.selectedPrice.split('-').map(Number);
      filters.minPrice = min;
      filters.maxPrice = max;
    }

    if (this.selectedAcreage) {
      const [minArea, maxArea] = this.selectedAcreage.split('-').map(Number);
      filters.minArea = minArea;
      filters.maxArea = maxArea;
    }
    
    this.roomService.filterRooms(filters).subscribe({
      next: (data) => {
        this.rooms = this.normalizeRoomData(data);
        this.sortRooms(); 
      },
      error: (err) => console.error('Lỗi lọc phòng:', err),
    });
  }
  sortRooms(): void {
    const toDate = (dateStr: string | number | Date): Date => {
      if (!dateStr) return new Date(0); 
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
      this.rooms.sort((a, b) => toDate(a.createdAt).getTime() - toDate(b.createdAt).getTime());
    }
  }

  onSortChange(event: any): void {
    this.sortOption = event.target.value;
    this.sortRooms();
  }

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