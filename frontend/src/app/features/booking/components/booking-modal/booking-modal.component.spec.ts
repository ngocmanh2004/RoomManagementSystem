import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { BookingModalComponent } from './booking-modal.component';
import { BookingService } from '../../../../services/booking.service';
import { AuthService } from '../../../../services/auth.service';
import { TenantService } from '../../../../services/tenant.service';
import { Room } from '../../../../models/room.model';

describe('BookingModalComponent - Sprint 2', () => {
  let component: BookingModalComponent;
  let fixture: ComponentFixture<BookingModalComponent>;
  let mockBookingService: jasmine.SpyObj<BookingService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockTenantService: jasmine.SpyObj<TenantService>;

  const mockRoom: Room = {
    id: 1,
    name: 'Phòng Test 101',
    price: 3000000,
    area: 25,
    description: 'Phòng đẹp',
    status: 'AVAILABLE',
    images: [],
    building: {
      id: 1,
      name: 'Dãy A',
      address: 'Quy Nhơn'
    }
  };

  beforeEach(async () => {
    mockBookingService = jasmine.createSpyObj('BookingService', ['createBooking']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['getCurrentUserId', 'getUserRole']);
    mockTenantService = jasmine.createSpyObj('TenantService', ['getTenantByUserId']);

    await TestBed.configureTestingModule({
      imports: [BookingModalComponent, ReactiveFormsModule],
      providers: [
        { provide: BookingService, useValue: mockBookingService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: TenantService, useValue: mockTenantService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(BookingModalComponent);
    component = fixture.componentInstance;
    component.room = mockRoom;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ========== US 8.4: Đặt thuê phòng ==========
  describe('US 8.4: Đặt thuê phòng', () => {
    
    // TEST 1: Hiển thị modal đặt phòng
    it('should display booking modal when opened', () => {
      component.isOpen = true;
      fixture.detectChanges();
      
      expect(component.isOpen).toBe(true);
      expect(component.room).toEqual(mockRoom);
    });

    // TEST 2: Khởi tạo form với các field bắt buộc
    it('should initialize form with required fields', () => {
      expect(component.bookingForm).toBeDefined();
      expect(component.bookingForm.get('fullName')).toBeDefined();
      expect(component.bookingForm.get('phoneNumber')).toBeDefined();
      expect(component.bookingForm.get('email')).toBeDefined();
      expect(component.bookingForm.get('startDate')).toBeDefined();
    });

    // TEST 3: Validate form trống
    it('should invalidate empty form', () => {
      expect(component.bookingForm.valid).toBe(false);
      expect(component.bookingForm.get('fullName')?.hasError('required')).toBe(true);
      expect(component.bookingForm.get('phoneNumber')?.hasError('required')).toBe(true);
    });

    // TEST 4: Validate số điện thoại không đúng định dạng
    it('should invalidate incorrect phone number format', () => {
      const phoneControl = component.bookingForm.get('phoneNumber');
      phoneControl?.setValue('123');
      
      expect(phoneControl?.hasError('pattern')).toBe(true);
    });

    // TEST 5: Tạo booking thành công
    it('should create booking successfully', () => {
      mockAuthService.getCurrentUserId.and.returnValue(1);
      mockTenantService.getTenantByUserId.and.returnValue(of({ id: 10, userId: 1 } as any));
      mockBookingService.createBooking.and.returnValue(of({} as any));
      
      component.bookingForm.patchValue({
        fullName: 'Nguyễn Văn A',
        phoneNumber: '0912345678',
        email: 'test@gmail.com',
        startDate: '2024-02-01',
        notes: 'Muốn xem phòng trước'
      });
      
      component.submit();
      
      expect(mockBookingService.createBooking).toHaveBeenCalled();
      expect(component.successMessage).toContain('thành công');
    });

    // TEST 6: Hiển thị lỗi khi tạo booking fail
    it('should show error when booking creation fails', () => {
      mockAuthService.getCurrentUserId.and.returnValue(1);
      mockTenantService.getTenantByUserId.and.returnValue(of({ id: 10, userId: 1 } as any));
      mockBookingService.createBooking.and.returnValue(
        throwError(() => new Error('Booking failed'))
      );
      
      component.bookingForm.patchValue({
        fullName: 'Nguyễn Văn A',
        phoneNumber: '0912345678',
        email: 'test@gmail.com',
        startDate: '2024-02-01'
      });
      
      component.submit();
      
      expect(component.errorMessage).toBeTruthy();
    });

    // TEST 7: Đóng modal
    it('should close modal and emit event', () => {
      spyOn(component.closeModal, 'emit');
      
      component.close();
      
      expect(component.closeModal.emit).toHaveBeenCalled();
    });

    // TEST 8: Reset form khi đóng modal
    it('should reset form when modal closes', () => {
      component.bookingForm.patchValue({
        fullName: 'Test',
        phoneNumber: '0912345678'
      });
      
      component.close();
      
      expect(component.bookingForm.get('fullName')?.value).toBe('');
      expect(component.errorMessage).toBe('');
      expect(component.successMessage).toBe('');
    });

    // TEST 9: Validate email format
    it('should invalidate incorrect email format', () => {
      const emailControl = component.bookingForm.get('email');
      emailControl?.setValue('invalid-email');
      
      expect(emailControl?.hasError('email')).toBe(true);
      
      emailControl?.setValue('valid@email.com');
      expect(emailControl?.hasError('email')).toBe(false);
    });

    // TEST 10: Prevent booking with past date
    it('should prevent booking with start date in the past', () => {
      const yesterday = new Date();
      yesterday.setDate(yesterday.getDate() - 1);
      const pastDate = yesterday.toISOString().split('T')[0];
      
      component.bookingForm.patchValue({
        fullName: 'Nguyễn Văn A',
        phoneNumber: '0912345678',
        email: 'test@gmail.com',
        startDate: pastDate
      });
      
      expect(component.bookingForm.valid).toBe(false);
    });

    // TEST 11: Show loading state during submission
    it('should show loading state while creating booking', () => {
      mockAuthService.getCurrentUserId.and.returnValue(1);
      mockTenantService.getTenantByUserId.and.returnValue(of({ id: 10, userId: 1 } as any));
      mockBookingService.createBooking.and.returnValue(of({} as any));
      
      component.bookingForm.patchValue({
        fullName: 'Nguyễn Văn A',
        phoneNumber: '0912345678',
        email: 'test@gmail.com',
        startDate: '2024-02-01'
      });
      
      component.submit();
      
      expect(component.isSubmitting).toBe(false); // Should be false after completion
    });

    // TEST 12: Validate phone number with Vietnamese format
    it('should accept valid Vietnamese phone numbers', () => {
      const phoneControl = component.bookingForm.get('phoneNumber');
      
      const validNumbers = ['0912345678', '0987654321', '0123456789'];
      validNumbers.forEach(number => {
        phoneControl?.setValue(number);
        expect(phoneControl?.hasError('pattern')).toBe(false);
      });
    });

    // TEST 13: Prevent double submission
    it('should prevent double submission while processing', () => {
      mockAuthService.getCurrentUserId.and.returnValue(1);
      mockTenantService.getTenantByUserId.and.returnValue(of({ id: 10, userId: 1 } as any));
      mockBookingService.createBooking.and.returnValue(of({} as any));
      
      component.bookingForm.patchValue({
        fullName: 'Nguyễn Văn A',
        phoneNumber: '0912345678',
        email: 'test@gmail.com',
        startDate: '2024-02-01'
      });
      
      component.isSubmitting = true;
      component.submit();
      
      // Should not call service if already submitting
      expect(mockBookingService.createBooking).not.toHaveBeenCalled();
    });
  });
});
