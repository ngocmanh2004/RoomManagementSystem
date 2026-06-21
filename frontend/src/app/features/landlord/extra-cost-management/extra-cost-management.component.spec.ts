import { TestBed, ComponentFixture } from '@angular/core/testing';
import { ExtraCostManagementComponent } from './extra-cost-management.component';
import { ExtraCostService } from '../../../services/extra-cost.service';
import { RoomService } from '../../../services/room.service';
import { InvoiceService } from '../../../services/invoice.service';
import { of } from 'rxjs';
import localeVi from '@angular/common/locales/vi';
import { registerLocaleData } from '@angular/common';
import { CostType, ExtraCostStatus } from '../../../models/extra-cost.model';

registerLocaleData(localeVi);

describe('ExtraCostManagementComponent', () => {
  let component: ExtraCostManagementComponent;
  let fixture: ComponentFixture<ExtraCostManagementComponent>;

  let mockExtraCostService: any;
  let mockRoomService: any;
  let mockInvoiceService: any;

  beforeEach(async () => {
    mockExtraCostService = {
      getAll: jasmine.createSpy('getAll').and.returnValue(of([])),
      create: jasmine.createSpy('create').and.returnValue(of({})),
      update: jasmine.createSpy('update').and.returnValue(of({})),
      delete: jasmine.createSpy('delete').and.returnValue(of({})),
      markPaid: jasmine.createSpy('markPaid').and.returnValue(of({})),
    };

    mockRoomService = {
      getMyRooms: jasmine.createSpy('getMyRooms').and.returnValue(of([])),
    };

    mockInvoiceService = {
      getAll: jasmine.createSpy('getAll').and.returnValue(of([])),
    };

    await TestBed.configureTestingModule({
      imports: [ExtraCostManagementComponent],
      providers: [
        { provide: ExtraCostService, useValue: mockExtraCostService },
        { provide: RoomService, useValue: mockRoomService },
        { provide: InvoiceService, useValue: mockInvoiceService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ExtraCostManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  /**
   * TC01: Component khởi tạo thành công
   */
  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  /**
   * TC02: removeAccents()
   * - Loại bỏ dấu tiếng Việt
   * - Chuyển chuỗi về chữ thường
   */
  it('should remove accents and lowercase', () => {
    const result = component.removeAccents('Nguyễn Văn Ánh');
    expect(result).toBe('nguyen van anh');
  });

  /**
   * TC03: filteredRecords()
   * - Lọc danh sách theo keyword (tìm theo tên người thuê không dấu)
   */
  it('filteredRecords should filter by keyword', () => {
    component.records.set([
      {
        id: 1,
        roomId: 101,
        name: 'Internet Fee',
        type: CostType.INTERNET,
        amount: 100,
        month: '2026-01',
        description: '',
        status: ExtraCostStatus.UNPAID,
        fullName: 'Nguyễn Văn A',
      },
      {
        id: 2,
        roomId: 102,
        name: 'Garbage Fee',
        type: CostType.GARBAGE,
        amount: 50,
        month: '2026-01',
        description: '',
        status: ExtraCostStatus.PAID,
        fullName: 'Trần Thị B',
      },
    ]);

    component.filters.set({
      month: '',
      roomId: 0,
      type: 'ALL',
      status: 'ALL',
      keyword: 'nguyen',
    });

    const filtered = component.filteredRecords();

    expect(filtered.length).toBe(1);
    expect(filtered[0].fullName).toBe('Nguyễn Văn A');
  });

  /**
   * TC04: stats()
   * - Tính tổng doanh thu
   * - Tổng số bản ghi
   * - Số hóa đơn chưa thanh toán
   * - Số hóa đơn đã thanh toán
   */
  it('stats should compute correctly', () => {
    component.records.set([
      {
        id: 1,
        roomId: 101,
        name: 'Internet Fee',
        type: CostType.INTERNET,
        amount: 100,
        month: '2026-01',
        description: '',
        status: ExtraCostStatus.UNPAID,
        fullName: 'Nguyễn Văn A',
      },
      {
        id: 2,
        roomId: 102,
        name: 'Garbage Fee',
        type: CostType.GARBAGE,
        amount: 50,
        month: '2026-01',
        description: '',
        status: ExtraCostStatus.PAID,
        fullName: 'Trần Thị B',
      },
    ]);

    const stats = component.stats();

    expect(stats.totalRevenue).toBe(150);
    expect(stats.totalRecords).toBe(2);
    expect(stats.unpaidBills).toBe(1);
    expect(stats.paidBills).toBe(1);
  });

  /**
   * TC05: submitForm()
   * - Khi ở chế độ thêm mới (isEditMode = false)
   * - Gọi service.create()
   */
  it('submitForm should call create for new record', () => {
    component.form.set({
      id: 0,
      roomId: 101,
      name: 'Internet Fee',
      type: CostType.INTERNET,
      amount: 100,
      month: '2026-01',
      description: '',
      status: ExtraCostStatus.UNPAID,
      fullName: '',
    });

    component.isEditMode.set(false);

    component.submitForm();

    expect(mockExtraCostService.create).toHaveBeenCalled();
  });

  /**
   * TC06: submitForm()
   * - Khi ở chế độ chỉnh sửa (isEditMode = true)
   * - Gọi service.update(id, payload)
   */
  it('submitForm should call update for edit mode', () => {
    component.form.set({
      id: 1,
      roomId: 101,
      name: 'Internet Fee',
      type: CostType.INTERNET,
      amount: 100,
      month: '2026-01',
      description: '',
      status: ExtraCostStatus.UNPAID,
      fullName: '',
    });

    component.isEditMode.set(true);

    component.submitForm();

    expect(mockExtraCostService.update).toHaveBeenCalledWith(
      1,
      jasmine.any(Object)
    );
  });

  /**
   * TC07: confirmPayment()
   * - Xác nhận thanh toán
   * - Gọi service.markPaid(id)
   */
  it('confirmPayment should call markPaid', () => {
    component.recordToConfirm.set({
      id: 1,
      roomId: 101,
      name: 'Internet Fee',
      type: CostType.INTERNET,
      amount: 100,
      month: '2026-01',
      description: '',
      status: ExtraCostStatus.UNPAID,
      fullName: '',
    });

    component.confirmPayment();

    expect(mockExtraCostService.markPaid).toHaveBeenCalledWith(1);
  });

  /**
   * TC08: onConfirmAction()
   * - Khi ở chế độ xóa (isDeleteMode = true)
   * - Gọi service.delete(id)
   */
  it('deleteRecord should call delete', () => {
    component.recordToConfirm.set({
      id: 1,
      roomId: 101,
      name: 'Internet Fee',
      type: CostType.INTERNET,
      amount: 100,
      month: '2026-01',
      description: '',
      status: ExtraCostStatus.UNPAID,
      fullName: '',
    });

    component.isDeleteMode.set(true);
    component.onConfirmAction();

    expect(mockExtraCostService.delete).toHaveBeenCalledWith(1);
  });
});
