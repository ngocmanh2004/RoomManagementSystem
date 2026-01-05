import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { Building, BuildingService } from '../../../services/building.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-building-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './building-management.component.html',
  styleUrls: ['./building-management.component.css'],
})
export class BuildingManagementComponent implements OnInit {
  buildings: Building[] = [];
  filteredBuildings: Building[] = [];
  searchTerm = '';
  currentDate: Date = new Date();

  // Modal
  isModalOpen = false;
  isEditMode = false;
  currentBuildingId: number | null = null;
  buildingForm: FormGroup;
  formError: string | null = null;

  // Delete Modal
  isDeleteModalOpen = false;
  buildingToDelete: Building | null = null;
  deleteError: string | null = null;

  // Image upload
  selectedImageFile: File | null = null;
  imagePreview: string | null = null;

  constructor(
    private buildingService: BuildingService,
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.buildingForm = this.fb.group({
      name: ['', Validators.required],
      provinceCode: ['', Validators.required],
      districtCode: ['', Validators.required],
      address: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.loadBuildings();
  }

  loadBuildings(): void {
    const landlordId = this.authService.getCurrentLandlordId();
    if (landlordId) {
      this.buildingService.getBuildingsByLandlord(landlordId).subscribe({
        next: (buildings) => {
          this.buildings = buildings;
          this.filteredBuildings = buildings;
        },
        error: (err) => console.error('Lỗi khi tải danh sách dãy trọ:', err)
      });
    }
  }

  searchBuildings(): void {
    const term = this.searchTerm.toLowerCase().trim();
    this.filteredBuildings = this.buildings.filter(b =>
      b.name.toLowerCase().includes(term) ||
      b.address.toLowerCase().includes(term)
    );
  }

  openModal(building?: Building): void {
    this.isEditMode = !!building;
    this.currentBuildingId = building?.id || null;
    
    if (building) {
      this.buildingForm.patchValue(building);
      this.imagePreview = building.imageUrl;
    } else {
      this.buildingForm.reset();
      this.imagePreview = null;
    }
    
    this.selectedImageFile = null;
    this.formError = null;
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.buildingForm.reset();
    this.selectedImageFile = null;
    this.imagePreview = null;
  }

  onImageSelected(event: any): void {
    const file = event.target.files?.[0];
    if (file) {
      this.selectedImageFile = file;
      
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.buildingForm.invalid) {
      this.formError = 'Vui lòng điền đầy đủ thông tin';
      return;
    }

    const landlordId = this.authService.getCurrentLandlordId();
    if (!landlordId) {
      this.formError = 'Không xác định được chủ trọ';
      return;
    }

    const buildingData = {
      ...this.buildingForm.value,
      landlordId: landlordId
    };

    const request = this.isEditMode && this.currentBuildingId
      ? this.buildingService.updateBuilding(this.currentBuildingId, buildingData)
      : this.buildingService.createBuilding(buildingData);

    request.subscribe({
      next: (building) => {
        // Upload ảnh nếu có
        if (this.selectedImageFile && building.id) {
          this.buildingService.uploadBuildingImage(building.id, this.selectedImageFile).subscribe({
            next: () => {
              this.loadBuildings();
              this.closeModal();
            },
            error: (err) => {
              console.error('Lỗi upload ảnh:', err);
              this.loadBuildings();
              this.closeModal();
            }
          });
        } else {
          this.loadBuildings();
          this.closeModal();
        }
      },
      error: (err) => {
        this.formError = err.error?.message || 'Có lỗi xảy ra';
      }
    });
  }

  openDeleteModal(building: Building): void {
    this.buildingToDelete = building;
    this.deleteError = null;
    this.isDeleteModalOpen = true;
  }

  closeDeleteModal(): void {
    this.isDeleteModalOpen = false;
    this.buildingToDelete = null;
    this.deleteError = null;
  }

  confirmDelete(): void {
    if (!this.buildingToDelete?.id) return;

    this.buildingService.deleteBuilding(this.buildingToDelete.id).subscribe({
      next: () => {
        this.loadBuildings();
        this.closeDeleteModal();
      },
      error: (err) => {
        this.deleteError = err.error?.error || 'Có lỗi xảy ra khi xóa';
      }
    });
  }

  viewRooms(buildingId: number): void {
    this.router.navigate(['/landlord/buildings', buildingId, 'rooms']);
  }

  getRoomCount(building: Building): number {
    return building.rooms?.length || 0;
  }

  getAvailableRoomCount(building: Building): number {
    return building.rooms?.filter(r => r.status === 'AVAILABLE').length || 0;
  }
}
