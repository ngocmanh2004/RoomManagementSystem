import {
  ChangeDetectionStrategy,
  Component,
  signal,
  computed,
  OnInit,
  inject,
  OnDestroy,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Invoice } from '../../../models/invoice.model';
import { Contract } from '../../../models/contract.model';
import { InvoiceService } from '../../../services/invoice.service';
import { ContractService } from '../../../services/contract.service';
import { Room } from '../../../models/room.model';
import { RoomService } from '../../../services/room.service';

@Component({
  selector: 'app-invoice-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './invoice-management.component.html',
  styleUrl: './invoice-management.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvoiceManagementComponent implements OnInit, OnDestroy {
  // ===============================
  // CONSTANTS & INJECTS
  // ===============================
  readonly InvoiceStatus = {
    UNPAID: 'Chưa thanh toán',
    PAID: 'Đã thanh toán',
    OVERDUE: 'Quá hạn',
  };

  private invoiceService = inject(InvoiceService);
  private contractService = inject(ContractService);
  private roomService = inject(RoomService);

  // ===============================
  // STATE SIGNALS
  // ===============================
  currentDateTime = new Date();
  months = signal<string[]>([]);
  contracts = signal<Contract[]>([]);
  invoices = signal<Invoice[]>([]);

  filters = signal<any>({
    month: '',
    contractId: 0,
    status: 'ALL',
    keyword: '',
  });

  form = signal<any>({
    contractId: 0,
    month: this.getCurrentMonth(),
    roomRent: 0,
    electricity: 0,
    water: 0,
    extraCost: 0,
    notes: '',
  });

  isModalOpen = signal(false);
  isEditMode = signal(false);
  isConfirmModalOpen = signal(false);
  invoiceToConfirm = signal<Invoice | null>(null);
  actionType = signal<'DELETE' | 'PAY' | 'UNPAY' | null>(null);
  editingInvoiceId = signal<number | null>(null);

  // ===============================
  // LIFECYCLE
  // ===============================
  ngOnInit() {
    this.loadContracts();
    this.loadInvoices();
    this.generateMonths();
  }

  ngOnDestroy() {
    // Clean up if needed
  }

  // ===============================
  // LOAD DATA
  // ===============================
  loadInvoices() {
    this.invoiceService.getAll().subscribe({
      next: (response: any) => {
        if (response && response.data) {
          this.invoices.set(response.data);
        } else if (Array.isArray(response)) {
          this.invoices.set(response as Invoice[]);
        } else {
          this.invoices.set([]);
        }
      },
      error: (err: any) => console.error('Lỗi tải hóa đơn:', err),
    });
  }

  
  loadContracts() {
    this.contractService.getContracts('').subscribe({
      next: (response: any) => {
        // API may return ApiResponse with paged data (response.data.content),
        // or ApiResponse with data array, or direct array.
        if (response && response.data) {
          // If it's a page response, try to read content
          const d = response.data;
          if (Array.isArray(d)) {
            this.contracts.set(d as Contract[]);
          } else if (d.content && Array.isArray(d.content)) {
            this.contracts.set(d.content as Contract[]);
          } else {
            // fallback: try to extract list-like fields
            this.contracts.set([]);
          }
        } else if (Array.isArray(response)) {
          this.contracts.set(response as Contract[]);
        } else {
          this.contracts.set([]);
        }
      },
      error: (err: any) => {
        console.error('Lỗi tải hợp đồng:', err);
        this.contracts.set([]);
      },
    });
  }

  generateMonths() {
    const currentDate = new Date();
    const months: string[] = [];
    for (let i = 0; i < 12; i++) {
      const date = new Date(currentDate.getFullYear(), currentDate.getMonth() - i, 1);
      months.push(`${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`);
    }
    this.months.set(months);
  }

  // ===============================
  // COMPUTED LOGIC
  // ===============================
  filteredInvoices = computed(() => {
    const f = this.filters();
    const keyword = f.keyword.toLowerCase().trim();

    return this.invoices().filter((inv) => {
      const matchMonth = !f.month || inv.month === f.month;
      const matchContract = f.contractId === 0 || inv.contractId === f.contractId;
      const matchStatus = f.status === 'ALL' || inv.status === f.status;
      const matchSearch =
        !keyword ||
        inv.contractCode?.toLowerCase().includes(keyword) ||
        inv.roomName?.toLowerCase().includes(keyword) ||
        inv.tenantName?.toLowerCase().includes(keyword);

      return matchMonth && matchContract && matchStatus && matchSearch;
    });
  });
  stats = computed(() => {
    const list = this.filteredInvoices();
    const totalAmount = list.reduce((sum, inv) => sum + (inv.totalAmount || 0), 0);
    const unpaidCount = list.filter((inv) => inv.status === 'UNPAID').length;
    const paidCount = list.filter((inv) => inv.status === 'PAID').length;
    const overdueCount = list.filter((inv) => inv.status === 'OVERDUE').length;

    const electricityTotal = list.reduce((s, inv) => s + (inv.electricity || 0), 0);
    const waterTotal = list.reduce((s, inv) => s + (inv.water || 0), 0);
    const extraTotal = list.reduce((s, inv) => s + (inv.extraCost || 0), 0);

    const base = electricityTotal + waterTotal + extraTotal;
    const electricityPct = base > 0 ? Math.round((electricityTotal / base) * 100) : 40;
    const waterPct = base > 0 ? Math.round((waterTotal / base) * 100) : 20;

    return {
      totalAmount,
      totalCount: list.length,
      unpaidCount,
      paidCount,
      overdueCount,
      electricityTotal,
      waterTotal,
      extraTotal,
      electricityPct,
      waterPct,
    };
  });

  // ===============================
  // MODAL & FORM LOGIC
  // ===============================
  openCreateModal() {
    this.isEditMode.set(false);
    this.editingInvoiceId.set(null);
    this.form.set({
      contractId: 0,
      month: this.getCurrentMonth(),
      electricity: 0,
      water: 0,
      extraCost: 0,
      notes: '',
    });
    this.isModalOpen.set(true);
  }

  closeModal() {
    this.isModalOpen.set(false);
    this.isEditMode.set(false);
    this.editingInvoiceId.set(null);
  }

  editInvoice(invoice: Invoice) {
    this.isEditMode.set(true);
    this.editingInvoiceId.set(invoice.id);
    this.form.set({
      contractId: invoice.contractId,
      month: invoice.month,
      electricity: invoice.electricity,
      water: invoice.water,
      extraCost: invoice.extraCost,
      notes: invoice.notes,
    });
    this.isModalOpen.set(true);
  }

  saveInvoice() {
    const currentForm = this.form();

    if (!currentForm.contractId) {
      alert('Vui lòng chọn hợp đồng');
      return;
    }

    if (this.isEditMode() && this.editingInvoiceId()) {
      this.invoiceService.updateInvoice(this.editingInvoiceId()!, currentForm).subscribe({
        next: (response) => {
          if (response.success) {
            alert('Cập nhật hóa đơn thành công');
            this.closeModal();
            this.loadInvoices();
          }
        },
        error: (err) => alert('Lỗi: ' + (err.error?.message || 'Cập nhật thất bại')),
      });
    } else {
      this.invoiceService.createInvoice(currentForm).subscribe({
        next: (response) => {
          if (response.success) {
            alert('Tạo hóa đơn thành công');
            this.closeModal();
            this.loadInvoices();
          }
        },
        error: (err) => alert('Lỗi: ' + (err.error?.message || 'Tạo thất bại')),
      });
    }
  }

  // ===============================
  // STATUS UPDATE LOGIC
  // ===============================
  updateInvoiceStatus(invoice: Invoice, newStatus: 'UNPAID' | 'PAID' | 'OVERDUE') {
    this.invoiceToConfirm.set(invoice);
    if (newStatus === 'PAID') {
      this.actionType.set('PAY');
    } else if (newStatus === 'UNPAID') {
      this.actionType.set('UNPAY');
    }
    this.isConfirmModalOpen.set(true);
  }

  confirmAction() {
    const invoice = this.invoiceToConfirm();
    const action = this.actionType();

    if (!invoice || !action) return;

    const newStatus = action === 'PAY' ? 'PAID' : 'UNPAID';

    this.invoiceService.updateStatus(invoice.id, newStatus).subscribe({
      next: (response) => {
        if (response.success) {
          alert(`Cập nhật trạng thái thành công`);
          this.isConfirmModalOpen.set(false);
          this.invoiceToConfirm.set(null);
          this.actionType.set(null);
          this.loadInvoices();
        }
      },
      error: (err) => alert('Lỗi: ' + (err.error?.message || 'Cập nhật thất bại')),
    });
  }

  cancelAction() {
    this.isConfirmModalOpen.set(false);
    this.invoiceToConfirm.set(null);
    this.actionType.set(null);
  }

  deleteInvoice(invoice: Invoice) {
    if (!confirm('Bạn có chắc muốn xóa hóa đơn này?')) {
      return;
    }

    this.invoiceService.deleteInvoice(invoice.id).subscribe({
      next: (response) => {
        if (response.success) {
          alert('Xóa hóa đơn thành công');
          this.loadInvoices();
        }
      },
      error: (err) => alert('Lỗi: ' + (err.error?.message || 'Xóa thất bại')),
    });

  }

  // trackBy for ngFor (performance)
  trackByInvoice(index: number, invoice: Invoice) {
    return invoice && (invoice as any).id ? (invoice as any).id : index;
  }

  // ===============================
  // UTILITIES
  // ===============================
  updateFilter(key: string, value: any) {
    const current = this.filters();
    this.filters.set({ ...current, [key]: value });
  }

  updateFormField(key: string, value: any) {
    const current = this.form();
    this.form.set({ ...current, [key]: value });
  }

  private getCurrentMonth(): string {
    const now = new Date();
    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PAID':
        return 'bg-green-100 text-green-800';
      case 'UNPAID':
        return 'bg-yellow-100 text-yellow-800';
      case 'OVERDUE':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  getStatusLabel(status: string): string {
    return (this.InvoiceStatus as any)[status] || status;
  }

  // Wrapper methods for template binding
  onFilterMonthChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.updateFilter('month', value);
  }

  onFilterContractChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.updateFilter('contractId', +value);
  }

  onFilterStatusChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.updateFilter('status', value);
  }

  onFilterKeywordChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.updateFilter('keyword', value);
  }

  onFormContractChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    const contractId = +value;
    this.updateFormField('contractId', contractId);
    // Tìm hợp đồng đã chọn
    const contract = this.contracts().find(c => c.id === contractId);
    if (contract) {
      // Lấy tiền phòng từ hợp đồng
      this.updateFormField('roomRent', contract.monthlyRent);
      // Gọi service lấy tiền điện, nước theo hợp đồng và tháng
      const month = this.form().month;
      this.fetchUtilitiesForContract(contractId, month);
    } else {
      this.updateFormField('roomRent', 0);
      this.updateFormField('electricity', 0);
      this.updateFormField('water', 0);
    }
  }

  onFormMonthChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.updateFormField('month', value);
    // Nếu đã chọn hợp đồng, re-fetch các chi phí theo tháng mới
    const contractId = this.form().contractId;
    if (contractId) {
      this.fetchUtilitiesForContract(contractId, value);
    }
  }

  private fetchUtilitiesForContract(contractId: number, month: string) {
    const contract = this.contracts().find(c => c.id === contractId) as any;
    // Determine roomId from several possible shapes returned by backend
    const roomId = contract?.roomId ?? contract?.room?.id ?? contract?.roomId ?? contractId;

    // Fetch electricity
    this.invoiceService.getElectricityByRoomMonth(roomId, month).subscribe({
      next: (electricity: number) => this.updateFormField('electricity', electricity),
      error: () => this.updateFormField('electricity', 0),
    });

    // Fetch water
    this.invoiceService.getWaterByRoomMonth(roomId, month).subscribe({
      next: (water: number) => this.updateFormField('water', water),
      error: () => this.updateFormField('water', 0),
    });

    // Fetch extra costs total
    this.invoiceService.getExtraCostsTotalByRoomMonth(roomId, month).subscribe({
      next: (extra: number) => this.updateFormField('extraCost', extra),
      error: () => this.updateFormField('extraCost', 0),
    });
  }

  onFormElectricityChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.updateFormField('electricity', +value);
  }

  onFormWaterChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.updateFormField('water', +value);
  }

  onFormExtraCostChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.updateFormField('extraCost', +value);
  }

  onFormNotesChange(event: Event): void {
    const value = (event.target as HTMLTextAreaElement).value;
    this.updateFormField('notes', value);
  }
}
