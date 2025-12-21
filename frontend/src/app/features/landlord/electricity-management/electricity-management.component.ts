import {
  ChangeDetectionStrategy,
  Component,
  signal,
  computed,
  OnInit,
} from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  ElectricRecord,
  UtilityStatus,
  UtilitySource,
  FilterState,
} from '../../../models/electricity.model';
import { Room } from '../../../models/room.model';
import { RoomService } from '../../../services/room.service';
import { UtilityService } from '../../../services/utility.service';
import { AuthService } from '../../../services/auth.service';

const DEFAULT_UNIT_PRICE = 3500;

@Component({
  selector: 'app-electricity-management',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe, FormsModule],
  templateUrl: './electricity-management.component.html',
  styleUrl: './electricity-management.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ElectricityManagementComponent implements OnInit {
  readonly UtilityStatus = UtilityStatus;
  readonly FIXED_UNIT_PRICE = DEFAULT_UNIT_PRICE;

  currentDateTime = new Date();
  months = signal<string[]>([]);
  maxMonth: string = '';
  roomList = signal<Room[]>([]);
  currentName = signal<string>('');

  records = signal<ElectricRecord[]>([]);

  filters = signal<{
    month: string;
    status: UtilityStatus | 'ALL';
    keyword: string;
  }>({
    month: '',
    status: 'ALL',
    keyword: '',
  });

  form = signal<ElectricRecord>({
    id: 0,
    roomId: 0,
    name: '',
    oldIndex: 0,
    newIndex: 0,
    unitPrice: DEFAULT_UNIT_PRICE,
    totalAmount: 0,
    month: '',
    status: UtilityStatus.UNPAID,
    source: UtilitySource.SYSTEM,
    fullName: '',
  });

  isModalOpen = signal(false);
  isEditMode = signal(false);
  isConfirmModalOpen = signal(false);
  isDeleteMode = signal(false);
  recordToConfirm = signal<ElectricRecord | null>(null);

  constructor(
    private utilityService: UtilityService,
    private authService: AuthService,
    private roomService: RoomService
  ) {}

  ngOnInit() {
    const now = new Date();
    this.maxMonth = `${now.getFullYear()}-${(now.getMonth() + 1)
      .toString()
      .padStart(2, '0')}`;

    this.loadData();
    this.loadRooms();
  }

  // ===============================
  // LOAD DATA
  // ===============================
  loadData() {
    this.utilityService.getAll().subscribe((data) => {
      this.records.set(data);

      const uniqueMonths = Array.from(new Set(data.map((r) => r.month)))
        .filter(Boolean)
        .sort()
        .reverse();

      this.months.set(uniqueMonths);
    });
  }

  loadRooms() {
    this.roomService.getMyRooms().subscribe({
      next: (rooms: Room[]) => {
        this.roomList.set(rooms.filter((r) => r.status === 'OCCUPIED'));
      },
      error: (err) => {
        console.error('Failed to load rooms', err);
        this.roomList.set([]);
      },
    });
  }

  removeAccents(str: string): string {
    if (!str) return '';
    return str
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/đ/g, 'd')
      .replace(/Đ/g, 'D')
      .toLowerCase()
      .trim();
  }

  // ===============================
  // COMPUTED: FILTER + SEARCH
  // ===============================
  filteredRecords = computed(() => {
    const f = this.filters();
    const keyword = this.removeAccents(f.keyword);
    const rooms = this.roomList();

    return this.records().filter((r) => {
      if (r.roomId == null) return false;

      const matchMonth = !f.month || r.month === f.month;
      const matchStatus = f.status === 'ALL' || r.status === f.status;

      const roomInfo = this.roomList().find((room) => room.id === r.roomId);
      const roomName = roomInfo ? roomInfo.name : `Phòng ${r.roomId}`;

      const normalizedRoomName = this.removeAccents(roomName);
      const normalizedTenantName = this.removeAccents(r.fullName || '');
      const roomIdStr = r.roomId.toString();
      const formattedCode = 'p' + r.roomId.toString().padStart(3, '0');

      const matchSearch =
        keyword === '' ||
        (r.roomId != null && r.roomId.toString().includes(keyword)) ||
        (r.roomId != null &&
          ('p' + r.roomId.toString().padStart(3, '0')).includes(keyword)) ||
        normalizedRoomName.includes(keyword) ||
        normalizedTenantName.includes(keyword);

      return matchMonth && matchStatus && matchSearch;
    });
  });

  stats = computed(() => {
    const list = this.filteredRecords();
    const totalRevenue = list.reduce((s, r) => s + r.totalAmount, 0);
    const totalRecords = list.length;
    const unpaidBills = list.filter(
      (r) => r.status === UtilityStatus.UNPAID
    ).length;
    const paidBills = totalRecords - unpaidBills;

    return { totalRevenue, totalRecords, unpaidBills, paidBills };
  });

  // ===============================
  // HELPERS
  // ===============================
  getStatusText(status: UtilityStatus): string {
    return status === UtilityStatus.PAID ? 'Đã thanh toán' : 'Chưa thanh toán';
  }

  // ===============================
  // FILTER & SEARCH EVENTS
  // ===============================
  onFilterMonth(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.filters.update((f) => ({ ...f, month: value }));
  }

  onFilterStatus(event: Event) {
    const value = (event.target as HTMLSelectElement).value as
      | UtilityStatus
      | 'ALL';
    this.filters.update((f) => ({ ...f, status: value }));
  }

  onSearch(value: string) {
    this.filters.update((f) => ({ ...f, keyword: value }));
  }

  // ===============================
  // CALCULATION LOGIC
  // ===============================
  recalculate() {
    const f = this.form();
    if (f.newIndex < f.oldIndex) {
      this.form.update((curr) => ({ ...curr, totalAmount: 0 }));
      return;
    }
    const usage = f.newIndex - f.oldIndex;
    const total = usage * f.unitPrice;

    this.form.update((curr) => ({ ...curr, totalAmount: total }));
  }

  updateUnitPrice(value: string) {
    const price = +value;
    this.form.update((f) => ({ ...f, unitPrice: price }));
    this.recalculate();
  }

  updateRoomId(value: string) {
    const roomId = +value;
    const selectedRoom = this.roomList().find((r) => r.id === roomId);
    const name = selectedRoom?.name || '';
    this.form.update((f) => ({
      ...f,
      roomId: roomId,
      name: name,
    }));
    this.currentName.set(name);
  }

  updateMonth(value: string) {
    this.form.update((f) => ({ ...f, month: value }));
  }

  updateOldIndex(value: string) {
    this.form.update((f) => ({ ...f, oldIndex: +value }));
    this.recalculate();
  }

  updateNewIndex(value: string) {
    this.form.update((f) => ({ ...f, newIndex: +value }));
    this.recalculate();
  }

  // ===============================
  // CRUD ACTIONS
  // ===============================
  submitForm() {
    const data = this.form();

    if (data.newIndex < data.oldIndex) {
      alert('Chỉ số mới không được nhỏ hơn chỉ số cũ!');
      return;
    }

    if (!this.isEditMode() && data.newIndex === data.oldIndex) {
      alert('Chỉ số mới bằng chỉ số cũ, không cần tạo bản ghi mới.');
      return;
    }

    if (data.roomId === 0) {
      alert('Vui lòng chọn phòng!');
      return;
    }

    if (!data.month) {
      alert('Vui lòng chọn tháng!');
      return;
    }

    if (this.isEditMode()) {
      this.utilityService.update(data.id, data).subscribe({
        next: () => {
          this.loadData();
          this.closeModal();
          alert('Cập nhật hóa đơn điện thành công!');
        },
        error: (err) => {
          console.error(err);
          alert('Có lỗi xảy ra khi cập nhật!');
        },
      });
    } else {
      this.utilityService.create(data).subscribe({
        next: () => {
          this.loadData();
          this.closeModal();
          alert('Thêm hóa đơn điện thành công!');
        },
        error: (err) => {
          console.error(err);
          alert('Có lỗi xảy ra khi thêm hóa đơn!');
        },
      });
    }
  }

  onConfirmAction() {
    if (this.isDeleteMode()) {
      this.deleteRecord();
    } else {
      this.confirmPayment();
    }
  }

  confirmPayment() {
    const id = this.recordToConfirm()?.id;
    if (!id) return;

    this.utilityService.markPaid(id).subscribe(() => {
      this.loadData();
      this.closeConfirmModal();
      alert('Đã xác nhận thanh toán!');
    });
  }

  deleteRecord() {
    const id = this.recordToConfirm()?.id;
    if (!id) return;

    this.utilityService.delete(id).subscribe(() => {
      this.loadData();
      this.closeConfirmModal();
      alert('Đã xóa bản ghi!');
    });
  }

  // ===============================
  // MODAL HANDLERS
  // ===============================
  openAddModal() {
    const now = new Date();
    const currentMonthStr = `${now.getFullYear()}-${(now.getMonth() + 1)
      .toString()
      .padStart(2, '0')}`;

    this.form.set({
      id: 0,
      roomId: 0,
      name: '',
      oldIndex: 0,
      newIndex: 0,
      unitPrice: DEFAULT_UNIT_PRICE,
      totalAmount: 0,
      month: currentMonthStr,
      status: UtilityStatus.UNPAID,
      source: UtilitySource.SYSTEM,
      fullName: '',
    });
    this.currentName.set('');
    this.isEditMode.set(false);
    this.isModalOpen.set(true);
  }

  openEditModal(record: ElectricRecord) {
    if (record.status === UtilityStatus.PAID) {
      alert('Bản ghi đã thanh toán, không thể chỉnh sửa!');
      return;
    }
    this.form.set({ ...record });
    this.currentName.set(record.fullName || '');
    this.isEditMode.set(true);
    this.isModalOpen.set(true);
  }

  closeModal() {
    this.isModalOpen.set(false);
  }

  openDeleteModal(record: ElectricRecord) {
    this.recordToConfirm.set(record);
    this.isDeleteMode.set(true);
    this.isConfirmModalOpen.set(true);
  }

  openConfirmPaymentModal(record: ElectricRecord) {
    this.recordToConfirm.set(record);
    this.isDeleteMode.set(false);
    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal() {
    this.isConfirmModalOpen.set(false);
    this.recordToConfirm.set(null);
  }

  // ===============================
  // EXPORT BILL (CSV/EXCEL)
  // ===============================
  onGenerateBill() {
    const data = this.filteredRecords();

    if (!data.length) {
      alert('Không có dữ liệu để xuất hóa đơn!');
      return;
    }
    const header = [
      'Tên Phòng',
      'Khách thuê',
      'Tháng',
      'Chỉ số cũ',
      'Chỉ số mới',
      'Tiêu thụ (kWh)',
      'Đơn giá',
      'Thành tiền',
      'Trạng thái',
    ].join(',');

    const csvSafe = (value: any): string => {
      if (value === null || value === undefined) return '""';
      return `"${String(value).replace(/"/g, '""')}"`;
    };

    const rows = data
      .map((r) => {
        const statusText =
          r.status === this.UtilityStatus.PAID
            ? 'Đã thanh toán'
            : 'Chưa thanh toán';

        return [
          csvSafe(r.name),
          csvSafe(r.fullName || '---'),
          csvSafe(r.month),
          csvSafe(r.oldIndex),
          csvSafe(r.newIndex),
          csvSafe(r.newIndex - r.oldIndex),
          csvSafe(r.unitPrice),
          csvSafe(r.totalAmount),
          csvSafe(statusText),
        ].join(',');
      })
      .join('\n');

    const csvContent = header + '\n' + rows;

    const blob = new Blob(['\uFEFF' + csvContent], {
      type: 'text/csv;charset=utf-8;',
    });

    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;

    link.download = `Hoa_don_dien_${new Date().toISOString().slice(0, 10)}.csv`;

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    URL.revokeObjectURL(url);
  }
}
