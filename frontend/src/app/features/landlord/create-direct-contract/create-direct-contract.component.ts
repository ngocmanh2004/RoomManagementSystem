import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LandlordBookingService } from '../../../services/landlord-booking.service';
import { DirectContractRequest } from '../../../models/direct-contract.model';
import { Contract } from '../../../models/contract.model';

@Component({
  selector: 'app-create-direct-contract',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-direct-contract.component.html',
  styleUrl: './create-direct-contract.component.css'
})
export class CreateDirectContractComponent implements OnInit {
  contractForm!: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  // Data từ API (giả sử bạn đã có service để lấy)
  availableRooms: any[] = [];
  tenants: any[] = [];
  
  isLoadingRooms = false;
  isLoadingTenants = false;

  constructor(
    private fb: FormBuilder,
    private landlordBookingService: LandlordBookingService,
    private router: Router
  ) {}

  ngOnInit() {
    console.log('🟢 CreateDirectContractComponent initialized');
    console.log('🟢 Current route:', this.router.url);
    this.initForm();
    this.loadAvailableRooms();
    this.loadTenants();
  }

  initForm() {
    const today = new Date().toISOString().split('T')[0];
    
    this.contractForm = this.fb.group({
      roomId: [null, Validators.required],
      tenantId: [null, Validators.required],
      fullName: ['', [Validators.required, Validators.maxLength(100)]],
      cccd: ['', [Validators.required, Validators.maxLength(20)]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10,11}$/)]],
      address: ['', [Validators.required, Validators.maxLength(255)]],
      startDate: [today, Validators.required],
      endDate: [''],
      deposit: [0, [Validators.required, Validators.min(0)]],
      notes: ['']
    });

    // Auto-fill thông tin khi chọn tenant
    this.contractForm.get('tenantId')?.valueChanges.subscribe(tenantId => {
      if (tenantId) {
        this.onTenantSelected(Number(tenantId));
      }
    });
  }

  loadAvailableRooms() {
    this.isLoadingRooms = true;
    
    this.landlordBookingService.getMyAvailableRooms().subscribe({
      next: (rooms) => {
        this.availableRooms = rooms.map(room => ({
          id: room.id,
          name: room.name,
          price: room.price,
          buildingName: room.buildingName || 'N/A'
        }));
        this.isLoadingRooms = false;
        console.log('✅ Loaded rooms from API:', this.availableRooms);
      },
      error: (err) => {
        console.error('❌ Error loading rooms:', err);
        this.errorMessage = 'Không thể tải danh sách phòng. Vui lòng thử lại.';
        this.isLoadingRooms = false;
      }
    });
  }

  loadTenants() {
    this.isLoadingTenants = true;
    
    this.landlordBookingService.getAvailableTenants().subscribe({
      next: (tenants) => {
        this.tenants = tenants.map(tenant => ({
          id: tenant.id,
          fullName: tenant.user?.fullName || tenant.fullName || 'N/A',
          cccd: tenant.cccd || '',
          phone: tenant.user?.phone || tenant.phone || '',
          address: tenant.address || ''
        }));
        this.isLoadingTenants = false;
        console.log('✅ Loaded tenants from API:', this.tenants);
      },
      error: (err) => {
        console.error('❌ Error loading tenants:', err);
        this.errorMessage = 'Không thể tải danh sách khách thuê. Vui lòng thử lại.';
        this.isLoadingTenants = false;
      }
    });
  }

  onTenantSelected(tenantId: number) {
    const tenant = this.tenants.find(t => t.id === tenantId);
    if (tenant) {
      this.contractForm.patchValue({
        fullName: tenant.fullName,
        cccd: tenant.cccd,
        phone: tenant.phone,
        address: tenant.address
      });
    }
  }

  onSubmit() {
    if (this.contractForm.invalid) {
      Object.keys(this.contractForm.controls).forEach(key => {
        this.contractForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    // ✅ Parse số từ form value
    const formData: DirectContractRequest = {
      roomId: Number(this.contractForm.value.roomId),
      tenantId: Number(this.contractForm.value.tenantId),
      fullName: this.contractForm.value.fullName,
      cccd: this.contractForm.value.cccd,
      phone: this.contractForm.value.phone,
      address: this.contractForm.value.address,
      startDate: this.contractForm.value.startDate,
      endDate: this.contractForm.value.endDate || undefined,
      deposit: Number(this.contractForm.value.deposit),
      notes: this.contractForm.value.notes || undefined
    };

    // 🔍 DEBUG: Log payload trước khi gửi
    console.log('📤 Sending payload:', JSON.stringify(formData, null, 2));
    console.log('Room ID type:', typeof formData.roomId, formData.roomId);
    console.log('Tenant ID type:', typeof formData.tenantId, formData.tenantId);
    console.log('Deposit type:', typeof formData.deposit, formData.deposit);

    // ✅ Validate trước khi gửi
    if (isNaN(formData.roomId) || isNaN(formData.tenantId)) {
      this.errorMessage = 'Vui lòng chọn phòng và khách thuê hợp lệ';
      this.isSubmitting = false;
      return;
    }
    
    this.landlordBookingService.createDirectContract(formData).subscribe({
      next: (contract: Contract) => {
        console.log('✅ Success:', contract);
        this.successMessage = 'Tạo hợp đồng thành công! Phòng đã chuyển sang trạng thái đang thuê.';
        this.isSubmitting = false;
        
        // Chuyển về trang danh sách sau 2s
        setTimeout(() => {
          this.router.navigate(['/landlord/bookings']);
        }, 2000);
      },
      error: (error: Error) => {
        console.error('❌ Error details:', error);
        this.errorMessage = error.message || 'Có lỗi xảy ra khi tạo hợp đồng';
        this.isSubmitting = false;
      }
    });
  }

  onCancel() {
    this.router.navigate(['/landlord/bookings']);
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('vi-VN').format(price);
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.contractForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getErrorMessage(fieldName: string): string {
    const field = this.contractForm.get(fieldName);
    if (!field || !field.errors) return '';

    if (field.errors['required']) return 'Trường này không được để trống';
    if (field.errors['pattern']) return 'Số điện thoại không hợp lệ (10-11 số)';
    if (field.errors['maxLength']) return `Không được quá ${field.errors['maxLength'].requiredLength} ký tự`;
    if (field.errors['min']) return 'Giá trị phải lớn hơn hoặc bằng 0';

    return 'Dữ liệu không hợp lệ';
  }
}