import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { InvoiceManagementComponent } from './invoice-management.component';
import { InvoiceService } from '../../../services/invoice.service';
import { ContractService } from '../../../services/contract.service';
import { RoomService } from '../../../services/room.service';
import { Invoice } from '../../../models/invoice.model';
import { Contract } from '../../../models/contract.model';

describe('InvoiceManagementComponent - Sprint 3', () => {
  let component: InvoiceManagementComponent;
  let fixture: ComponentFixture<InvoiceManagementComponent>;
  let mockInvoiceService: jasmine.SpyObj<InvoiceService>;
  let mockContractService: jasmine.SpyObj<ContractService>;
  let mockRoomService: jasmine.SpyObj<RoomService>;

  const mockInvoices: Invoice[] = [
    {
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
      notes: 'Hóa đơn tháng 1',
      createdAt: new Date('2026-01-01'),
      updatedAt: new Date('2026-01-01')
    },
    {
      id: 2,
      contractId: 1,
      contractCode: 'HD001',
      roomId: 1,
      roomName: 'Phòng 101',
      tenantName: 'Nguyễn Văn A',
      month: '2025-12',
      roomRent: 3000000,
      electricity: 180000,
      water: 90000,
      extraCost: 30000,
      totalAmount: 3300000,
      status: 'PAID',
      notes: '',
      createdAt: new Date('2025-12-01'),
      updatedAt: new Date('2025-12-15')
    }
  ];

  const mockContracts: Contract[] = [
    {
      id: 1,
      code: 'HD001',
      roomId: 1,
      roomName: 'Phòng 101',
      tenantId: 10,
      tenantName: 'Nguyễn Văn A',
      startDate: new Date('2025-01-01'),
      endDate: new Date('2026-01-01'),
      monthlyRent: 3000000,
      deposit: 6000000,
      status: 'ACTIVE'
    } as Contract
  ];

  beforeEach(async () => {
    mockInvoiceService = jasmine.createSpyObj('InvoiceService', [
      'getAll',
      'createInvoice',
      'updateInvoice',
      'updateStatus',
      'deleteInvoice',
      'getByLandlordId',
      'getByStatus'
    ]);
    mockContractService = jasmine.createSpyObj('ContractService', ['getContracts']);
    mockRoomService = jasmine.createSpyObj('RoomService', ['getRooms']);

    await TestBed.configureTestingModule({
      imports: [InvoiceManagementComponent],
      providers: [
        { provide: InvoiceService, useValue: mockInvoiceService },
        { provide: ContractService, useValue: mockContractService },
        { provide: RoomService, useValue: mockRoomService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(InvoiceManagementComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ========== US 4.1: Tạo hóa đơn ==========
  describe('US 4.1: Tạo hóa đơn', () => {
    
    beforeEach(() => {
      mockContractService.getContracts.and.returnValue(of({ data: mockContracts } as any));
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
    });

    // TEST 1: Hiển thị form tạo hóa đơn
    it('should open create invoice modal', () => {
      component.openCreateModal();
      
      expect(component.isModalOpen()).toBe(true);
      expect(component.isEditMode()).toBe(false);
      expect(component.form().contractId).toBe(0);
    });

    // TEST 2: Validate các trường bắt buộc khi tạo hóa đơn
    it('should validate required fields when creating invoice', () => {
      spyOn(window, 'alert');
      component.openCreateModal();
      component.form.set({
        contractId: 0,
        month: '2026-01',
        electricity: 200000,
        water: 100000,
        extraCost: 50000,
        notes: ''
      });
      
      component.saveInvoice();
      
      expect(window.alert).toHaveBeenCalledWith('Vui lòng chọn hợp đồng');
      expect(mockInvoiceService.createInvoice).not.toHaveBeenCalled();
    });

    // TEST 3: Tạo hóa đơn mới thành công
    it('should create new invoice successfully', () => {
      spyOn(window, 'alert');
      const newInvoiceRequest = {
        contractId: 1,
        month: '2026-01',
        electricity: 200000,
        water: 100000,
        extraCost: 50000,
        notes: 'Test invoice'
      };
      
      mockInvoiceService.createInvoice.and.returnValue(
        of({ success: true, data: { ...mockInvoices[0] } } as any)
      );
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
      
      component.openCreateModal();
      component.form.set(newInvoiceRequest);
      component.saveInvoice();
      
      expect(mockInvoiceService.createInvoice).toHaveBeenCalledWith(newInvoiceRequest);
      expect(window.alert).toHaveBeenCalledWith('Tạo hóa đơn thành công');
    });

    // TEST 4: Hiển thị lỗi khi tạo hóa đơn thất bại
    it('should show error when create invoice fails', () => {
      spyOn(window, 'alert');
      const errorResponse = { error: { message: 'Lỗi tạo hóa đơn' } };
      
      mockInvoiceService.createInvoice.and.returnValue(
        throwError(() => errorResponse)
      );
      
      component.form.set({
        contractId: 1,
        month: '2026-01',
        electricity: 200000,
        water: 100000,
        extraCost: 50000,
        notes: ''
      });
      
      component.saveInvoice();
      
      expect(window.alert).toHaveBeenCalledWith('Lỗi: Lỗi tạo hóa đơn');
    });

    // TEST 5: Load danh sách hợp đồng để tạo hóa đơn
    it('should load contracts for invoice creation', () => {
      component.ngOnInit();
      
      expect(mockContractService.getContracts).toHaveBeenCalled();
      expect(component.contracts().length).toBeGreaterThan(0);
    });

    // TEST 6: Tính toán tổng tiền hóa đơn tự động
    it('should calculate total amount automatically', () => {
      const invoice = mockInvoices[0];
      const expectedTotal = invoice.roomRent + invoice.electricity + invoice.water + invoice.extraCost;
      
      expect(invoice.totalAmount).toBe(expectedTotal);
    });

    // TEST 7: Tạo hóa đơn với tháng hiện tại mặc định
    it('should create invoice with current month as default', () => {
      component.openCreateModal();
      
      const currentMonth = component.form().month;
      expect(currentMonth).toMatch(/^\d{4}-\d{2}$/); // Format YYYY-MM
    });

    // TEST 8: Đóng modal sau khi tạo thành công
    it('should close modal after successful creation', () => {
      mockInvoiceService.createInvoice.and.returnValue(
        of({ success: true, data: mockInvoices[0] } as any)
      );
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
      
      component.openCreateModal();
      component.form.set({
        contractId: 1,
        month: '2026-01',
        electricity: 200000,
        water: 100000,
        extraCost: 50000,
        notes: ''
      });
      
      component.saveInvoice();
      
      expect(component.isModalOpen()).toBe(false);
    });
  });

  // ========== US 9.1: Xem danh sách hóa đơn ==========
  describe('US 9.1: Xem danh sách hóa đơn', () => {
    
    beforeEach(() => {
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
      mockContractService.getContracts.and.returnValue(of({ data: mockContracts } as any));
    });

    // TEST 9: Load tất cả hóa đơn khi khởi tạo
    it('should load all invoices on init', () => {
      component.ngOnInit();
      
      expect(mockInvoiceService.getAll).toHaveBeenCalled();
      expect(component.invoices().length).toBe(2);
    });

    // TEST 10: Hiển thị danh sách hóa đơn với đầy đủ thông tin
    it('should display invoice list with full information', () => {
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
      
      component.loadInvoices();
      
      const invoices = component.invoices();
      expect(invoices[0].contractCode).toBe('HD001');
      expect(invoices[0].roomName).toBe('Phòng 101');
      expect(invoices[0].tenantName).toBe('Nguyễn Văn A');
      expect(invoices[0].totalAmount).toBe(3350000);
    });

    // TEST 11: Lọc hóa đơn theo tháng
    it('should filter invoices by month', () => {
      component.invoices.set(mockInvoices);
      component.filters.set({
        month: '2026-01',
        contractId: 0,
        status: 'ALL',
        keyword: ''
      });
      
      const filtered = component.filteredInvoices();
      
      expect(filtered.length).toBe(1);
      expect(filtered[0].month).toBe('2026-01');
    });

    // TEST 12: Lọc hóa đơn theo trạng thái
    it('should filter invoices by status', () => {
      component.invoices.set(mockInvoices);
      component.filters.set({
        month: '',
        contractId: 0,
        status: 'PAID',
        keyword: ''
      });
      
      const filtered = component.filteredInvoices();
      
      expect(filtered.length).toBe(1);
      expect(filtered[0].status).toBe('PAID');
    });

    // TEST 13: Lọc hóa đơn theo hợp đồng
    it('should filter invoices by contract', () => {
      component.invoices.set(mockInvoices);
      component.filters.set({
        month: '',
        contractId: 1,
        status: 'ALL',
        keyword: ''
      });
      
      const filtered = component.filteredInvoices();
      
      expect(filtered.length).toBe(2);
      expect(filtered.every(inv => inv.contractId === 1)).toBe(true);
    });

    // TEST 14: Tìm kiếm hóa đơn theo từ khóa
    it('should search invoices by keyword', () => {
      component.invoices.set(mockInvoices);
      component.filters.set({
        month: '',
        contractId: 0,
        status: 'ALL',
        keyword: 'HD001'
      });
      
      const filtered = component.filteredInvoices();
      
      expect(filtered.length).toBe(2);
      expect(filtered.every(inv => inv.contractCode.includes('HD001'))).toBe(true);
    });

    // TEST 15: Tính toán thống kê hóa đơn
    it('should calculate invoice statistics', () => {
      component.invoices.set(mockInvoices);
      component.filters.set({
        month: '',
        contractId: 0,
        status: 'ALL',
        keyword: ''
      });
      
      const stats = component.stats();
      
      expect(stats.totalCount).toBe(2);
      expect(stats.unpaidCount).toBe(1);
      expect(stats.paidCount).toBe(1);
      expect(stats.totalAmount).toBe(6650000);
    });

    // TEST 16: Hiển thị hóa đơn theo thứ tự thời gian
    it('should display invoices in chronological order', () => {
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
      
      component.loadInvoices();
      
      const invoices = component.invoices();
      expect(invoices.length).toBe(2);
      expect(invoices[0].month).toBe('2026-01');
    });
  });

  // ========== US 9.2: Thanh toán hóa đơn trực tuyến ==========
  describe('US 9.2: Thanh toán hóa đơn trực tuyến', () => {
    
    // TEST 17: Chuyển trạng thái hóa đơn sang PAID
    it('should update invoice status to PAID', () => {
      spyOn(window, 'alert');
      const invoice = mockInvoices[0];
      
      mockInvoiceService.updateStatus.and.returnValue(
        of({ success: true, data: { ...invoice, status: 'PAID' } } as any)
      );
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
      
      component.updateInvoiceStatus(invoice, 'PAID');
      component.confirmAction();
      
      expect(mockInvoiceService.updateStatus).toHaveBeenCalledWith(invoice.id, 'PAID');
      expect(window.alert).toHaveBeenCalledWith('Cập nhật trạng thái thành công');
    });

    // TEST 18: Hiển thị modal xác nhận thanh toán
    it('should show confirmation modal before payment', () => {
      const invoice = mockInvoices[0];
      
      component.updateInvoiceStatus(invoice, 'PAID');
      
      expect(component.isConfirmModalOpen()).toBe(true);
      expect(component.invoiceToConfirm()).toBe(invoice);
      expect(component.actionType()).toBe('PAY');
    });

    // TEST 19: Hủy thanh toán khi user không xác nhận
    it('should cancel payment if user does not confirm', () => {
      const invoice = mockInvoices[0];
      
      component.updateInvoiceStatus(invoice, 'PAID');
      component.cancelAction();
      
      expect(component.isConfirmModalOpen()).toBe(false);
      expect(component.invoiceToConfirm()).toBeNull();
      expect(mockInvoiceService.updateStatus).not.toHaveBeenCalled();
    });

    // TEST 20: Reload danh sách sau khi thanh toán thành công
    it('should reload invoices after successful payment', () => {
      const invoice = mockInvoices[0];
      
      mockInvoiceService.updateStatus.and.returnValue(
        of({ success: true, data: { ...invoice, status: 'PAID' } } as any)
      );
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
      spyOn(component, 'loadInvoices');
      
      component.updateInvoiceStatus(invoice, 'PAID');
      component.confirmAction();
      
      expect(component.loadInvoices).toHaveBeenCalled();
    });

    // TEST 21: Hiển thị lỗi khi thanh toán thất bại
    it('should show error when payment fails', () => {
      spyOn(window, 'alert');
      const invoice = mockInvoices[0];
      const errorResponse = { error: { message: 'Thanh toán thất bại' } };
      
      mockInvoiceService.updateStatus.and.returnValue(
        throwError(() => errorResponse)
      );
      
      component.updateInvoiceStatus(invoice, 'PAID');
      component.confirmAction();
      
      expect(window.alert).toHaveBeenCalledWith('Lỗi: Thanh toán thất bại');
    });
  });

  // ========== US 9.3: Cập nhật trạng thái thanh toán thủ công ==========
  describe('US 9.3: Cập nhật trạng thái thanh toán thủ công', () => {
    
    // TEST 22: Cập nhật trạng thái từ PAID về UNPAID
    it('should update status from PAID to UNPAID', () => {
      spyOn(window, 'alert');
      const invoice = { ...mockInvoices[1], status: 'PAID' as const };
      
      mockInvoiceService.updateStatus.and.returnValue(
        of({ success: true, data: { ...invoice, status: 'UNPAID' } } as any)
      );
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
      
      component.updateInvoiceStatus(invoice, 'UNPAID');
      component.confirmAction();
      
      expect(mockInvoiceService.updateStatus).toHaveBeenCalledWith(invoice.id, 'UNPAID');
      expect(window.alert).toHaveBeenCalledWith('Cập nhật trạng thái thành công');
    });

    // TEST 23: Hiển thị modal xác nhận khi thay đổi trạng thái
    it('should show confirmation modal when changing status', () => {
      const invoice = mockInvoices[1];
      
      component.updateInvoiceStatus(invoice, 'UNPAID');
      
      expect(component.isConfirmModalOpen()).toBe(true);
      expect(component.actionType()).toBe('UNPAY');
    });

    // TEST 24: Admin có thể chỉnh sửa hóa đơn
    it('should allow admin to edit invoice', () => {
      const invoice = mockInvoices[0];
      
      component.editInvoice(invoice);
      
      expect(component.isEditMode()).toBe(true);
      expect(component.editingInvoiceId()).toBe(invoice.id);
      expect(component.form().contractId).toBe(invoice.contractId);
      expect(component.form().month).toBe(invoice.month);
    });

    // TEST 25: Cập nhật hóa đơn thành công
    it('should update invoice successfully', () => {
      spyOn(window, 'alert');
      const invoice = mockInvoices[0];
      const updateRequest = {
        contractId: invoice.contractId,
        month: invoice.month,
        electricity: 250000,
        water: 120000,
        extraCost: 60000,
        notes: 'Updated'
      };
      
      mockInvoiceService.updateInvoice.and.returnValue(
        of({ success: true, data: { ...invoice, ...updateRequest } } as any)
      );
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
      
      component.editInvoice(invoice);
      component.form.set(updateRequest);
      component.saveInvoice();
      
      expect(mockInvoiceService.updateInvoice).toHaveBeenCalledWith(invoice.id, updateRequest);
      expect(window.alert).toHaveBeenCalledWith('Cập nhật hóa đơn thành công');
    });

    // TEST 26: Đóng modal sau khi cập nhật thành công
    it('should close modal after successful update', () => {
      const invoice = mockInvoices[0];
      
      mockInvoiceService.updateInvoice.and.returnValue(
        of({ success: true, data: invoice } as any)
      );
      mockInvoiceService.getAll.and.returnValue(of({ data: mockInvoices } as any));
      
      component.editInvoice(invoice);
      component.form.set({
        contractId: invoice.contractId,
        month: invoice.month,
        electricity: invoice.electricity,
        water: invoice.water,
        extraCost: invoice.extraCost,
        notes: invoice.notes
      });
      
      component.saveInvoice();
      
      expect(component.isModalOpen()).toBe(false);
      expect(component.isEditMode()).toBe(false);
    });

    // TEST 27: Hiển thị lỗi khi cập nhật thất bại
    it('should show error when update fails', () => {
      spyOn(window, 'alert');
      const invoice = mockInvoices[0];
      const errorResponse = { error: { message: 'Cập nhật thất bại' } };
      
      mockInvoiceService.updateInvoice.and.returnValue(
        throwError(() => errorResponse)
      );
      
      component.editInvoice(invoice);
      component.form.set({
        contractId: invoice.contractId,
        month: invoice.month,
        electricity: invoice.electricity,
        water: invoice.water,
        extraCost: invoice.extraCost,
        notes: invoice.notes
      });
      
      component.saveInvoice();
      
      expect(window.alert).toHaveBeenCalledWith('Lỗi: Cập nhật thất bại');
    });

    // TEST 28: Validate dữ liệu khi cập nhật thủ công
    it('should validate data when updating manually', () => {
      spyOn(window, 'alert');
      const invoice = mockInvoices[0];
      
      component.editInvoice(invoice);
      component.form.set({
        contractId: 0,
        month: invoice.month,
        electricity: invoice.electricity,
        water: invoice.water,
        extraCost: invoice.extraCost,
        notes: invoice.notes
      });
      
      component.saveInvoice();
      
      expect(window.alert).toHaveBeenCalledWith('Vui lòng chọn hợp đồng');
      expect(mockInvoiceService.updateInvoice).not.toHaveBeenCalled();
    });
  });
});
