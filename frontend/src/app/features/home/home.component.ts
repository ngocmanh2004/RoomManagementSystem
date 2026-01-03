import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BuildingService } from '../../services/building.service';
import { Building } from '../../models/building.model';
import { BuildingCardComponent } from '../../shared/components/building-card/building-card.component';
import { ProvinceService } from '../../services/province.service'; 
import { Province, District } from '../../models/province.model'; 

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, BuildingCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  buildings: Building[] = [];

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
    this.buildingService.getAllBuildings().subscribe({
      next: (data) => {
        this.buildings = data;
      },
      error: (err) => {
        console.error('❌ Lỗi khi tải danh sách dãy trọ:', err);
      }
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
    // For now, just reload all buildings
    // In future, can add search/filter for buildings
    this.loadAllBuildings();
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

  // When user clicks on a building card, navigate to building-rooms page
  onBuildingClick(buildingId: number): void {
    this.router.navigate(['/buildings', buildingId]);
  }
}