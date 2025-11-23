import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BookingService } from '../../../../services/booking.service';
import { AuthService } from '../../../../services/auth.service';
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
    
    if (!this.currentUserId) {
      this.errorMessage = 'Bạn cần đăng nhập để đặt thuê phòng';
    }
    
    if (this.userRole !== 2) {
      this.errorMessage = 'Chỉ khách thuê có thể đặt phòng';
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['isOpen'] && this.isOpen && this.room) {
      this.loadUserData();
    }
  }

  initializeForm() {
    this.bookingForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      cccd: ['', [Validators.required, Validators.pattern(/^[0-9]{9,12}$/)]],
      phone: ['', [Validators.required, Validators.pattern(/^0[0-9]{9}$/)]],
      address: ['', [Validators.required, Validators.minLength(5)]],
      startDate: ['', Validators.required],
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

    this.tenantService.getTenantByUserId(this.currentUserId).subscribe({
      next: (tenant: any) => {
        const currentUser = this.authService.getCurrentUser();
        
        this.bookingForm.patchValue({
          fullName: currentUser?.fullName || '',
          cccd: tenant?.cccd || '',
          phone: currentUser?.phone || '',
          address: tenant?.address || ''
        });
        
        this.isLoadingUserData = false;
      },
      error: (error: any) => {
        console.error('Error loading tenant data:', error);
        const currentUser = this.authService.getCurrentUser();
        
        this.bookingForm.patchValue({
          fullName: currentUser?.fullName || '',
          phone: currentUser?.phone || '',
          address: ''
        });
        
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
    if (this.userRole !== 2) {
      this.errorMessage = 'Chỉ khách thuê có thể đặt phòng';
      return;
    }

    if (!this.bookingForm.valid) {
      this.errorMessage = 'Vui lòng điền đầy đủ thông tin hợp lệ';
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

    if (formValue.deposit < 0) {
      this.errorMessage = 'Số tiền cọc phải là số dương';
      return;
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

    this.bookingService.createBooking(bookingRequest).subscribe({
      next: (response: any) => {
        console.log('Booking created successfully:', response);
        this.successMessage = 'Yêu cầu đặt thuê phòng đã được gửi thành công!';
        this.isSubmitting = false;
        
        setTimeout(() => {
          this.bookingSuccess.emit();
          this.close();
        }, 1500);
      },
      error: (error: any) => {
        console.error('Booking error:', error);
        const errorMsg = error?.error?.message || error?.message || 'Lỗi đặt thuê phòng. Vui lòng thử lại.';
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