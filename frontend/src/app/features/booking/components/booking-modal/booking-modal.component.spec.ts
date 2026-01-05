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
    address: 'Quy Nhơn',
    description: 'Phòng đẹp',
    maxOccupants: 2,
    status: 'AVAILABLE',
    images: []
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
      
      component.submitBooking();
      
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
      
      component.submitBooking();
      
      expect(component.errorMessage).toBeTruthy();
    });

    // TEST 7: Đóng modal
    it('should close modal and emit event', () => {
      spyOn(component.closeModal, 'emit');
      
      component.onClose();
      
      expect(component.closeModal.emit).toHaveBeenCalled();
    });

    // TEST 8: Reset form khi đóng modal
    it('should reset form when modal closes', () => {
      component.bookingForm.patchValue({
        fullName: 'Test',
        phoneNumber: '0912345678'
      });
      
      component.onClose();
      
      expect(component.bookingForm.get('fullName')?.value).toBe('');
      expect(component.errorMessage).toBe('');
      expect(component.successMessage).toBe('');
    });
  });
});
