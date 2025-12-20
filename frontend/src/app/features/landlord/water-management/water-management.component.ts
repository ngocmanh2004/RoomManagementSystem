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
  roomList = signal<Room[]>([]);
  records = signal<WaterRecord[]>([]);
  currentTenantName = signal<string>('');

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
    roomName: '',
    oldIndex: 0,
    newIndex: 0,
    usage: 0,
    unitPrice: DEFAULT_WATER_PRICE,
    totalAmount: 0,
    month: '',
    status: UtilityStatus.UNPAID,
    tenantName: '',
  });

  isModalOpen = signal(false);
  isEditMode = signal(false);
  isConfirmModalOpen = signal(false);
  recordToConfirm = signal<WaterRecord | null>(null);

  // ===============================
  // CONSTRUCTOR: THEO DÕI THAY ĐỔI
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
    this.roomService.getAllRooms().subscribe({
      next: (rooms) => this.roomList.set(rooms),
      error: (err) => console.error('Lỗi tải phòng:', err),
    });
  }

  // ===============================
  // SMART CHART LOGIC (TỰ ĐỘNG THAY ĐỔI)
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
        const nameA = a.roomName || '';
        const nameB = b.roomName || '';
        return nameA.localeCompare(nameB, 'vi', { numeric: true });
      });

      const labels = sortedByRoom.map((r) => {
        let label = r.roomName || `P.${r.roomId}`;
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
              backgroundColor: 'rgba(6, 182, 212, 0.7)', // Cyan đậm
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
            tooltip: { callbacks: { label: (c: any) => ` ${c.formattedValue} m³` } },
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
    const keyword = this.removeAccents(f.keyword).replace(/\s/g, '');

    return this.records().filter((r) => {
      const matchMonth = !f.month || r.month === f.month;
      const matchStatus = f.status === 'ALL' || r.status === f.status;
      const matchRoomIdDropdown = f.roomId === 0 || r.roomId == f.roomId;

      // Search Logic
      const normalizedRoomName = this.removeAccents(r.roomName || '');
      const normalizedTenantName = this.removeAccents(r.tenantName || '');
      const roomIdStr = r.roomId.toString();
      const formattedCode = 'p' + r.roomId.toString().padStart(3, '0');

      const matchSearch =
        keyword === '' ||
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
  updateRoomId(val: string) {
    const rId = +val;
    const room = this.roomList().find((r) => r.id === rId);
    this.form.update((f) => ({
      ...f,
      roomId: rId,
      roomName: room?.name || '',
      tenantName: room?.tenantName || '',
    }));
    this.currentTenantName.set(room?.tenantName || '');
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
    if (!data.roomId) {
      alert('Chưa chọn phòng!');
      return;
    }

    const obs$ = this.isEditMode()
      ? this.waterService.update(data.id, data)
      : this.waterService.create(data);

    obs$.subscribe({
      next: () => {
        this.loadData();
        this.closeModal();
        alert(this.isEditMode() ? 'Cập nhật xong!' : 'Thêm mới xong!');
      },
      error: (err) => {
        if (!this.isEditMode() && err.status === 400)
          alert('Phòng này tháng này đã ghi nước rồi!');
        else alert('Có lỗi xảy ra!');
      },
    });
  }

  onConfirmAction() {
    const rec = this.recordToConfirm();
    if (rec)
      this.waterService.delete(rec.id).subscribe(() => {
        this.loadData();
        this.closeConfirmModal();
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
      roomName: '',
      oldIndex: 0,
      newIndex: 0,
      usage: 0,
      unitPrice: DEFAULT_WATER_PRICE,
      totalAmount: 0,
      month: m,
      status: UtilityStatus.UNPAID,
      tenantName: '',
    });
    this.currentTenantName.set('');
    this.isEditMode.set(false);
    this.isModalOpen.set(true);
  }

  openEditModal(r: WaterRecord) {
    this.form.set({ ...r });
    this.currentTenantName.set(r.tenantName || '');
    this.isEditMode.set(true);
    this.isModalOpen.set(true);
  }

  closeModal() {
    this.isModalOpen.set(false);
  }
  openDeleteModal(r: WaterRecord) {
    this.recordToConfirm.set(r);
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
      'Phòng,Khách thuê,Tháng,Chỉ số cũ,Chỉ số mới,Tiêu thụ,Đơn giá,Thành tiền,Trạng thái\n';
    const rows = data
      .map(
        (r) =>
          `${r.roomName},"${r.tenantName || '--'}",${r.month},${r.oldIndex},${
            r.newIndex
          },${r.newIndex - r.oldIndex},${r.unitPrice},${r.totalAmount},${
            r.status === UtilityStatus.PAID ? 'Đã thu' : 'Chưa thu'
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
