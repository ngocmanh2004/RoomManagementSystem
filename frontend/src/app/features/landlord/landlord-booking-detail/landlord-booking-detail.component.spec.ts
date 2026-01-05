import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LandlordBookingDetailComponent } from './landlord-booking-detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { LandlordBookingService } from '../../../services/landlord-booking.service';
import { of, throwError } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

/* ================= MOCK DATA ================= */

const mockContract = {
  id: 1,
  contractCodeGenerated: 'HD001',
  status: 'PENDING',
  fullName: 'Nguyễn Văn A',
  phone: '0987654321',
  roomName: 'Phòng 101',
  buildingName: 'A',
  startDate: '2024-01-01',
  endDate: '2024-12-31',
  monthlyRentCalculated: 3000000,
  deposit: 1000000,
  totalInitialCost: 4000000,
  createdAt: '2024-01-01'
} as any;

/* ================= MOCK SERVICE ================= */

class MockLandlordBookingService {
  getContractDetail() {
    return of(mockContract);
  }

  approveContract() {
    return of({ ...mockContract, status: 'APPROVED' });
  }

  rejectContract() {
    return of({ ...mockContract, status: 'REJECTED' });
  }

  terminateContract() {
    return of({ ...mockContract, status: 'CANCELLED' });
  }

  extendContract() {
    return of({ ...mockContract, endDate: '2025-12-31' });
  }
}

/* ================= MOCK ROUTER ================= */

class MockRouter {
  navigate = jasmine.createSpy('navigate');
}

/* ================= MOCK ROUTE ================= */

const mockActivatedRoute = {
  snapshot: {
    paramMap: {
      get: () => '1'
    }
  }
};

/* ================= TEST SUITE ================= */

describe('LandlordBookingDetailComponent', () => {
  let component: LandlordBookingDetailComponent;
  let fixture: ComponentFixture<LandlordBookingDetailComponent>;
  let service: LandlordBookingService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        FormsModule,
        LandlordBookingDetailComponent
      ],
      providers: [
        { provide: LandlordBookingService, useClass: MockLandlordBookingService },
        { provide: Router, useClass: MockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LandlordBookingDetailComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(LandlordBookingService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  /* ========== BASIC ========== */

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  /* ========== ngOnInit ========== */

  it('should load contract detail on init', () => {
    expect(component.contract).toBeTruthy();
    expect(component.contract?.id).toBe(1);
  });

  it('should handle error when loading contract detail', () => {
    spyOn(service, 'getContractDetail')
      .and.returnValue(throwError(() => new Error('Load error')));

    component.loadContractDetail(1);
    expect(component.errorMessage).toBe('Load error');
  });

  /* ========== APPROVE ========== */

  it('should open and close approve modal', () => {
    component.openApproveModal();
    expect(component.showApproveModal).toBeTrue();

    component.closeApproveModal();
    expect(component.showApproveModal).toBeFalse();
  });

  it('should approve contract successfully', fakeAsync(() => {
    component.contract = mockContract;

    component.confirmApprove();
    tick();

    expect(component.contract?.status).toBe('APPROVED');
    expect(component.successMessage).toContain('Đã duyệt');
  }));

  /* ========== REJECT ========== */

  it('should open and close reject modal', () => {
    component.openRejectModal();
    expect(component.showRejectModal).toBeTrue();

    component.closeRejectModal();
    expect(component.showRejectModal).toBeFalse();
  });

  it('should not reject without reason', () => {
    spyOn(window, 'alert');
    component.contract = mockContract;
    component.rejectionReason = '';

    component.confirmReject();
    expect(window.alert).toHaveBeenCalled();
  });

  it('should reject contract successfully', fakeAsync(() => {
    component.contract = mockContract;
    component.rejectionReason = 'Không phù hợp';

    component.confirmReject();
    tick();

    expect(component.contract?.status).toBe('REJECTED');
    expect(component.successMessage).toContain('từ chối');
  }));

  /* ========== TERMINATE ========== */

  it('should open and close terminate modal', () => {
    component.openTerminateModal();
    expect(component.showTerminateModal).toBeTrue();

    component.closeTerminateModal();
    expect(component.showTerminateModal).toBeFalse();
  });

  it('should terminate contract successfully', fakeAsync(() => {
    component.contract = { ...mockContract, status: 'ACTIVE' };

    component.confirmTerminate();
    tick();

    expect(component.contract?.status).toBe('CANCELLED');
    expect(component.successMessage).toContain('thanh lý');
  }));

  /* ========== EXTEND ========== */

  it('should open extend modal and set default end date', () => {
    component.contract = mockContract;
    component.openExtendModal();

    expect(component.showExtendModal).toBeTrue();
    expect(component.newEndDate).toBeTruthy();
  });

  it('should prevent extend if new date invalid', () => {
    spyOn(window, 'alert');
    component.contract = mockContract;
    component.newEndDate = '2000-01-01';

    component.confirmExtend();
    expect(window.alert).toHaveBeenCalled();
  });

  /* ========== UTIL METHODS ========== */

  it('should return correct status class', () => {
    expect(component.getStatusClass('ACTIVE')).toBe('status-active');
  });

  it('should return correct status label', () => {
    expect(component.getStatusLabel('PENDING')).toBe('Chờ duyệt');
  });

  it('should format price correctly', () => {
    expect(component.formatPrice(1000000)).toBe('1.000.000');
  });

  it('should check pending and active status', () => {
    component.contract = { ...mockContract, status: 'PENDING' };
    expect(component.isPending()).toBeTrue();

    component.contract = { ...mockContract, status: 'ACTIVE' };
    expect(component.isActive()).toBeTrue();
  });

  /* ========== NAVIGATION ========== */

  it('should navigate back', () => {
    component.goBack();
    expect(router.navigate).toHaveBeenCalledWith(['/landlord/bookings']);
  });
});
