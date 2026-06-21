import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BookingService } from './booking.service';
import { BookingRequest } from '../models/booking.model';
import { Contract } from '../models/contract.model';
import { ApiResponse } from '../models/api-response.model';

describe('BookingService - Sprint 2 (US 8.4)', () => {
  let service: BookingService;
  let httpMock: HttpTestingController;

  const mockBookingRequest: BookingRequest = {
    roomId: 1,
    startDate: '2026-02-01',
    endDate: '2027-02-01',
    deposit: 6000000,
    notes: 'Test booking',
    fullName: 'Nguyễn Văn A',
    cccd: '123456789',
    phone: '0912345678',
    address: 'Quy Nhơn'
  };

  const mockContract: Contract = {
    id: 1,
    contractCode: 'HD001',
    contractCodeGenerated: 'HD001',
    roomName: 'Phòng 101',
    buildingName: 'Dãy A',
    fullName: 'Nguyễn Văn A',
    cccd: '123456789',
    phone: '0912345678',
    address: 'Quy Nhơn',
    landlordName: 'Chủ trọ A',
    landlordPhone: '0987654321',
    startDate: '2026-02-01',
    endDate: '2027-02-01',
    deposit: 6000000,
    monthlyRent: 3000000,
    monthlyRentCalculated: 3000000,
    totalInitialCost: 9000000,
    notes: 'Test booking',
    status: 'PENDING',
    statusDisplayName: 'Chờ duyệt',
    durationMonths: 12,
    terms: [],
    createdAt: '2026-01-05T00:00:00',
    updatedAt: '2026-01-05T00:00:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BookingService]
    });

    service = TestBed.inject(BookingService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // TEST 1: Tạo booking mới thành công
  it('should create booking successfully', () => {
    const mockResponse: ApiResponse<Contract> = {
      data: mockContract
    } as any;

    service.createBooking(mockBookingRequest).subscribe(contract => {
      expect(contract).toEqual(mockContract);
      expect(contract.contractCode).toBe('HD001');
      expect(contract.status).toBe('PENDING');
    });

    const req = httpMock.expectOne('/api/bookings');
    expect(req.request.method).toBe('POST');
    expect(req.request.body.roomId).toBe(1);
    expect(req.request.body.fullName).toBe('Nguyễn Văn A');
    req.flush(mockResponse);
  });

  // TEST 2: Handle 401 Unauthorized error
  it('should handle 401 unauthorized error', () => {
    service.createBooking(mockBookingRequest).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.message).toContain('đăng nhập');
      }
    });

    const req = httpMock.expectOne('/api/bookings');
    req.flush({ message: 'Unauthorized' }, { status: 401, statusText: 'Unauthorized' });
  });

  // TEST 3: Handle 403 Forbidden error
  it('should handle 403 forbidden error', () => {
    service.createBooking(mockBookingRequest).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.message).toContain('không có quyền');
      }
    });

    const req = httpMock.expectOne('/api/bookings');
    req.flush({ message: 'Forbidden' }, { status: 403, statusText: 'Forbidden' });
  });

  // TEST 4: Handle network error
  it('should handle network error gracefully', () => {
    service.createBooking(mockBookingRequest).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.message).toBeTruthy();
      }
    });

    const req = httpMock.expectOne('/api/bookings');
    req.error(new ProgressEvent('Network error'));
  });

  // TEST 5: Validate booking request payload
  it('should send correct payload structure', () => {
    service.createBooking(mockBookingRequest).subscribe();

    const req = httpMock.expectOne('/api/bookings');
    expect(req.request.body).toEqual({
      roomId: 1,
      startDate: '2026-02-01',
      endDate: '2027-02-01',
      deposit: 6000000,
      notes: 'Test booking',
      fullName: 'Nguyễn Văn A',
      cccd: '123456789',
      phone: '0912345678',
      address: 'Quy Nhơn'
    });
    req.flush({ data: mockContract });
  });
});
