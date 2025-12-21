import {
  ChangeDetectionStrategy,
  Component,
  signal,
  computed,
  OnInit,
  inject,
  ViewChild,
  ElementRef,
  AfterViewInit,
  OnDestroy,
  effect,
} from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, registerables, ChartConfiguration } from 'chart.js';
Chart.register(...registerables);
import { Room } from '../../../models/room.model';
import { RoomService } from '../../../services/room.service';
import { ExtraCostService } from '../../../services/extra-cost.service';
import {
  ExtraCost,
  CostType,
  ExtraCostStatus,
} from '../../../models/extra-cost.model';

@Component({
  selector: 'app-extra-cost-management',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe, FormsModule],
  templateUrl: './extra-cost-management.component.html',
  styleUrl: './extra-cost-management.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExtraCostManagementComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  // ===============================
  // CONSTANTS & INJECTS
  // ===============================
  readonly UtilityStatus = ExtraCostStatus;
  readonly CostType = CostType;

  private extraCostService = inject(ExtraCostService);
  private roomService = inject(RoomService);

  // ===============================
  // CHART CONFIG
  // ===============================
  @ViewChild('costTypeChart') private chartRef!: ElementRef;
  chart: any;

  readonly CHART_COLORS: Record<string, string> = {
    INTERNET: '#f97316',
    GARBAGE: '#3b82f6',
    MAINTENANCE: '#ef4444',
    OTHERS: '#9ca3af',
  };

  // ===============================
  // STATE SIGNALS
  // ===============================
  currentDateTime = new Date();
  months = signal<string[]>([]);
  maxMonth: string = '';
  roomList = signal<Room[]>([]);
  records = signal<ExtraCost[]>([]);
  currentName = signal<string>('');

  filters = signal<{
    month: string;
    roomId: number;
    type: CostType | 'ALL';
    status: ExtraCostStatus | 'ALL';
    keyword: string;
  }>({
    month: '',
    roomId: 0,
    type: 'ALL',
    status: 'ALL',
    keyword: '',
  });

  form = signal<ExtraCost>({
    id: 0,
    roomId: 0,
    name: '',
    type: CostType.OTHERS,
    amount: 0,
    month: '',
    description: '',
    status: ExtraCostStatus.UNPAID,
  });

  isModalOpen = signal(false);
  isEditMode = signal(false);
  isConfirmModalOpen = signal(false);
  isDeleteMode = signal(false);
  recordToConfirm = signal<ExtraCost | null>(null);
  actionType = signal<'DELETE' | 'PAY' | null>(null);

  // ===============================
  // CONSTRUCTOR
  // ===============================
  constructor() {
    effect(() => {
      const _ = this.filteredRecords();
      if (this.chart || this.chartRef) {
        this.renderSmartChart();
      }
    });
  }

  ngOnInit() {
    const now = new Date();
    this.maxMonth = `${now.getFullYear()}-${(now.getMonth() + 1)
      .toString()
      .padStart(2, '0')}`;
    this.loadRooms();
    this.loadData();
  }

  ngAfterViewInit() {
    this.renderSmartChart();
  }

  ngOnDestroy() {
    if (this.chart) this.chart.destroy();
  }

  // ===============================
  // LOAD DATA
  // ===============================
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

  loadData() {
    this.extraCostService.getAll().subscribe({
      next: (data) => {
        this.records.set(data);
        const uniqueMonths = Array.from(new Set(data.map((r) => r.month)))
          .filter(Boolean)
          .sort()
          .reverse();
        this.months.set(uniqueMonths);
      },
      error: (err) => console.error('Lỗi tải chi phí:', err),
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
  // COMPUTED LOGIC
  // ===============================

  filteredRecords = computed(() => {
    const f = this.filters();
    const keyword = this.removeAccents(f.keyword);
    const rooms = this.roomList();

    return this.records().filter((r) => {
      const matchMonth = !f.month || r.month === f.month;
      const matchRoom = f.roomId === 0 || r.roomId === f.roomId;
      const matchStatus = f.status === 'ALL' || r.status === f.status;

      const roomInfo = this.roomList().find((room) => room.id === r.roomId);
      const roomName = roomInfo ? roomInfo.name : `Phòng ${r.roomId}`;

      const normalizedRoomName = this.removeAccents(roomName);
      const normalizedTenantName = this.removeAccents(r.fullName || '');
      const roomIdStr = r.roomId.toString();
      const formattedCode = 'p' + r.roomId.toString().padStart(3, '0');
      const matchType = f.type === 'ALL' || r.type === f.type;
      const matchSearch =
        keyword === '' ||
        (r.roomId != null && r.roomId.toString().includes(keyword)) ||
        (r.roomId != null &&
          ('p' + r.roomId.toString().padStart(3, '0')).includes(keyword)) ||
        normalizedRoomName.includes(keyword) ||
        normalizedTenantName.includes(keyword);

      r.code?.toLowerCase().includes(keyword) ||
        r.name.toLowerCase().includes(keyword);

      return matchMonth && matchRoom && matchStatus && matchType && matchSearch;
    });
  });

  stats = computed(() => {
    const list = this.filteredRecords();
    const totalRevenue = list.reduce((sum, r) => sum + r.amount, 0);
    const unpaidBills = list.filter(
      (r) => r.status === ExtraCostStatus.UNPAID
    ).length;

    return {
      totalRevenue,
      totalRecords: list.length,
      unpaidBills,
      paidBills: list.length - unpaidBills,
    };
  });

  costAnalysis = computed(() => {
    const list = this.filteredRecords();
    const total = list.reduce((sum, r) => sum + r.amount, 0) || 1;

    const grouped = list.reduce((acc, curr) => {
      acc[curr.type] = (acc[curr.type] || 0) + curr.amount;
      return acc;
    }, {} as Record<string, number>);

    const trendMap: Record<string, Record<string, number>> = {};
    const allMonths = Array.from(new Set(list.map((r) => r.month))).sort();

    list.forEach((r) => {
      if (!trendMap[r.type]) trendMap[r.type] = {};
      trendMap[r.type][r.month] = (trendMap[r.type][r.month] || 0) + r.amount;
    });

    return Object.keys(grouped)
      .map((type) => {
        const amount = grouped[type];

        const monthlyData = trendMap[type] || {};
        const values = allMonths.map((m) => monthlyData[m] || 0);
        const maxValue = Math.max(...values) || 1;

        const trendBars = values.map((v) => Math.round((v / maxValue) * 100));

        return {
          type: type,
          label: this.getCostTypeName(type),
          amount: amount,
          percentage: ((amount / total) * 100).toFixed(1),
          icon: this.getCostTypeIcon(type),
          colorClass: this.getCostTypeColorClass(type),
          colorHex: this.CHART_COLORS[type],
          trendBars: trendBars,
          trendMonths: allMonths,
        };
      })
      .sort((a, b) => b.amount - a.amount);
  });

  // ===============================
  // CHART LOGIC IMPLEMENTATION
  // ===============================
  renderSmartChart() {
    if (!this.chartRef) return;

    const isViewingSingleMonth = !!this.filters().month;

    if (this.chart) this.chart.destroy();

    const ctx = this.chartRef.nativeElement.getContext('2d');

    let config: ChartConfiguration;

    if (isViewingSingleMonth) {
      const analysisData = this.costAnalysis();

      const doughnutConfig: ChartConfiguration<'doughnut'> = {
        type: 'doughnut',
        data: {
          labels: analysisData.map((d) => d.label),
          datasets: [
            {
              data: analysisData.map((d) => d.amount),
              backgroundColor: analysisData.map(
                (d) => this.CHART_COLORS[d.type]
              ),
              borderWidth: 0,
              hoverOffset: 4,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '75%',
          plugins: {
            legend: { display: false },
            tooltip: {
              callbacks: {
                label: (c) =>
                  ` ${new Intl.NumberFormat('vi-VN').format(
                    c.raw as number
                  )} đ`,
              },
            },
          },
        },
      };
      config = doughnutConfig;
    } else {
      const chartData = this.prepareStackedData(this.filteredRecords());

      const barConfig: ChartConfiguration<'bar'> = {
        type: 'bar',
        data: {
          labels: chartData.labels,
          datasets: chartData.datasets,
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: true, position: 'top' },
            tooltip: {
              callbacks: {
                label: (c) =>
                  ` ${c.dataset.label}: ${new Intl.NumberFormat('vi-VN').format(
                    c.raw as number
                  )} đ`,
              },
            },
          },
          scales: {
            x: { stacked: true, grid: { display: false } },
            y: {
              stacked: true,
              beginAtZero: true,
              ticks: { callback: (v) => (v as number) / 1000 + 'k' },
            },
          },
        },
      };
      config = barConfig;
    }

    this.chart = new Chart(ctx, config);
  }
  prepareStackedData(records: ExtraCost[]) {
    const uniqueMonths = Array.from(
      new Set(records.map((r) => r.month))
    ).sort();
    const labels = uniqueMonths.map(
      (m) => `T${m.split('-')[1]}/${m.split('-')[0].slice(2)}`
    );
    const types = ['INTERNET', 'GARBAGE', 'MAINTENANCE', 'OTHERS'];

    const datasets = types.map((type) => {
      const data = uniqueMonths.map((month) => {
        return records
          .filter((r) => r.month === month && r.type === type)
          .reduce((sum, r) => sum + r.amount, 0);
      });
      return {
        label: this.getCostTypeName(type),
        data: data,
        backgroundColor: this.CHART_COLORS[type],
        barPercentage: 0.6,
        borderRadius: 2,
      };
    });
    return { labels, datasets };
  }

  // ===============================
  // HELPER FUNCTIONS (UI)
  // ===============================
  getCostTypeName(type: string): string {
    const map: Record<string, string> = {
      INTERNET: 'Internet / Wifi',
      GARBAGE: 'Rác thải',
      MAINTENANCE: 'Bảo trì',
      OTHERS: 'Khác',
    };
    return map[type] || type;
  }

  getCostTypeIcon(type: string): string {
    const map: Record<string, string> = {
      INTERNET: 'fa-solid fa-wifi',
      GARBAGE: 'fa-solid fa-trash-can',
      MAINTENANCE: 'fa-solid fa-screwdriver-wrench',
      OTHERS: 'fa-solid fa-ellipsis',
    };
    return map[type] || 'fa-solid fa-circle';
  }

  getCostTypeColorClass(type: string): string {
    const map: Record<string, string> = {
      INTERNET: 'orange-bg text-orange',
      GARBAGE: 'blue-bg text-blue',
      MAINTENANCE: 'red-bg text-red',
      OTHERS: 'gray-bg text-gray',
    };
    return map[type] || '';
  }

  getCostTypeClass(type: string): string {
    return type.toLowerCase() + '-badge';
  }

  // ===============================
  // EVENT HANDLERS (FILTER & FORM)
  // ===============================

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

  updateType(val: string) {
    this.form.update((f) => ({ ...f, type: val as CostType }));
  }

  updateAmount(val: string) {
    const amount = +val;
    this.form.update((f) => ({ ...f, amount: amount }));
  }

  updateMonth(val: string) {
    this.form.update((f) => ({ ...f, month: val }));
  }

  updateDescription(val: string) {
    this.form.update((f) => ({ ...f, description: val }));
  }

  // Filter events
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
  onFilterType(e: Event) {
    this.filters.update((f) => ({
      ...f,
      type: (e.target as HTMLSelectElement).value as any,
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

  // ===============================
  // CRUD ACTIONS
  // ===============================
  submitForm() {
    const data = this.form();

    if (data.amount < 0) {
      alert('Số tiền không được âm!');
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

    if (!this.isEditMode()) {
      const isDuplicate = this.records().some(
        (r) =>
          r.roomId === data.roomId &&
          r.month === data.month &&
          r.type === data.type
      );

      if (isDuplicate) {
        alert('Loại chi phí này đã tồn tại trong tháng cho phòng này!');
        return;
      }
    }

    if (this.isEditMode()) {
      this.extraCostService.update(data.id, data).subscribe({
        next: () => {
          this.loadData();
          this.closeModal();
          alert('Cập nhật thành công!');
        },
        error: (err) => {
          console.error(err);
          alert('Có lỗi xảy ra khi cập nhật!');
        },
      });
    } else {
      this.extraCostService.create(data).subscribe({
        next: () => {
          this.loadData();
          this.closeModal();
          alert('Thêm mới thành công!');
        },
        error: (err) => {
          console.error(err);
          alert('Có lỗi xảy ra khi thêm hóa đơn!');
        },
      });
    }
  }

  confirmPayment() {
    const id = this.recordToConfirm()?.id;
    if (!id) return;

    this.extraCostService.markPaid(id).subscribe({
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

  onConfirmAction() {
    if (this.isDeleteMode()) {
      this.deleteRecord();
    } else {
      this.confirmPayment();
    }
  }
  deleteRecord() {
    const id = this.recordToConfirm()?.id;
    if (!id) return;

    this.extraCostService.delete(id).subscribe(() => {
      this.loadData();
      this.closeConfirmModal();
      alert('Đã xóa bản ghi!');
    });
  }
  // ===============================
  // MODAL LOGIC
  // ===============================
  openAddModal() {
    const now = new Date();
    const currentMonth = `${now.getFullYear()}-${(now.getMonth() + 1)
      .toString()
      .padStart(2, '0')}`;
    this.form.set({
      id: 0,
      roomId: 0,
      name: '',
      type: CostType.OTHERS,
      amount: 0,
      month: currentMonth,
      description: '',
      status: ExtraCostStatus.UNPAID,
      fullName: '',
    });
    this.currentName.set('');
    this.isEditMode.set(false);
    this.isModalOpen.set(true);
  }

  openEditModal(record: ExtraCost) {
    this.form.set({ ...record });
    if (record.status === ExtraCostStatus.PAID) {
      alert('Bản ghi đã thanh toán, không thể chỉnh sửa!');
      return;
    }
    this.currentName.set(record.fullName || '');
    this.isEditMode.set(true);
    this.isModalOpen.set(true);
  }

  closeModal() {
    this.isModalOpen.set(false);
  }

  openDeleteModal(record: ExtraCost) {
    this.recordToConfirm.set(record);
    this.isDeleteMode.set(true);
    this.isConfirmModalOpen.set(true);
  }

  openConfirmPaymentModal(record: ExtraCost) {
    this.recordToConfirm.set(record);
    this.isDeleteMode.set(false);
    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal() {
    this.isConfirmModalOpen.set(false);
    this.recordToConfirm.set(null);
  }

  // ===============================
  // EXPORT
  // ===============================
  onExportExcel() {
    const data = this.filteredRecords();
    if (!data.length) return alert('Không có dữ liệu!');

    const csvContent =
      'Mã CP,Phòng,Loại,Mô tả,Số tiền,Tháng,Trạng thái\n' +
      data
        .map(
          (r) =>
            `${r.code},${r.name},,${r.fullName || '---'},${this.getCostTypeName(
              r.type
            )},"${r.description || ''}",${r.amount},${r.month},${r.status}`
        )
        .join('\n');

    const blob = new Blob(['\uFEFF' + csvContent], {
      type: 'text/csv;charset=utf-8;',
    });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `Chi_phi_phat_sinh_${new Date()
      .toISOString()
      .slice(0, 10)}.csv`;
    link.click();
  }
}
