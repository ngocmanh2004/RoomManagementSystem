import {
  ChangeDetectionStrategy,
  Component,
  signal,
  computed,
  OnInit,
  inject,
  AfterViewInit,
  OnDestroy,
  ViewChild,
  ElementRef,
  effect,
} from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, registerables, ChartConfiguration } from 'chart.js';
Chart.register(...registerables);
import { WaterRecord } from '../../../models/water.model';
import { UtilityStatus } from '../../../models/electricity.model';
import { Room } from '../../../models/room.model';
import { WaterService } from '../../../services/water.service';
import { RoomService } from '../../../services/room.service';

const DEFAULT_WATER_PRICE = 15000;

@Component({
  selector: 'app-water-management',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe, FormsModule],
  templateUrl: './water-management.component.html',
  styleUrl: './water-management.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WaterManagementComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  // ===============================
  // CONSTANTS & INJECTS
  // ===============================
  readonly UtilityStatus = UtilityStatus;
  private waterService = inject(WaterService);
  private roomService = inject(RoomService);

  // ===============================
  // CHART VARIABLES
  // ===============================
  @ViewChild('waterUsageChart') private chartRef!: ElementRef;
  chart: Chart | null = null;

  // ===============================
  // STATE SIGNALS
  // ===============================
  currentDateTime = new Date();
  months = signal<string[]>([]);
  maxMonth: string = '';
  roomList = signal<Room[]>([]);
  records = signal<WaterRecord[]>([]);
  currentName = signal<string>('');

  filters = signal<{
    month: string;
    status: UtilityStatus | 'ALL';
    roomId: number;
    keyword: string;
  }>({
    month: '',
    status: 'ALL',
    roomId: 0,
    keyword: '',
  });

  form = signal<WaterRecord>({
    id: 0,
    roomId: 0,
    name: '',
    oldIndex: 0,
    newIndex: 0,
    usage: 0,
    unitPrice: DEFAULT_WATER_PRICE,
    totalAmount: 0,
    month: '',
    status: UtilityStatus.UNPAID,
    fullName: '',
  });

  isModalOpen = signal(false);
  isEditMode = signal(false);
  isConfirmModalOpen = signal(false);
  isDeleteMode = signal(false);
  recordToConfirm = signal<WaterRecord | null>(null);

  // ===============================
  // CONSTRUCTOR
  // ===============================
  constructor() {
    effect(() => {
      const records = this.filteredRecords();
      const currentFilters = this.filters();

      this.renderSmartChart(records, currentFilters);
    });
  }

  // ===============================
  // LIFECYCLE
  // ===============================
  ngOnInit() {
    const now = new Date();
    this.maxMonth = `${now.getFullYear()}-${(now.getMonth() + 1)
      .toString()
      .padStart(2, '0')}`;
    this.loadData();
    this.loadRooms();
  }

  ngAfterViewInit() {
    this.renderSmartChart(this.filteredRecords(), this.filters());
  }

  ngOnDestroy() {
    if (this.chart) {
      this.chart.destroy();
    }
  }

  // ===============================
  // LOAD DATA
  // ===============================
  loadData() {
    this.waterService.getAll().subscribe({
      next: (data) => {
        this.records.set(data);
        const uniqueMonths = Array.from(new Set(data.map((r) => r.month)))
          .filter(Boolean)
          .sort()
          .reverse();
        this.months.set(uniqueMonths);
      },
      error: (err) => console.error('Lỗi tải dữ liệu:', err),
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

  // ===============================
  // SMART CHART LOGIC
  // ===============================
  renderSmartChart(records: WaterRecord[], currentFilters: any) {
    if (!this.chartRef) return;

    if (this.chart) {
      this.chart.destroy();
    }

    const ctx = this.chartRef.nativeElement.getContext('2d');
    let config: ChartConfiguration;

    const isViewingSingleMonth = !!currentFilters.month;

    if (isViewingSingleMonth) {
      const sortedByRoom = [...records].sort((a, b) => {
        const nameA = a.name || '';
        const nameB = b.name || '';
        return nameA.localeCompare(nameB, 'vi', { numeric: true });
      });

      const labels = sortedByRoom.map((r) => {
        let label = r.name || `P.${r.roomId}`;
        label = label.replace(/^(Phòng|Phong|P\.|P)\s*/i, '');
        return `P.${label}`;
      });

      const data = sortedByRoom.map((r) => r.newIndex - r.oldIndex);

      config = {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [
            {
              label: `Tiêu thụ T${currentFilters.month.split('-')[1]} (m³)`,
              data: data,
              backgroundColor: 'rgba(6, 182, 212, 0.7)',
              borderColor: '#06b6d4',
              borderWidth: 1,
              borderRadius: 4,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            x: {
              ticks: { autoSkip: true, maxRotation: 90 },
            },
          },
        },
      };
    } else {
      const groupedData = this.groupDataByMonth(records);

      config = {
        type: 'line',
        data: {
          labels: groupedData.labels,
          datasets: [
            {
              label: 'Tổng tiêu thụ (m³)',
              data: groupedData.values,
              borderColor: '#06b6d4',
              backgroundColor: 'rgba(6, 182, 212, 0.2)',
              fill: true,
              tension: 0.4,
              pointRadius: 4,
              pointBackgroundColor: '#fff',
              pointBorderColor: '#06b6d4',
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: true, position: 'top' },
            tooltip: {
              callbacks: { label: (c: any) => ` ${c.formattedValue} m³` },
            },
          },
          scales: {
            y: { beginAtZero: true },
            x: { grid: { display: false } },
          },
        },
      };
    }

    this.chart = new Chart(ctx, config);
  }

  private groupDataByMonth(records: WaterRecord[]) {
    const map = new Map<string, number>();

    records.forEach((r) => {
      const currentVal = map.get(r.month) || 0;
      const usage = r.newIndex - r.oldIndex;
      map.set(r.month, currentVal + usage);
    });

    const sortedEntries = Array.from(map.entries()).sort((a, b) =>
      a[0].localeCompare(b[0])
    );

    const labels = sortedEntries.map((e) => {
      const [year, month] = e[0].split('-');
      return `T${month}/${year}`;
    });

    const values = sortedEntries.map((e) => e[1]);

    return { labels, values };
  }

  // ===============================
  // HELPER & COMPUTED
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

  filteredRecords = computed(() => {
    const f = this.filters();
    const keyword = this.removeAccents(f.keyword);
    const rooms = this.roomList();

    return this.records().filter((r) => {
      const matchMonth = !f.month || r.month === f.month;
      const matchStatus = f.status === 'ALL' || r.status === f.status;
      const matchRoomIdDropdown = f.roomId === 0 || r.roomId == f.roomId;

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
        roomIdStr.includes(keyword) ||
        formattedCode.includes(keyword) ||
        normalizedRoomName.includes(keyword) ||
        normalizedTenantName.includes(keyword);

      return matchMonth && matchStatus && matchRoomIdDropdown && matchSearch;
    });
  });

  stats = computed(() => {
    const list = this.filteredRecords();
    const totalRevenue = list.reduce((sum, r) => sum + r.totalAmount, 0);
    const totalUsage = list.reduce(
      (sum, r) => sum + (r.newIndex - r.oldIndex),
      0
    );
    const totalRecords = list.length;
    const unpaidBills = list.filter(
      (r) => r.status === UtilityStatus.UNPAID
    ).length;
    const paidBills = totalRecords - unpaidBills;
    return { totalRevenue, totalUsage, totalRecords, unpaidBills, paidBills };
  });

  // ===============================
  // FORM LOGIC
  // ===============================
  recalculate() {
    const f = this.form();
    if (f.newIndex < f.oldIndex) return;
    const usage = f.newIndex - f.oldIndex;
    const total = usage * f.unitPrice;
    this.form.update((curr) => ({ ...curr, usage, totalAmount: total }));
  }

  updateUnitPrice(val: string) {
    this.form.update((f) => ({ ...f, unitPrice: +val }));
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

  updateMonth(val: string) {
    this.form.update((f) => ({ ...f, month: val }));
  }
  updateOldIndex(val: string) {
    this.form.update((f) => ({ ...f, oldIndex: +val }));
    this.recalculate();
  }
  updateNewIndex(val: string) {
    this.form.update((f) => ({ ...f, newIndex: +val }));
    this.recalculate();
  }

  // ===============================
  // CRUD & HANDLERS
  // ===============================
  submitForm() {
    const data = this.form();
    if (data.newIndex < data.oldIndex) {
      alert('Lỗi: Chỉ số mới phải >= chỉ số cũ!');
      return;
    }
    if (!this.isEditMode() && data.newIndex === data.oldIndex) {
      alert('Chỉ số mới bằng chỉ số cũ, không cần tạo bản ghi mới.');
      return;
    }
    if (!data.roomId) {
      alert('Vui lòng chọn phòng!');
      return;
    }

    if (!data.month) {
      alert('Vui lòng chọn tháng!');
      return;
    }

    const obs$ = this.isEditMode()
      ? this.waterService.update(data.id, data)
      : this.waterService.create(data);

    obs$.subscribe({
      next: () => {
        this.loadData();
        this.closeModal();
        alert(
          this.isEditMode()
            ? 'Cập nhật hóa đơn nước thành công!'
            : 'Thêm hóa đơn nước thành công!'
        );
      },
      error: (err) => {
        if (!this.isEditMode() && err.status === 400)
          alert('Phòng này tháng này đã ghi nước rồi!');
        else alert('Có lỗi xảy ra!');
      },
    });
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

    this.waterService.markPaid(id).subscribe({
      next: () => {
        this.loadData();
        this.closeConfirmModal();
        alert('Đã xác nhận thanh toán!');
      },
      error: (err) => {
        console.error(err);
        alert('Có lỗi khi xác nhận thanh toán!');
      },
    });
  }

  deleteRecord() {
    const id = this.recordToConfirm()?.id;
    if (!id) return;

    this.waterService.delete(id).subscribe(() => {
      this.loadData();
      this.closeConfirmModal();
      alert('Đã xóa bản ghi!');
    });
  }

  onFilterMonth(e: Event) {
    this.filters.update((f) => ({
      ...f,
      month: (e.target as HTMLSelectElement).value,
    }));
  }
  onFilterRoom(e: Event) {
    this.filters.update((f) => ({
      ...f,
      roomId: +(e.target as HTMLSelectElement).value,
    }));
  }
  onFilterStatus(e: Event) {
    this.filters.update((f) => ({
      ...f,
      status: (e.target as HTMLSelectElement).value as any,
    }));
  }
  onSearch(val: string) {
    this.filters.update((f) => ({ ...f, keyword: val }));
  }

  openAddModal() {
    const now = new Date();
    const m = `${now.getFullYear()}-${(now.getMonth() + 1)
      .toString()
      .padStart(2, '0')}`;
    this.form.set({
      id: 0,
      roomId: 0,
      name: '',
      oldIndex: 0,
      newIndex: 0,
      usage: 0,
      unitPrice: DEFAULT_WATER_PRICE,
      totalAmount: 0,
      month: m,
      status: UtilityStatus.UNPAID,
      fullName: '',
    });
    this.currentName.set('');
    this.isEditMode.set(false);
    this.isModalOpen.set(true);
  }

  openEditModal(record: WaterRecord) {
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
  openConfirmPaymentModal(record: WaterRecord) {
    this.recordToConfirm.set(record);
    this.isDeleteMode.set(false);
    this.isConfirmModalOpen.set(true);
  }
  openDeleteModal(record: WaterRecord) {
    if (record.status === UtilityStatus.PAID) {
      alert('Hóa đơn đã thanh toán nên không thể xóa!');
      return;
    }

    this.recordToConfirm.set(record);
    this.isDeleteMode.set(true);
    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal() {
    this.isConfirmModalOpen.set(false);
    this.recordToConfirm.set(null);
  }

  onGenerateReport() {
    const data = this.filteredRecords();
    if (!data.length) return alert('Không có dữ liệu!');
    const header =
      'Tên Phòng,Khách thuê,Tháng,Chỉ số cũ,Chỉ số mới,Tiêu thụ,Đơn giá,Thành tiền,Trạng thái\n';
    const rows = data
      .map(
        (r) =>
          `${r.name},"${r.fullName || '--'}",${r.month},${r.oldIndex},${
            r.newIndex
          },${r.newIndex - r.oldIndex},${r.unitPrice},${r.totalAmount},${
            r.status === UtilityStatus.PAID
              ? 'Đã thanh toán'
              : 'Chưa thanh toán'
          }`
      )
      .join('\n');
    const blob = new Blob(['\uFEFF' + header + rows], {
      type: 'text/csv;charset=utf-8;',
    });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `Nuoc_${new Date().toISOString().slice(0, 10)}.csv`;
    link.click();
  }
}
