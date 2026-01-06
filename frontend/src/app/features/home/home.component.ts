import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BuildingService } from '../../services/building.service';
import { Building } from '../../models/building.model';
import { PageResponse } from '../../models/page-response.model';
import { BuildingCardComponent } from '../../shared/components/building-card/building-card.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { ProvinceService } from '../../services/province.service'; 
import { Province, District } from '../../models/province.model'; 

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, BuildingCardComponent, PaginationComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  buildings: Building[] = [];
  currentPage: number = 0;
  pageSize: number = 5;
  totalPages: number = 0;
  totalElements: number = 0;

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
    private buildingService: BuildingService,
    private provinceService: ProvinceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAllBuildings();
    this.loadProvinces(); 
  }

  loadAllBuildings(): void {
    this.buildingService.getAllBuildingsPaged(this.currentPage, this.pageSize).subscribe({
      next: (response: PageResponse<Building>) => {
        this.buildings = response.content;
        this.currentPage = response.number;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
      },
      error: (err) => {
      }
    });
  }

  onPageChange(page: number): void {
    const hasFilter = this.selectedProvinceCode || this.selectedDistrictCode || 
                      this.selectedPrice || this.selectedAcreage;
    
    if (!hasFilter) {
      this.currentPage = page;
      this.loadAllBuildings();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  loadProvinces(): void {
    this.provinceService.getAllProvinces().subscribe({
      next: (data) => {
        this.provinces = data;
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
        },
        error: (err) => console.error('❌ Lỗi khi tải quận huyện:', err)
      });
    }
  }

  onSearch(evt?: Event): void {
    evt?.preventDefault();
    
    const filterParams: any = {};
    
    if (this.selectedProvinceCode) {
      filterParams.provinceCode = this.selectedProvinceCode;
    }
    
    if (this.selectedDistrictCode) {
      filterParams.districtCode = this.selectedDistrictCode;
    }
    
    if (this.selectedPrice) {
      const [min, max] = this.selectedPrice.split('-').map(p => parseInt(p));
      filterParams.minPrice = min;
      filterParams.maxPrice = max;
    }
    
    if (this.selectedAcreage) {
      const [min, max] = this.selectedAcreage.split('-').map(a => parseInt(a));
      filterParams.minAcreage = min;
      filterParams.maxAcreage = max;
    }
    
    if (Object.keys(filterParams).length > 0) {
      this.buildingService.searchBuildings(filterParams).subscribe({
        next: (data) => {
          this.buildings = data;
          this.currentPage = 0;
          this.totalPages = 1;
          this.totalElements = data.length;
        },
        error: (err) => {
          console.error('❌ Lỗi khi tìm kiếm:', err);
          this.buildings = [];
          this.totalPages = 0;
          this.totalElements = 0;
        }
      });
    } else {
      this.loadAllBuildings();
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
    
    this.loadAllBuildings();
  }

  onBuildingClick(buildingId: number): void {
    this.router.navigate(['/buildings', buildingId]);
  }
}