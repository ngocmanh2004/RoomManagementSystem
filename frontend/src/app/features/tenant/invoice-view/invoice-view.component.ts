import {
  ChangeDetectionStrategy,
  Component,
  signal,
  computed,
  OnInit,
  inject,
} from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Invoice } from '../../../models/invoice.model';
import { InvoiceService } from '../../../services/invoice.service';
import { AuthService } from '../../../services/auth.service';
import { TenantService } from '../../../services/tenant.service';

@Component({
  selector: 'app-invoice-view',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe, FormsModule],
  templateUrl: './invoice-view.component.html',
  styleUrl: './invoice-view.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvoiceViewComponent implements OnInit {
  // ===============================
  // CONSTANTS & INJECTS
  // ===============================
  readonly InvoiceStatus = {
    UNPAID: 'Chưa thanh toán',
    PAID: 'Đã thanh toán',
    OVERDUE: 'Quá hạn',
  };

  private invoiceService = inject(InvoiceService);
  private authService = inject(AuthService);
  private tenantService = inject(TenantService);
  private http = inject(HttpClient);

  // ===============================
  // STATE SIGNALS
  // ===============================
  currentDateTime = new Date();
  months = signal<string[]>([]);
  invoices = signal<Invoice[]>([]);
  currentTenantId = signal<number | null>(null);

  filters = signal<{
    month: string;
    status: 'ALL' | 'UNPAID' | 'PAID' | 'OVERDUE';
  }>({
    month: '',
    status: 'ALL',
  });

  selectedInvoice = signal<Invoice | null>(null);
  isDetailModalOpen = signal(false);
  isLoading = signal(false);

  // ===============================
  // LIFECYCLE
  // ===============================
  ngOnInit() {
    this.getCurrentTenantId();
    this.loadInvoices();
    this.generateMonths();
  }

  // ===============================
  // LOAD DATA
  // ===============================
  getCurrentTenantId() {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.tenantService.getTenantByUserId(user.id).subscribe({
        next: (response: any) => {
          if (response?.id) {
            this.currentTenantId.set(response.id);
            this.loadInvoices();
          }
        },
        error: (err: any) => console.error('Lỗi tải thông tin người thuê:', err),
      });
    }
  }

  loadInvoices() {
    const tenantId = this.currentTenantId();
    if (!tenantId) return;

    this.isLoading.set(true);
    this.invoiceService.getByTenantId(tenantId).subscribe({
      next: (response: any) => {
        if (response.data) {
          this.invoices.set(response.data);
          this.extractMonths();
        }
        this.isLoading.set(false);
      },
      error: (err: any) => {
        console.error('Lỗi tải hóa đơn:', err);
        this.isLoading.set(false);
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

  extractMonths() {
    const uniqueMonths = Array.from(new Set(this.invoices().map((inv) => inv.month)))
      .sort()
      .reverse();
    this.months.set(uniqueMonths);
  }

  // ===============================
  // COMPUTED LOGIC
  // ===============================
  filteredInvoices = computed(() => {
    const f = this.filters();
    return this.invoices().filter((inv) => {
      const matchMonth = !f.month || inv.month === f.month;
      const matchStatus = f.status === 'ALL' || inv.status === f.status;
      return matchMonth && matchStatus;
    });
  });

  stats = computed(() => {
    const list = this.filteredInvoices();
    const totalAmount = list.reduce((sum, inv) => sum + inv.totalAmount, 0);
    const unpaidCount = list.filter((inv) => inv.status === 'UNPAID').length;
    const paidCount = list.filter((inv) => inv.status === 'PAID').length;
    const overdueCount = list.filter((inv) => inv.status === 'OVERDUE').length;

    return {
      totalAmount,
      totalCount: list.length,
      unpaidCount,
      paidCount,
      overdueCount,
    };
  });

  totalUnpaidAmount = computed(() => {
    return this.invoices()
      .filter((inv) => inv.status === 'UNPAID' || inv.status === 'OVERDUE')
      .reduce((sum, inv) => sum + inv.totalAmount, 0);
  });

  // ===============================
  // DETAIL VIEW LOGIC
  // ===============================
  viewInvoiceDetail(invoice: Invoice) {
    this.selectedInvoice.set(invoice);
    this.isDetailModalOpen.set(true);
  }

  closeDetailModal() {
    this.isDetailModalOpen.set(false);
    this.selectedInvoice.set(null);
  }

  downloadInvoice(invoice: Invoice) {
    // Tạo nội dung hóa đơn
    const content = `
BIÊN LAI / HÓA ĐƠN
=====================================
Mã Hợp Đồng: ${invoice.contractCode}
Phòng: ${invoice.roomName}
Khách Thuê: ${invoice.tenantName}
Tháng: T${invoice.month.split('-')[1]}/${invoice.month.split('-')[0]}

CHI TIẾT CHI PHÍ:
-------------------------------------
Tiền Phòng:       ${this.formatCurrency(invoice.roomRent)}
Tiền Điện:        ${this.formatCurrency(invoice.electricity)}
Tiền Nước:        ${this.formatCurrency(invoice.water)}
Chi Phí Khác:     ${this.formatCurrency(invoice.extraCost)}
=====================================
TỔNG CỘNG:        ${this.formatCurrency(invoice.totalAmount)}

Trạng Thái: ${(this.InvoiceStatus as any)[invoice.status] || invoice.status}
Ghi Chú: ${invoice.notes || 'Không có'}

Ngày Tạo: ${new Date(invoice.createdAt).toLocaleDateString('vi-VN')}
    `.trim();

    // Tạo blob từ content
    const blob = new Blob([content], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `hoa-don-${invoice.contractCode}-${invoice.month}.txt`;
    link.click();
    URL.revokeObjectURL(url);
  }

  payInvoice(invoice: Invoice) {
  if (!invoice || !invoice.id) return;
  
  this.isLoading.set(true);
  
  this.http.post('/api/payments/create', { invoiceId: invoice.id }).subscribe({
    next: (res: any) => {
      this.isLoading.set(false);
      const url = res?.data?.paymentUrl;
      if (url) {
        console.log('VNPay payment URL:', url);
        // Mở VNPay trong tab mới
        window.open(url, '_blank');
      } else {
        console.error('Không lấy được URL thanh toán', res);
        alert('Không thể tạo link thanh toán. Vui lòng thử lại!');
      }
    },
    error: (err: any) => {
      this.isLoading.set(false);
      console.error('Lỗi tạo thanh toán:', err);
      alert('Có lỗi xảy ra. Vui lòng thử lại!');
    }
  });
}

  // ===============================
  // UTILITIES
  // ===============================
  updateFilter(key: string, value: any) {
    const current = this.filters();
    this.filters.set({ ...current, [key]: value });
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

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(value);
  }

  getMonthDisplay(month: string): string {
    return `T${month.split('-')[1]}/${month.split('-')[0]}`;
  }

  // Wrapper methods for template binding
  onMonthChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.updateFilter('month', value);
  }

  onStatusChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.updateFilter('status', value);
  }
}
