import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { WaterManagementComponent } from './water-management.component';
import { WaterService } from '../../../services/water.service';
import { RoomService } from '../../../services/room.service';
import { InvoiceService } from '../../../services/invoice.service';
import { UtilityStatus } from '../../../models/electricity.model';
import { Room } from '../../../models/room.model';
import { WaterRecord } from '../../../models/water.model';
import { Invoice } from '../../../models/invoice.model';
import localeVi from '@angular/common/locales/vi';
import { registerLocaleData } from '@angular/common';
import { LOCALE_ID } from '@angular/core';

describe('WaterManagementComponent', () => {
  registerLocaleData(localeVi);

  let component: WaterManagementComponent;
  let fixture: ComponentFixture<WaterManagementComponent>;

  let waterServiceMock: any;
  let roomServiceMock: any;
  let invoiceServiceMock: any;

  const fakeRooms: Room[] = [
    { id: 1, name: 'P.101', status: 'OCCUPIED' },
    { id: 2, name: 'P.102', status: 'OCCUPIED' },
  ];

  const fakeInvoices: Invoice[] = [
    {
      id: 1,
      contractId: 1,
      contractCode: 'HD001',
      roomId: 1,
      roomName: 'P.101',
      tenantName: 'Nguyen A',
      month: '2026-01',
      roomRent: 2000000,
      electricity: 50000,
      water: 150000,
      extraCost: 0,
      totalAmount: 2205000,
      status: 'PAID',
      createdAt: new Date(),
      updatedAt: new Date(),
    },
    {
      id: 2,
      contractId: 2,
      contractCode: 'HD002',
      roomId: 2,
      roomName: 'P.102',
      tenantName: 'Nguyen B',
      month: '2026-01',
      roomRent: 1800000,
      electricity: 40000,
      water: 150000,
      extraCost: 0,
      totalAmount: 1990000,
      status: 'UNPAID',
      createdAt: new Date(),
      updatedAt: new Date(),
    },
  ];

  const fakeRecords: WaterRecord[] = [
    {
      id: 1,
      roomId: 1,
      name: 'P.101',
      fullName: 'Nguyen A',
      oldIndex: 10,
      newIndex: 20,
      usage: 10,
      unitPrice: 15000,
      totalAmount: 150000,
      month: '2026-01',
      status: UtilityStatus.UNPAID, // will be synced to PAID
    },
    {
      id: 2,
      roomId: 2,
      name: 'P.102',
      fullName: 'Nguyen B',
      oldIndex: 5,
      newIndex: 15,
      usage: 10,
      unitPrice: 15000,
      totalAmount: 150000,
      month: '2026-01',
      status: UtilityStatus.UNPAID,
    },
  ];

  beforeEach(async () => {
    waterServiceMock = jasmine.createSpyObj('WaterService', [
      'getAll',
      'create',
      'update',
      'markPaid',
      'delete',
    ]);
    roomServiceMock = jasmine.createSpyObj('RoomService', ['getMyRooms']);
    invoiceServiceMock = jasmine.createSpyObj('InvoiceService', ['getAll']);

    // Trả dữ liệu đúng kiểu và sync logic
    waterServiceMock.getAll.and.returnValue(
      of(fakeRecords.map((r) => ({ ...r })))
    );
    waterServiceMock.create.and.returnValue(of({ ...fakeRecords[1] }));
    waterServiceMock.update.and.returnValue(of({ ...fakeRecords[1] }));
    waterServiceMock.markPaid.and.returnValue(
      of({ ...fakeRecords[1], status: UtilityStatus.PAID })
    );
    waterServiceMock.delete.and.returnValue(of(void 0));

    roomServiceMock.getMyRooms.and.returnValue(of(fakeRooms));
    invoiceServiceMock.getAll.and.returnValue(of(fakeInvoices));

    await TestBed.configureTestingModule({
      imports: [WaterManagementComponent],
      providers: [
        { provide: WaterService, useValue: waterServiceMock },
        { provide: RoomService, useValue: roomServiceMock },
        { provide: InvoiceService, useValue: invoiceServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(WaterManagementComponent);
    component = fixture.componentInstance;

    spyOn(window, 'alert');
    fixture.detectChanges();
  });
  /**
   * TC01: Component khởi tạo thành công
   */
  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  /**
   * TC02: NgOnInit
   * - Load danh sách phòng
   * - Gán đúng dữ liệu vào roomList signal
   */
  it('should load rooms on init', () => {
    expect(component.roomList()).toEqual(fakeRooms);
  });

  /**
   * TC03: NgOnInit
   * - Load danh sách ghi nước
   * - Đồng bộ trạng thái thanh toán với hóa đơn
   */
  it('should load water records and sync with invoices', () => {
    const records = component.records();
    const paidRecord = records.find((r) => r.roomId === 1);

    // Trạng thái đã được sync từ invoice
    expect(paidRecord?.status).toBe(UtilityStatus.PAID);
  });

  /**
   * TC04: openAddModal()
   * - Mở modal thêm mới
   * - Reset form về trạng thái mặc định
   * - Tắt chế độ chỉnh sửa
   */
  it('should open add modal and reset form', () => {
    component.openAddModal();

    expect(component.isModalOpen()).toBeTrue();
    expect(component.isEditMode()).toBeFalse();
    expect(component.form().id).toBe(0);
  });

  /**
   * TC05: openEditModal()
   * - Không cho phép chỉnh sửa bản ghi đã thanh toán
   * - Hiển thị cảnh báo cho người dùng
   */
  it('should not open edit modal for paid record', () => {
    const paidRecord = component
      .records()
      .find((r) => r.status === UtilityStatus.PAID)!;

    component.openEditModal(paidRecord);

    expect(window.alert).toHaveBeenCalledWith(
      'Bản ghi đã thanh toán, không thể chỉnh sửa!'
    );
  });

  /**
   * TC06: confirmPayment()
   * - Xác nhận thanh toán bản ghi chưa thanh toán
   * - Gọi service.markPaid(id)
   */
  it('should confirm payment', () => {
    const unpaidRecord = component
      .records()
      .find((r) => r.status === UtilityStatus.UNPAID)!;

    component.recordToConfirm.set(unpaidRecord);
    component.confirmPayment();

    expect(waterServiceMock.markPaid).toHaveBeenCalledWith(unpaidRecord.id);
  });

  /**
   * TC07: deleteRecord()
   * - Xóa bản ghi khi đang ở chế độ xóa
   * - Gọi service.delete(id)
   */
  it('should delete record', () => {
    const record = component.records()[0];

    component.recordToConfirm.set(record);
    component.isDeleteMode.set(true);
    component.deleteRecord();

    expect(waterServiceMock.delete).toHaveBeenCalledWith(record.id);
  });

  /**
   * TC08: submitForm()
   * - Chế độ thêm mới (create)
   * - Gọi service.create()
   */
  it('should submit form in create mode', () => {
    component.openAddModal();

    const form = component.form();
    form.roomId = 2;
    form.month = '2026-01';
    form.newIndex = 20;
    form.oldIndex = 10;

    component.submitForm();

    expect(waterServiceMock.create).toHaveBeenCalled();
  });

  /**
   * TC09: filteredRecords()
   * - Lọc danh sách bản ghi theo tháng
   */
  it('should filter records by month', () => {
    component.filters.update((f) => ({ ...f, month: '2026-01' }));

    const filtered = component.filteredRecords();

    expect(filtered.every((r) => r.month === '2026-01')).toBeTrue();
  });
});
