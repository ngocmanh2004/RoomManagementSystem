import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ElectricityManagementComponent } from './electricity-management.component';
import { of } from 'rxjs';
import { UtilityService } from '../../../services/utility.service';
import { RoomService } from '../../../services/room.service';
import { AuthService } from '../../../services/auth.service';
import { InvoiceService } from '../../../services/invoice.service';
import {
  ElectricRecord,
  UtilityStatus,
} from '../../../models/electricity.model';
import { Room } from '../../../models/room.model';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UtilitySource } from '../../../models/electricity.model';
import { registerLocaleData } from '@angular/common';
import localeVi from '@angular/common/locales/vi';

describe('ElectricityManagementComponent', () => {
  let component: ElectricityManagementComponent;
  let fixture: ComponentFixture<ElectricityManagementComponent>;

  let mockUtilityService: any;
  let mockRoomService: any;
  let mockAuthService: any;
  let mockInvoiceService: any;

  beforeEach(async () => {
    registerLocaleData(localeVi);
    mockUtilityService = {
      getAll: jasmine.createSpy('getAll').and.returnValue(of([])),
      create: jasmine.createSpy('create').and.returnValue(of({})),
      update: jasmine.createSpy('update').and.returnValue(of({})),
      markPaid: jasmine.createSpy('markPaid').and.returnValue(of({})),
      delete: jasmine.createSpy('delete').and.returnValue(of({})),
    };

    mockRoomService = {
      getMyRooms: jasmine.createSpy('getMyRooms').and.returnValue(of([])),
    };

    mockInvoiceService = {
      getAll: jasmine.createSpy('getAll').and.returnValue(of([])),
    };

    mockAuthService = {};

    await TestBed.configureTestingModule({
      // ⚠️ Standalone component + các module nó dùng
      imports: [
        ElectricityManagementComponent,
        CommonModule,
        FormsModule,
        CurrencyPipe,
        DatePipe,
      ],
      providers: [
        { provide: UtilityService, useValue: mockUtilityService },
        { provide: RoomService, useValue: mockRoomService },
        { provide: InvoiceService, useValue: mockInvoiceService },
        { provide: AuthService, useValue: mockAuthService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ElectricityManagementComponent);
    component = fixture.componentInstance;

    // 👉 Khởi tạo dữ liệu ban đầu cho signal
    component.roomList.set([]);
    component.records.set([]);
    component.filters.set({ month: '', status: 'ALL', keyword: '' });
    component.form.set({
      id: 0,
      roomId: 0,
      name: '',
      oldIndex: 0,
      newIndex: 0,
      unitPrice: 3500,
      totalAmount: 0,
      month: '',
      status: UtilityStatus.UNPAID,
      source: UtilitySource.SYSTEM,
      fullName: '',
    });

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
   * - Chuyển về chữ thường
   */
  it('removeAccents should remove accents and lowercase string', () => {
    const input = 'Nguyễn Văn Ánh';
    const output = component.removeAccents(input);
    expect(output).toBe('nguyen van anh');
  });

  /**
   * TC03: recalculate()
   * - Tính totalAmount = (newIndex - oldIndex) * unitPrice
   */
  it('recalculate should compute totalAmount correctly', () => {
    component.form.set({
      oldIndex: 10,
      newIndex: 15,
      unitPrice: 1000,
      totalAmount: 0,
      id: 0,
      roomId: 0,
      name: '',
      month: '',
      status: UtilityStatus.UNPAID,
      source: UtilitySource.SYSTEM,
      fullName: '',
    });

    component.recalculate();

    expect(component.form().totalAmount).toBe(5000);
  });

  /**
   * TC04: filteredRecords()
   * - Lọc danh sách theo keyword (tên phòng)
   */
  it('filteredRecords should filter by keyword', () => {
    component.roomList.set([
      { id: 1, name: 'Phong 1', status: 'OCCUPIED' },
    ] as Room[]);

    component.records.set([
      {
        roomId: 1,
        fullName: 'Nguyen Van A',
        month: '2026-01',
        status: UtilityStatus.UNPAID,
      } as ElectricRecord,
      {
        roomId: 2,
        fullName: 'Tran Van B',
        month: '2026-01',
        status: UtilityStatus.UNPAID,
      } as ElectricRecord,
    ]);

    component.filters.update((f) => ({ ...f, keyword: 'phong 1' }));

    const filtered = component.filteredRecords();

    expect(filtered.length).toBe(1);
    expect(filtered[0].roomId).toBe(1);
  });

  /**
   * TC05: stats()
   * - Tính tổng doanh thu
   * - Tổng số hóa đơn
   * - Số hóa đơn chưa thanh toán
   * - Số hóa đơn đã thanh toán
   */
  it('stats should compute totalRevenue, totalRecords, unpaidBills, paidBills correctly', () => {
    component.records.set([
      {
        roomId: 1,
        totalAmount: 1000,
        status: UtilityStatus.UNPAID,
        month: '2026-01',
      } as ElectricRecord,
      {
        roomId: 2,
        totalAmount: 2000,
        status: UtilityStatus.PAID,
        month: '2026-01',
      } as ElectricRecord,
    ]);

    component.roomList.set([
      { id: 1, name: 'Phong 1', status: 'OCCUPIED' },
      { id: 2, name: 'Phong 2', status: 'OCCUPIED' },
    ] as Room[]);

    component.filters.update((f) => ({ ...f, status: 'ALL', keyword: '' }));

    const stats = component.stats();

    expect(stats.totalRevenue).toBe(3000);
    expect(stats.totalRecords).toBe(2);
    expect(stats.unpaidBills).toBe(1);
    expect(stats.paidBills).toBe(1);
  });
});
