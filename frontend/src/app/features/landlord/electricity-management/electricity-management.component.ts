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
  // ===============================
  // CONSTANTS
  // ===============================
  readonly UtilityStatus = UtilityStatus;
  readonly FIXED_UNIT_PRICE = DEFAULT_UNIT_PRICE;

  currentDateTime = new Date();
  months = signal<string[]>([]);
  roomList = signal<Room[]>([]);
  currentTenantName = signal<string>('');

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
    oldIndex: 0,
    newIndex: 0,
    unitPrice: DEFAULT_UNIT_PRICE,
    totalAmount: 0,
    month: '',
    status: UtilityStatus.UNPAID,
    source: UtilitySource.SYSTEM,
    tenantName: '',
  });

  isModalOpen = signal(false);
  isEditMode = signal(false);
  isConfirmModalOpen = signal(false);
  isDeleteMode = signal(false);
  recordToConfirm = signal<ElectricRecord | null>(null);

  constructor(
    private utilityService: UtilityService,
    private roomService: RoomService
  ) {}

  ngOnInit() {
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
    this.roomService.getAllRooms().subscribe({
      next: (rooms: Room[]) => {
        this.roomList.set(rooms);
      },
      error: (err: any) => console.error('Failed to load rooms', err),
    });
  }

  // ===============================
  // UTILITY: XÓA DẤU TIẾNG VIỆT
  // ===============================
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

    return this.records().filter((r) => {
      const matchMonth = !f.month || r.month === f.month;
      const matchStatus = f.status === 'ALL' || r.status === f.status;

      const roomInfo = this.roomList().find((room) => room.id === r.roomId);
      const roomName = roomInfo ? roomInfo.name : `Phòng ${r.roomId}`;

      const normalizedRoomName = this.removeAccents(roomName);
      const normalizedTenantName = this.removeAccents(r.tenantName || '');
      const roomIdStr = r.roomId.toString();

      const formattedCode = 'p' + r.roomId.toString().padStart(3, '0');

      const matchSearch =
        keyword === '' ||
        roomIdStr.includes(keyword) ||
        formattedCode.includes(keyword) ||
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
    if (f.newIndex < f.oldIndex) return;

    const usage = f.newIndex - f.oldIndex;
    const total = usage * f.unitPrice;

    this.form.update((curr) => ({ ...curr, totalAmount: total }));
  }
  updateUnitPrice(value: string) {
    const price = +value;
    this.form.update((f) => ({ ...f, unitPrice: price }));
    this.recalculate();
  }

  // ===============================
  // FORM UPDATERS
  // ===============================
  updateRoomId(value: string) {
    const roomId = +value;
    const selectedRoom = this.roomList().find((r) => r.id === roomId);
    const tenantName = selectedRoom?.tenantName || '';
    this.form.update((f) => ({
      ...f,
      roomId: roomId,
      tenantName: tenantName,
    }));
    this.currentTenantName.set(tenantName);
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

    if (data.roomId === 0) {
      alert('Vui lòng chọn phòng!');
      return;
    }

    if (this.isEditMode()) {
      this.utilityService.update(data.id, data).subscribe(() => {
        this.loadData();
        this.closeModal();
      });
    } else {
      this.utilityService.create(data).subscribe(() => {
        this.loadData();
        this.closeModal();
      });
    }
  }

  deleteRecord() {
    const id = this.recordToConfirm()?.id;
    if (!id) return;
    this.utilityService.delete(id).subscribe(() => {
      this.loadData();
      this.closeConfirmModal();
    });
  }

  // Chỉ còn Delete
  onConfirmAction() {
    const id = this.recordToConfirm()?.id;
    if (!id) return;
    this.utilityService.delete(id).subscribe(() => {
      this.loadData();
      this.closeConfirmModal();
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
      oldIndex: 0,
      newIndex: 0,
      unitPrice: DEFAULT_UNIT_PRICE,
      totalAmount: 0,
      month: currentMonthStr,
      status: UtilityStatus.UNPAID,
      source: UtilitySource.SYSTEM,
      tenantName: '',
    });
    this.currentTenantName.set('');

    this.isEditMode.set(false);
    this.isModalOpen.set(true);
  }

  openEditModal(record: ElectricRecord) {
    this.form.set({ ...record });
    this.currentTenantName.set(record.tenantName || '');

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
      'Phòng',
      'Khách thuê',
      'Tháng',
      'Chỉ số cũ',
      'Chỉ số mới',
      'Tiêu thụ (kWh)',
      'Đơn giá',
      'Thành tiền',
      'Trạng thái',
    ].join(',');
    const rows = data
      .map((r) => {
        const statusText =
          r.status === this.UtilityStatus.PAID
            ? 'Đã thanh toán'
            : 'Chưa thanh toán';

        const safeTenantName = r.tenantName ? `"${r.tenantName}"` : '---';

        return [
          `Phòng ${r.roomId}`,
          safeTenantName,
          r.month,
          r.oldIndex,
          r.newIndex,
          r.newIndex - r.oldIndex,
          r.unitPrice,
          r.totalAmount,
          statusText,
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
