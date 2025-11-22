import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { TenantService } from '../../services/tenant.service';
import { LandlordService } from '../../services/landlord.service';
import { ProvinceService } from '../../services/province.service';
import { User } from '../../models/users'; // Giả sử bạn đã tạo file này
import { Tenant } from '../../models/tenant.model'; // Giả sử bạn đã tạo file này
import { Province, District } from '../../models/province.model'; // Giả sử bạn đã tạo file này

@Component({
  selector: 'app-tenant-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './tenant-profile.component.html',
  styleUrls: ['./tenant-profile.component.css']
})
export class TenantProfileComponent implements OnInit {
  editMode = false;
  saving = false;

  user: User | null = null;
  tenant: Tenant | null = null;
  contract: any = null; 
  room: any = null;    
  invoice: any = null;  
  isLandlordView = false;

  provinces: Province[] = [];
  districts: District[] = [];

  profileForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private tenantService: TenantService,
    private landlordService: LandlordService,
    private provinceService: ProvinceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadProvinces(); 
    // Refresh current user and subscribe reactively to changes
    this.authService.fetchCurrentUser().subscribe({ next: () => {}, error: () => {} });
    this.subscribeToCurrentUser();
  }

  initForm(): void {
    this.profileForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.email]],
      phone: ['', Validators.required],
      
      cccd: [''],
      dateOfBirth: [''],
      provinceCode: [null], 
      districtCode: [null], 
      address: [''] 
    });
  }

  loadProvinces(): void {
    this.provinceService.getAllProvinces().subscribe(data => {
      this.provinces = data;
    });
  }

  /**
   * Subscribe to authService.currentUser$ so the profile view updates reactively
   * when the user's role changes (e.g., after admin approval).
   */
  subscribeToCurrentUser(): void {
    this.authService.currentUser$.subscribe((currentUser) => {
      if (!currentUser) return;

      // If user is landlord, load landlord info and show landlord view
      if (currentUser.role === 1) {
        this.isLandlordView = true;

        // Try to load landlord data
        this.landlordService.getRegistrationStatus(currentUser.id).subscribe({
          next: (landlordData) => {
            if (!landlordData) return;
            this.user = landlordData.user;
            this.profileForm.patchValue({
              fullName: this.user?.fullName || '',
              email: this.user?.email || '',
              phone: this.user?.phone || '',
              cccd: landlordData.cccd || '',
              dateOfBirth: '',
              provinceCode: landlordData.provinceCode || null,
              districtCode: landlordData.districtCode || null,
              address: landlordData.address || ''
            });

            if (landlordData.provinceCode) {
              this.loadDistricts(landlordData.provinceCode);
            }
          },
          error: (err) => {
            console.error('Lỗi khi tải thông tin landlord:', err);
          }
        });

        // Also try to populate tenant record if exists (optional)
        this.tenantService.getTenantByUserId(currentUser.id).subscribe({ next: (t) => this.tenant = t, error: () => {} });

      } else {
        // Tenant view
        this.isLandlordView = false;
        this.tenantService.getTenantByUserId(currentUser.id).subscribe({
          next: (tenantData) => {
            this.tenant = tenantData;
            this.user = tenantData.user;
            this.profileForm.patchValue({
              fullName: this.user.fullName || '',
              email: this.user.email || '',
              phone: this.user.phone || '',
              cccd: tenantData.cccd || '',
              dateOfBirth: tenantData.dateOfBirth || '',
              provinceCode: tenantData.provinceCode || null,
              districtCode: tenantData.districtCode || null,
              address: tenantData.address || ''
            });

            if (tenantData.provinceCode) {
              this.loadDistricts(tenantData.provinceCode);
            }
          },
          error: (err) => {
            console.error('Lỗi khi tải thông tin tenant:', err);
          }
        });
      }
    });
  }


  loadDistricts(provinceCode: number): void {
    if (!provinceCode) {
      this.districts = [];
      this.profileForm.get('districtCode')?.setValue(null);
      return;
    }
    this.provinceService.getDistrictsByProvince(provinceCode).subscribe(data => {
      this.districts = data;
    });
  }


  onProvinceChange(event: any): void {
    const provinceCode = event.target.value;
    this.loadDistricts(Number(provinceCode));
    this.profileForm.get('districtCode')?.setValue(null); // Reset quận/huyện
  }


  toggleEditMode(): void {
    if (this.editMode) {
      this.profileForm.patchValue({
        fullName: this.user?.fullName || '',
        email: this.user?.email || '',
        phone: this.user?.phone || '',
        cccd: this.tenant?.cccd || '',
        dateOfBirth: this.tenant?.dateOfBirth || '',
        provinceCode: this.tenant?.provinceCode || null,
        districtCode: this.tenant?.districtCode || null,
        address: this.tenant?.address || ''
      });
      if (this.tenant?.provinceCode) {
        this.loadDistricts(this.tenant.provinceCode);
      }
    }
    this.editMode = !this.editMode;
  }

  async onSave(): Promise<void> {
    if (this.profileForm.invalid || !this.user || !this.tenant) { 
      Object.keys(this.profileForm.controls).forEach(key => {
        this.profileForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.saving = true;
    const formValue = this.profileForm.value;

    const updateData = {
      cccd: formValue.cccd,
      dateOfBirth: formValue.dateOfBirth,
      provinceCode: formValue.provinceCode,
      districtCode: formValue.districtCode,
      address: formValue.address,

      user: {
        id: this.user.id,
        fullName: formValue.fullName,
        email: formValue.email,
        phone: formValue.phone,
        status: this.user.status, 
        username: this.user.username, 
        role: this.user.role
      }
    };

    this.tenantService.update(this.tenant.id, updateData).subscribe({
      next: (updatedTenant) => {
        this.editMode = false;
        this.tenant = updatedTenant;
        if (this.user) {
          this.user.fullName = formValue.fullName;
          this.user.email = formValue.email;
          this.user.phone = formValue.phone;

          this.authService.updateUserInfo({
            fullName: formValue.fullName,
            email: formValue.email,
          });
        }
        this.tenant = updatedTenant;   
      },
      error: (err) => {
        console.error('Lỗi khi cập nhật:', err);
      },
      complete: () => {
        this.saving = false;
      }
    });
  }


  formatCurrency(value: number): string {
    if (value == null) return '0 VND';
    return new Intl.NumberFormat('vi-VN', { 
      style: 'currency', 
      currency: 'VND' 
    }).format(value);
  }

  formatDate(dateStr: string | undefined | null): string {
    if (!dateStr) return 'Chưa cập nhật'; 
    const date = new Date(dateStr);
    return date.toLocaleDateString('vi-VN');
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }
}