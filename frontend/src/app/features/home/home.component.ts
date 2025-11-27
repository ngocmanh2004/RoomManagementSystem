import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../services/room.service';
import { RoomCardComponent } from '../../shared/components/room-card/room-card.component';
import { ProvinceService } from '../../services/province.service'; 
import { Province, District } from '../../models/province.model'; 

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

  provinces: Province[] = []; 
  districts: District[] = []; 

  selectedProvinceCode: string = ''; 
  selectedDistrictCode: string = ''; 
  
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
    private provinceService: ProvinceService
  ) {}

  ngOnInit(): void {
    this.loadAllRooms();
    this.loadAmenities();
    this.loadProvinces(); 
  }

  
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

  loadProvinces(): void {
    this.provinceService.getAllProvinces().subscribe({
      next: (data) => {
        this.provinces = data;
        console.log('✅ Tải tỉnh thành thành công:', this.provinces);
      },
      error: (err) => console.error('❌ Lỗi khi tải tỉnh thành:', err)
    });
  }

  onProvinceChange(): void {
    this.districts = []; 
    this.selectedDistrictCode = ''; 

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

  onSearch(evt?: Event): void {
    evt?.preventDefault();
    
    const filters: any = {};
    
    if (this.selectedProvinceCode) {
      filters.provinceCode = parseInt(this.selectedProvinceCode);
    }
    if (this.selectedDistrictCode) {
      filters.districtCode = parseInt(this.selectedDistrictCode);
    }
    
    if (this.selectedType) {
      filters.type = this.selectedType;
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

    console.log('🔍 Bộ lọc tìm kiếm:', filters);

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
    this.selectedProvinceCode = ''; 
    this.selectedDistrictCode = ''; 
    this.districts = []; 

    this.selectedType = '';
    this.selectedPrice = '';
    this.selectedAcreage = '';
    this.minPrice = undefined;
    this.maxPrice = undefined;
    this.amenities.forEach(a => a.selected = false);
    
    this.loadAllRooms();
  }
}