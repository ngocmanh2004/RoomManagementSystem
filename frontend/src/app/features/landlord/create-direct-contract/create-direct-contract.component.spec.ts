import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CreateDirectContractComponent } from './create-direct-contract.component';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LandlordBookingService } from '../../../services/landlord-booking.service';
import { of, throwError } from 'rxjs';
import { CommonModule } from '@angular/common';

/* ================= MOCK DATA ================= */

const mockRooms = [
  { id: 1, name: 'Phòng 101', price: 3000000, buildingName: 'A' }
];

const mockTenants = [
  {
    id: 1,
    fullName: 'Nguyễn Văn A',
    cccd: '0123456789',
    phone: '0987654321',
    address: 'Hà Nội'
  }
];

const mockContractResponse = {
  id: 99
} as any;

/* ================= MOCK SERVICE ================= */

class MockLandlordBookingService {
  getMyAvailableRooms() {
    return of(mockRooms);
  }

  getAvailableTenants() {
    return of(mockTenants);
  }

  createDirectContract() {
    return of(mockContractResponse);
  }
}

/* ================= MOCK ROUTER ================= */

class MockRouter {
  navigate = jasmine.createSpy('navigate');
  url = '/landlord/create-direct-contract';
}

/* ================= TEST SUITE ================= */

describe('CreateDirectContractComponent', () => {
  let component: CreateDirectContractComponent;
  let fixture: ComponentFixture<CreateDirectContractComponent>;
  let service: LandlordBookingService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        ReactiveFormsModule,
        CreateDirectContractComponent
      ],
      providers: [
        { provide: LandlordBookingService, useClass: MockLandlordBookingService },
        { provide: Router, useClass: MockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateDirectContractComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(LandlordBookingService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  /* ========== BASIC ========== */

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize flags correctly', () => {
    expect(component.isSubmitting).toBeFalse();
    expect(component.errorMessage).toBe('');
    expect(component.successMessage).toBe('');
  });

  /* ========== ngOnInit ========== */

  it('should initialize form and load data on ngOnInit', () => {
    spyOn(component, 'initForm');
    spyOn(component, 'loadAvailableRooms');
    spyOn(component, 'loadTenants');

    component.ngOnInit();

    expect(component.initForm).toHaveBeenCalled();
    expect(component.loadAvailableRooms).toHaveBeenCalled();
    expect(component.loadTenants).toHaveBeenCalled();
  });

  /* ========== FORM ========== */

  it('should create form with default values', () => {
    expect(component.contractForm).toBeTruthy();
    expect(component.contractForm.get('deposit')?.value).toBe(0);
    expect(component.contractForm.get('roomId')?.valid).toBeFalse();
  });

  it('should mark form invalid when required fields missing', () => {
    component.contractForm.patchValue({
      fullName: '',
      phone: ''
    });
    expect(component.contractForm.invalid).toBeTrue();
  });

  it('should validate phone pattern', () => {
    component.contractForm.get('phone')?.setValue('123');
    expect(component.contractForm.get('phone')?.invalid).toBeTrue();
  });

  /* ========== LOAD ROOMS ========== */

  it('should load available rooms', () => {
    component.loadAvailableRooms();
    expect(component.availableRooms.length).toBe(1);
    expect(component.availableRooms[0].name).toBe('Phòng 101');
  });

  it('should handle error when loading rooms', () => {
    spyOn(service, 'getMyAvailableRooms').and.returnValue(throwError(() => new Error()));
    component.loadAvailableRooms();
    expect(component.errorMessage).toContain('Không thể tải danh sách phòng');
  });

  /* ========== LOAD TENANTS ========== */

  it('should load tenants', () => {
    component.loadTenants();
    expect(component.tenants.length).toBe(1);
    expect(component.tenants[0].fullName).toBe('Nguyễn Văn A');
  });

  it('should auto-fill tenant info when tenant selected', () => {
    component.tenants = mockTenants;
    component.onTenantSelected(1);

    expect(component.contractForm.value.fullName).toBe('Nguyễn Văn A');
    expect(component.contractForm.value.phone).toBe('0987654321');
  });

  /* ========== SUBMIT ========== */

  it('should stop submit if form invalid', () => {
    spyOn(service, 'createDirectContract');
    component.onSubmit();
    expect(service.createDirectContract).not.toHaveBeenCalled();
  });

  it('should navigate after success', fakeAsync(() => {
    component.contractForm.patchValue({
      roomId: 1,
      tenantId: 1,
      fullName: 'Nguyễn Văn A',
      cccd: '0123456789',
      phone: '0987654321',
      address: 'Hà Nội',
      startDate: '2024-01-01',
      deposit: 1000000
    });

    component.onSubmit();
    tick(2000);

    expect(router.navigate).toHaveBeenCalledWith(['/landlord/bookings']);
  }));

  it('should handle submit error', () => {
    spyOn(service, 'createDirectContract')
      .and.returnValue(throwError(() => new Error('Server error')));

    component.contractForm.patchValue({
      roomId: 1,
      tenantId: 1,
      fullName: 'Nguyễn Văn A',
      cccd: '0123456789',
      phone: '0987654321',
      address: 'Hà Nội',
      startDate: '2024-01-01',
      deposit: 1000000
    });

    component.onSubmit();
    expect(component.errorMessage).toContain('Server error');
  });

  /* ========== UTIL METHODS ========== */

  it('should format price correctly', () => {
    expect(component.formatPrice(1000000)).toBe('1.000.000');
  });

  it('should detect invalid field', () => {
    const control = component.contractForm.get('fullName');
    control?.markAsTouched();
    expect(component.isFieldInvalid('fullName')).toBeTrue();
  });

  it('should return correct error message', () => {
    const control = component.contractForm.get('phone');
    control?.setErrors({ pattern: true });
    control?.markAsTouched();

    expect(component.getErrorMessage('phone')).toContain('Số điện thoại');
  });

  /* ========== CANCEL ========== */

  it('should navigate on cancel', () => {
    component.onCancel();
    expect(router.navigate).toHaveBeenCalledWith(['/landlord/bookings']);
  });
});
