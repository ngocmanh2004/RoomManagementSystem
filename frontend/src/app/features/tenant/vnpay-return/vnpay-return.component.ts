import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-vnpay-return',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vnpay-return.component.html',
  styleUrl: './vnpay-return.component.css'
})
export class VnpayReturnComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private http = inject(HttpClient);

  // State signals
  isLoading = signal(true);
  paymentSuccess = signal(false);
  errorMessage = signal('');
  transactionRef = signal('');
  amount = signal(0);
  paymentTime = signal(new Date());
  invoiceId = signal<number | null>(null);

  ngOnInit() {
    this.processPaymentReturn();
  }

  /**
   * Xử lý kết quả thanh toán từ VNPay
   */
  processPaymentReturn() {
    // Lấy tất cả query params từ VNPay
    this.route.queryParams.subscribe(params => {
      console.log('VNPay return params:', params);

      // Gửi toàn bộ params về backend để xử lý
      this.http.get('/api/payments/vnpay-return', { params }).subscribe({
        next: (response: any) => {
          console.log('Backend response:', response);
          this.isLoading.set(false);
          
          if (response.data?.success) {
            // Thanh toán thành công
            this.paymentSuccess.set(true);
            this.transactionRef.set(params['vnp_TxnRef'] || '');
            this.amount.set(parseInt(params['vnp_Amount'] || '0') / 100);
            this.paymentTime.set(new Date());
            
            // Lưu invoice ID để có thể quay lại
            if (response.data.invoice?.id) {
              this.invoiceId.set(response.data.invoice.id);
            }
          } else {
            // Thanh toán thất bại
            this.paymentSuccess.set(false);
            this.errorMessage.set(
              response.data?.message || 'Thanh toán không thành công'
            );
          }
        },
        error: (err) => {
          console.error('Payment return error:', err);
          this.isLoading.set(false);
          this.paymentSuccess.set(false);
          this.errorMessage.set('Có lỗi xảy ra khi xử lý thanh toán');
        }
      });
    });
  }

  /**
   * Quay về trang danh sách hóa đơn
   */
  goToInvoices() {
    this.router.navigate(['/tenant/tenant-profile']);
  }

  /**
   * Thử thanh toán lại (nếu thất bại)
   */
  retryPayment() {
    this.goToInvoices();
  }

  /**
   * Format số tiền
   */
  formatCurrency(value: number): string {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(value);
  }
}