import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LandlordBookingComponent } from './landlord-booking.component';
import { LandlordBookingService } from '../../../services/landlord-booking.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

/* ================= MOCK DATA ================= */

const mockContracts = [
  {
    id: 1,
    contractCodeGenerated: 'HD001',
    fullName: 'Nguyễn Văn A',
    phone: '0987654321',
    roomName: 'Phòng 101',
    startDate: '2024-01-01',
    endDate: '2024-12-31',
    monthlyRentCalculated: 3000000,
    status: 'ACTIVE'
  },
  {
    id: 2,
    contractCodeGenerated: 'HD002',
    fullName: 'Trần Thị B',
    phone: '0912345678',
    roomName: 'Phòng 102',
    startDate: '2023-01-01',
    endDate: null,
    monthlyRentCalculated: 2500000,
    status: 'EXPIRED'
  }
] as any;

const mockResponse = {
  content: mockContracts,
  totalPages: 1,
  totalElements: 2,
  number: 0
};

/* ================= MOCK SERVICE ================= */

class MockLandlordBookingService {
  getMyContracts(page: number, size: number, status: string) {
    return of(mockResponse);
  }
}

/* ================= TEST SUITE ================= */

describe('LandlordBookingComponent', () => {
  let component: LandlordBookingComponent;
  let fixture: ComponentFixture<LandlordBookingComponent>;
  let service: LandlordBookingService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CommonModule,
        FormsModule,
        RouterTestingModule,
        LandlordBookingComponent
      ],
      providers: [
        { provide: LandlordBookingService, useClass: MockLandlordBookingService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LandlordBookingComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(LandlordBookingService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  /* ========== BASIC ========== */

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize default values', () => {
    expect(component.currentPage).toBe(0);
    expect(component.pageSize).toBe(10);
    expect(component.isLoading).toBeFalse();
  });

  /* ========== ngOnInit ========== */

  it('should load contracts and stats on init', () => {
    spyOn(component, 'loadContracts');
    spyOn(component, 'loadAllContractsForStats');

    component.ngOnInit();

    expect(component.loadContracts).toHaveBeenCalled();
    expect(component.loadAllContractsForStats).toHaveBeenCalled();
  });

  /* ========== LOAD CONTRACTS ========== */

  it('should load contracts successfully', () => {
    component.loadContracts();

    expect(component.contracts.length).toBe(2);
    expect(component.filteredContracts.length).toBe(2);
    expect(component.totalElements).toBe(2);
    expect(component.isLoading).toBeFalse();
  });

  it('should handle error when loading contracts', () => {
    spyOn(service, 'getMyContracts')
      .and.returnValue(throwError(() => new Error('API error')));

    component.loadContracts();

    expect(component.errorMessage).toBe('API error');
    expect(component.isLoading).toBeFalse();
  });

  /* ========== STATISTICS ========== */

  it('should load all contracts for statistics', () => {
    component.loadAllContractsForStats();
    expect(component.getCountByStatus('ACTIVE')).toBe(1);
    expect(component.getCountByStatus('EXPIRED')).toBe(1);
  });

  /* ========== FILTER BY STATUS ========== */

  it('should reload contracts when status changes', () => {
    spyOn(component, 'loadContracts');
    component.onStatusChange();
    expect(component.currentPage).toBe(0);
    expect(component.loadContracts).toHaveBeenCalled();
  });

  /* ========== SEARCH ========== */

  it('should return all contracts when search empty', () => {
    component.contracts = mockContracts;
    component.searchText = '';
    component.onSearch();

    expect(component.filteredContracts.length).toBe(2);
  });

  it('should filter contracts by contract code', () => {
    component.contracts = mockContracts;
    component.searchText = 'HD001';
    component.onSearch();

    expect(component.filteredContracts.length).toBe(1);
  });

  it('should filter contracts by tenant name', () => {
    component.contracts = mockContracts;
    component.searchText = 'trần';
    component.onSearch();

    expect(component.filteredContracts.length).toBe(1);
  });

  /* ========== PAGINATION ========== */

  it('should change page and reload contracts', () => {
    spyOn(component, 'loadContracts');
    component.onPageChange(1);

    expect(component.currentPage).toBe(1);
    expect(component.loadContracts).toHaveBeenCalled();
  });

  /* ========== ROUTER ========== */

  it('should navigate to contract detail', () => {
    spyOn(router, 'navigate');
    component.viewDetail(10);

    expect(router.navigate).toHaveBeenCalledWith(['/landlord/bookings', 10]);
  });

  /* ========== STATUS UTILS ========== */

  it('should return correct status class', () => {
    expect(component.getStatusClass('ACTIVE')).toBe('status-active');
    expect(component.getStatusClass('EXPIRED')).toBe('status-expired');
  });

  it('should return correct status label', () => {
    expect(component.getStatusLabel('ACTIVE')).toBe('Đang hiệu lực');
    expect(component.getStatusLabel('CANCELLED')).toBe('Đã hủy');
  });

  /* ========== FORMATTERS ========== */

  it('should format price correctly', () => {
    expect(component.formatPrice(1000000)).toBe('1.000.000');
  });

  it('should format date correctly', () => {
    const formatted = component.formatDate('2024-01-01');
    expect(formatted).toContain('01');
  });
});
