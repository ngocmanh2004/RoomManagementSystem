import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BookingService } from '../../../../services/booking.service';
import { AuthService } from '../../../../services/auth.service';  // ✅ Sửa path
import { TenantService } from '../../../../services/tenant.service';
import { BookingRequest } from '../../../../models/booking.model';
import { Room } from '../../../../models/room.model';

@Component({
  selector: 'app-booking-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './booking-modal.component.html',
  styleUrl: './booking-modal.component.css'
})
export class BookingModalComponent implements OnInit {
  @Input() room: Room | null = null;
  @Input() isOpen = false;
  @Output() closeModal = new EventEmitter<void>();
  @Output() bookingSuccess = new EventEmitter<void>();

  bookingForm!: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  currentUserId: number | null = null;
  userRole: number | null = null;
  isLoadingUserData = false;

  constructor(
    private fb: FormBuilder,
    private bookingService: BookingService,
    private authService: AuthService,
    private tenantService: TenantService
  ) {
    this.initializeForm();
  }

  ngOnInit() {
    this.currentUserId = this.authService.getCurrentUserId();
    this.userRole = this.authService.getUserRole();
    
    console.log('📌 Modal init - userId:', this.currentUserId, 'role:', this.userRole);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['isOpen'] && this.isOpen && this.room) {
      this.errorMessage = '';
      this.successMessage = '';
      this.currentUserId = this.authService.getCurrentUserId();
      this.userRole = this.authService.getUserRole();
      
      console.log('📌 Modal opened - userId:', this.currentUserId, 'role:', this.userRole);
      
      if (this.currentUserId) {
        this.loadUserData();
      }
    }
  }

  initializeForm() {
    const today = new Date().toISOString().split('T')[0];
    
    this.bookingForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      cccd: ['', [Validators.required, Validators.pattern(/^[0-9]{9,12}$/)]],
      phone: ['', [Validators.required, Validators.pattern(/^0[0-9]{9}$/)]],
      address: ['', [Validators.required, Validators.minLength(5)]],
      startDate: [today, Validators.required],
      endDate: [''],
      deposit: [0, [Validators.required, Validators.min(0)]],
      notes: ['']
    });
  }

  loadUserData() {
  if (!this.currentUserId) {
    this.errorMessage = 'Không thể tải thông tin người dùng';
    return;
  }

  this.isLoadingUserData = true;
  this.errorMessage = '';

  const currentUser = this.authService.getCurrentUser();
  console.log('📌 Current user from AuthService:', currentUser);
  console.log('📌 Current user fullName:', currentUser?.fullName);
  console.log('📌 Current user phone:', currentUser?.phone);

  this.tenantService.getTenantByUserId(this.currentUserId).subscribe({
    next: (response: any) => {
      console.log('✅ Tenant API raw response:', response);
      console.log('✅ Response type:', typeof response);
      console.log('✅ Response keys:', Object.keys(response || {}));
      
      console.log('✅ Full response object:', JSON.stringify(response, null, 2));
      
      let tenant = response;
      
      if (response?.data) {
        tenant = response.data;
        console.log('✅ Unwrapped tenant from response.data:', tenant);
      }
      
      console.log('📋 Tenant fields:');
      console.log('  - id:', tenant?.id);
      console.log('  - userId:', tenant?.userId);
      console.log('  - cccd:', tenant?.cccd);
      console.log('  - phone:', tenant?.phone);
      console.log('  - user.phone:', tenant?.user?.phone); // ✅ Thêm dòng này
      console.log('  - address:', tenant?.address);
      console.log('  - fullName:', tenant?.fullName);
      console.log('  - user.fullName:', tenant?.user?.fullName); // ✅ Thêm dòng này
      console.log('  - ALL tenant object:', JSON.stringify(tenant, null, 2));
      
      // ✅ FIX: Lấy từ tenant.user.phone và tenant.user.fullName
      const formValues = {
        fullName: tenant?.user?.fullName || currentUser?.fullName || tenant?.fullName || '',
        cccd: tenant?.cccd || '',
        phone: tenant?.user?.phone || currentUser?.phone || tenant?.phone || '',
        address: tenant?.address || ''
      };
      
      console.log('📝 Values to patch into form:', formValues);
      
      this.bookingForm.patchValue(formValues);
      
      console.log('✅ Form value after patch:', this.bookingForm.value);
      console.log('✅ Form control values:');
      console.log('  - fullName:', this.bookingForm.get('fullName')?.value);
      console.log('  - cccd:', this.bookingForm.get('cccd')?.value);
      console.log('  - phone:', this.bookingForm.get('phone')?.value);
      console.log('  - address:', this.bookingForm.get('address')?.value);
      
      this.isLoadingUserData = false;
    },
    error: (error: any) => {
      console.error('❌ Error loading tenant data:', error);
      
      if (currentUser) {
        this.bookingForm.patchValue({
          fullName: currentUser.fullName || '',
          phone: currentUser.phone || '',
          cccd: '',
          address: ''
        });
      }
      
      this.isLoadingUserData = false;
    }
  });
}

  resetForm() {
    this.loadUserData();
  }

  close() {
    this.closeModal.emit();
  }

  submit() {
    this.currentUserId = this.authService.getCurrentUserId();
    this.userRole = this.authService.getUserRole();
    const token = localStorage.getItem('accessToken');
    
    console.log('========== BOOKING MODAL SUBMIT ==========');
    console.log('📌 Current User ID:', this.currentUserId);
    console.log('📌 User Role:', this.userRole);
    console.log('📌 Access Token:', token ? 'EXISTS' : 'MISSING');
    console.log('📌 Form Valid:', this.bookingForm.valid);
    console.log('📌 Form Value:', this.bookingForm.value);
    
    if (!token) {
      this.errorMessage = 'Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.';
      return;
    }
    
    if (!this.currentUserId) {
      this.errorMessage = 'Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.';
      return;
    }
    
    if (this.userRole !== 2) {
      this.errorMessage = 'Chỉ khách thuê có thể đặt phòng';
      return;
    }

    if (!this.bookingForm.valid) {
      this.errorMessage = 'Vui lòng điền đầy đủ thông tin hợp lệ';
      this.bookingForm.markAllAsTouched();
      return;
    }

    if (!this.room || !this.room.id) {
      this.errorMessage = 'Thông tin phòng không hợp lệ';
      return;
    }

    const formValue = this.bookingForm.value;
    const startDate = new Date(formValue.startDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (startDate < today) {
      this.errorMessage = 'Ngày bắt đầu phải từ hôm nay trở đi';
      return;
    }

    if (formValue.endDate) {
      const endDate = new Date(formValue.endDate);
      if (endDate <= startDate) {
        this.errorMessage = 'Ngày kết thúc phải sau ngày bắt đầu';
        return;
      }
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const bookingRequest: BookingRequest = {
      roomId: this.room.id,
      startDate: this.formatDate(formValue.startDate),
      endDate: formValue.endDate ? this.formatDate(formValue.endDate) : undefined,
      deposit: formValue.deposit || 0,
      notes: formValue.notes || '',
      fullName: formValue.fullName,
      cccd: formValue.cccd,
      phone: formValue.phone,
      address: formValue.address
    };

    console.log('📤 Sending booking request:', bookingRequest);

    this.bookingService.createBooking(bookingRequest).subscribe({
      next: (response: any) => {
        console.log('✅ Booking response:', response);
        this.successMessage = 'Yêu cầu đặt thuê phòng đã được gửi thành công!';
        this.isSubmitting = false;
        
        setTimeout(() => {
          this.bookingSuccess.emit();
          this.close();
        }, 1500);
      },
      error: (error: any) => {
        console.error('❌ Booking error:', error);
        
        let errorMsg = 'Lỗi đặt thuê phòng. Vui lòng thử lại.';
        
        if (error.status === 401) {
          errorMsg = 'Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.';
        } else if (error.status === 403) {
          errorMsg = 'Bạn không có quyền thực hiện thao tác này.';
        } else if (error.error?.message) {
          errorMsg = error.error.message;
        } else if (error.message) {
          errorMsg = error.message;
        }
        
        this.errorMessage = errorMsg;
        this.isSubmitting = false;
      }
    });
  }

  formatDate(date: string): string {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  get fullNameError(): string {
    const control = this.bookingForm.get('fullName');
    if (control?.hasError('required')) return 'Tên không được để trống';
    if (control?.hasError('minlength')) return 'Tên phải có ít nhất 3 ký tự';
    return '';
  }

  get cccdError(): string {
    const control = this.bookingForm.get('cccd');
    if (control?.hasError('required')) return 'CCCD không được để trống';
    if (control?.hasError('pattern')) return 'CCCD phải là 9-12 số';
    return '';
  }

  get phoneError(): string {
    const control = this.bookingForm.get('phone');
    if (control?.hasError('required')) return 'Số điện thoại không được để trống';
    if (control?.hasError('pattern')) return 'Số điện thoại không hợp lệ (0xxxxxxxxx)';
    return '';
  }

  get addressError(): string {
    const control = this.bookingForm.get('address');
    if (control?.hasError('required')) return 'Địa chỉ không được để trống';
    if (control?.hasError('minlength')) return 'Địa chỉ phải có ít nhất 5 ký tự';
    return '';
  }

  get startDateError(): string {
    const control = this.bookingForm.get('startDate');
    if (control?.hasError('required')) return 'Ngày bắt đầu không được để trống';
    return '';
  }

  get depositError(): string {
    const control = this.bookingForm.get('deposit');
    if (control?.hasError('required')) return 'Số tiền cọc không được để trống';
    if (control?.hasError('min')) return 'Số tiền cọc phải >= 0';
    return '';
  }
}