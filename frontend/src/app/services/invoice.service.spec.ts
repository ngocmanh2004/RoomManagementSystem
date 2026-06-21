import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { InvoiceService } from './invoice.service';
import { Invoice, InvoiceCreateRequest } from '../models/invoice.model';

describe('InvoiceService - Sprint 3', () => {
  let service: InvoiceService;
  let httpMock: HttpTestingController;

  const mockInvoice: Invoice = {
    id: 1,
    contractId: 1,
    contractCode: 'HD001',
    roomId: 1,
    roomName: 'Phòng 101',
    tenantName: 'Nguyễn Văn A',
    month: '2026-01',
    roomRent: 3000000,
    electricity: 200000,
    water: 100000,
    extraCost: 50000,
    totalAmount: 3350000,
    status: 'UNPAID',
    notes: '',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [InvoiceService]
    });
    service = TestBed.inject(InvoiceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ========== US 4.1: Tạo hóa đơn - Service Layer ==========
  describe('US 4.1: Tạo hóa đơn - Service Layer', () => {

    // TEST 1: Call API tạo hóa đơn
    it('should call POST /api/invoices to create invoice', () => {
      const request: InvoiceCreateRequest = {
        contractId: 1,
        month: '2026-01',
        electricity: 200000,
        water: 100000,
        extraCost: 50000,
        notes: 'Test'
      };

      service.createInvoice(request).subscribe(response => {
        expect(response.success).toBe(true);
        expect(response.data).toEqual(jasmine.objectContaining({ id: 1 }));
      });

      const req = httpMock.expectOne('/api/invoices');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(request);
      req.flush({ success: true, data: mockInvoice });
    });

    // TEST 2: Handle error khi tạo hóa đơn fail
    it('should handle error when create invoice fails', () => {
      const request: InvoiceCreateRequest = {
        contractId: 1,
        month: '2026-01',
        electricity: 200000,
        water: 100000,
        extraCost: 50000
      };

      service.createInvoice(request).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('/api/invoices');
      req.flush({ message: 'Bad Request' }, { status: 400, statusText: 'Bad Request' });
    });

    // TEST 3: Lấy tiền điện theo phòng và tháng
    it('should get electricity cost by room and month', () => {
      service.getElectricityByRoomMonth(1, '2026-01').subscribe(amount => {
        expect(amount).toBe(200000);
      });

      const req = httpMock.expectOne('/api/utilities/electric?month=2026-01&roomId=1');
      expect(req.request.method).toBe('GET');
      req.flush([{ totalAmount: 200000 }]);
    });

    // TEST 4: Trả về 0 khi không có dữ liệu điện
    it('should return 0 when no electricity data', () => {
      service.getElectricityByRoomMonth(1, '2026-01').subscribe(amount => {
        expect(amount).toBe(0);
      });

      const req = httpMock.expectOne('/api/utilities/electric?month=2026-01&roomId=1');
      req.flush([]);
    });

    // TEST 5: Lấy tiền nước theo phòng và tháng
    it('should get water cost by room and month', () => {
      service.getWaterByRoomMonth(1, '2026-01').subscribe(amount => {
        expect(amount).toBe(100000);
      });

      const req = httpMock.expectOne('/api/utilities/water?month=2026-01&roomId=1');
      expect(req.request.method).toBe('GET');
      req.flush([{ totalAmount: 100000 }]);
    });

    // TEST 6: Lấy tổng chi phí phát sinh
    it('should get total extra costs by room and month', () => {
      service.getExtraCostsTotalByRoomMonth(1, '2026-01').subscribe(amount => {
        expect(amount).toBe(150000);
      });

      const req = httpMock.expectOne('/api/utilities/extra-costs?month=2026-01&roomId=1');
      req.flush([
        { amount: 50000 },
        { totalAmount: 100000 }
      ]);
    });
  });

  // ========== US 9.1: Xem danh sách hóa đơn - Service Layer ==========
  describe('US 9.1: Xem danh sách hóa đơn - Service Layer', () => {

    // TEST 7: Lấy tất cả hóa đơn
    it('should get all invoices', () => {
      const mockInvoices = [mockInvoice];

      service.getAll().subscribe(response => {
        expect(response.data?.length).toBe(1);
        expect(response.data?.[0].id).toBe(1);
      });

      const req = httpMock.expectOne('/api/invoices');
      expect(req.request.method).toBe('GET');
      req.flush({ success: true, data: mockInvoices });
    });

    // TEST 8: Lấy hóa đơn theo ID
    it('should get invoice by id', () => {
      service.getById(1).subscribe(response => {
        expect(response.data?.id).toBe(1);
      });

      const req = httpMock.expectOne('/api/invoices/1');
      expect(req.request.method).toBe('GET');
      req.flush({ success: true, data: mockInvoice });
    });

    // TEST 9: Lấy hóa đơn theo hợp đồng
    it('should get invoices by contract id', () => {
      service.getByContractId(1).subscribe(response => {
        expect(response.data?.length).toBe(1);
        expect(response.data?.[0].contractId).toBe(1);
      });

      const req = httpMock.expectOne('/api/invoices/contract/1');
      expect(req.request.method).toBe('GET');
      req.flush({ success: true, data: [mockInvoice] });
    });

    // TEST 10: Lấy hóa đơn theo phòng
    it('should get invoices by room id', () => {
      service.getByRoomId(1).subscribe(response => {
        expect(response.data?.length).toBe(1);
        expect(response.data?.[0].roomId).toBe(1);
      });

      const req = httpMock.expectOne('/api/invoices/room/1');
      req.flush({ success: true, data: [mockInvoice] });
    });

    // TEST 11: Lấy hóa đơn theo người thuê
    it('should get invoices by tenant id', () => {
      service.getByTenantId(10).subscribe(response => {
        expect(response.data).toBeDefined();
      });

      const req = httpMock.expectOne('/api/invoices/tenant/10');
      req.flush({ success: true, data: [mockInvoice] });
    });

    // TEST 12: Lấy hóa đơn theo chủ trọ
    it('should get invoices by landlord id', () => {
      service.getByLandlordId(5).subscribe(response => {
        expect(response.data).toBeDefined();
      });

      const req = httpMock.expectOne('/api/invoices/landlord/5');
      req.flush({ success: true, data: [mockInvoice] });
    });

    // TEST 13: Lấy hóa đơn theo tháng
    it('should get invoices by month', () => {
      service.getByMonth('2026-01').subscribe(response => {
        expect(response.data?.[0].month).toBe('2026-01');
      });

      const req = httpMock.expectOne('/api/invoices/month/2026-01');
      req.flush({ success: true, data: [mockInvoice] });
    });

    // TEST 14: Lấy hóa đơn theo trạng thái
    it('should get invoices by status', () => {
      service.getByStatus('UNPAID').subscribe(response => {
        expect(response.data?.[0].status).toBe('UNPAID');
      });

      const req = httpMock.expectOne('/api/invoices/status/UNPAID');
      req.flush({ success: true, data: [mockInvoice] });
    });

    // TEST 15: Lấy hóa đơn theo chủ trọ và trạng thái
    it('should get invoices by landlord id and status', () => {
      service.getByLandlordIdAndStatus(5, 'UNPAID').subscribe(response => {
        expect(response.data).toBeDefined();
      });

      const req = httpMock.expectOne('/api/invoices/landlord/5/status/UNPAID');
      req.flush({ success: true, data: [mockInvoice] });
    });
  });

  // ========== US 9.2: Thanh toán hóa đơn - Service Layer ==========
  describe('US 9.2: Thanh toán hóa đơn - Service Layer', () => {

    // TEST 16: Cập nhật trạng thái hóa đơn
    it('should update invoice status', () => {
      service.updateStatus(1, 'PAID').subscribe(response => {
        expect(response.success).toBe(true);
        expect(response.data?.status).toBe('PAID');
      });

      const req = httpMock.expectOne('/api/invoices/1/status?status=PAID');
      expect(req.request.method).toBe('PATCH');
      req.flush({ success: true, data: { ...mockInvoice, status: 'PAID' } });
    });

    // TEST 17: Handle error khi cập nhật status fail
    it('should handle error when update status fails', () => {
      service.updateStatus(1, 'PAID').subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(403);
        }
      });

      const req = httpMock.expectOne('/api/invoices/1/status?status=PAID');
      req.flush({ message: 'Forbidden' }, { status: 403, statusText: 'Forbidden' });
    });
  });

  // ========== US 9.3: Cập nhật hóa đơn - Service Layer ==========
  describe('US 9.3: Cập nhật hóa đơn - Service Layer', () => {

    // TEST 18: Cập nhật hóa đơn
    it('should update invoice', () => {
      const updateRequest: InvoiceCreateRequest = {
        contractId: 1,
        month: '2026-01',
        electricity: 250000,
        water: 120000,
        extraCost: 60000,
        notes: 'Updated'
      };

      service.updateInvoice(1, updateRequest).subscribe(response => {
        expect(response.success).toBe(true);
      });

      const req = httpMock.expectOne('/api/invoices/1');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateRequest);
      req.flush({ success: true, data: mockInvoice });
    });

    // TEST 19: Xóa hóa đơn
    it('should delete invoice', () => {
      service.deleteInvoice(1).subscribe(response => {
        expect(response.success).toBe(true);
      });

      const req = httpMock.expectOne('/api/invoices/1');
      expect(req.request.method).toBe('DELETE');
      req.flush({ success: true });
    });

    // TEST 20: Handle error khi xóa fail
    it('should handle error when delete invoice fails', () => {
      service.deleteInvoice(1).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne('/api/invoices/1');
      req.flush({ message: 'Not Found' }, { status: 404, statusText: 'Not Found' });
    });
  });
});
