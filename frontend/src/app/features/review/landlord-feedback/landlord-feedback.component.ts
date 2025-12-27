import { Component } from '@angular/core';
import { FeedbackService } from '../../../services/feedback.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-landlord-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './landlord-feedback.component.html',
  styleUrl: './landlord-feedback.component.css'
})
export class LandlordFeedbackComponent {
  page: any = { content: [], number: 0, totalPages: 1, first: true, last: true, empty: true };
  
  // Modal resolve
  resolveId = 0;
  resolveNote = '';
  isResolveModalOpen = false;

  // Toast thông báo
  alertMessage = '';
  alertType: 'success' | 'error' | 'info' | '' = '';

  constructor(private feedbackService: FeedbackService) {
    this.loadPage(0);
  }

  loadPage(page: number) {
    if (page < 0) return;
    this.feedbackService.getForLandlord(page).subscribe({
      next: (data) => this.page = data,
      error: () => this.showAlert('Lỗi tải dữ liệu', 'error')
    });
  }

  // Bắt đầu xử lý
  startProcessing(id: number) {
    this.feedbackService.process(id, 'PROCESSING').subscribe({
      next: () => {
        this.showAlert('Bắt đầu xử lý thành công', 'info');
        this.loadPage(this.page.number);
      },
      error: () => this.showAlert('Lỗi khi bắt đầu xử lý', 'error')
    });
  }

  // Mở modal xác nhận xử lý xong
  openResolveModal(id: number) {
    this.resolveId = id;
    this.resolveNote = '';
    this.isResolveModalOpen = true;
  }

  closeResolveModal() {
    this.isResolveModalOpen = false;
  }

  // Gửi xác nhận đã xử lý xong
  submitResolve() {
    if (!this.resolveNote.trim()) return;

    console.log('Submitting resolve for ID:', this.resolveId);
    
    this.feedbackService.process(this.resolveId, 'RESOLVED', this.resolveNote).subscribe({
      next: (response) => {
        console.log('Resolve response:', response);
        this.showAlert('Đã gửi thông báo xử lý xong!', 'success');
        this.closeResolveModal();
        
        // Force reload ngay lập tức
        this.loadPage(this.page.number);
        
        // Thêm delay nhỏ rồi reload lại
        setTimeout(() => {
          this.loadPage(this.page.number);
        }, 1000);
      },
      error: (err) => {
        console.error('Resolve error:', err);
        this.showAlert('Gửi thất bại', 'error');
      }
    });
  }

  // HỦY PHẢN HỒI (API thật)
  cancel(id: number) {
    if (!confirm('Bạn có chắc muốn HỦY phản hồi này?\nHành động không thể hoàn tác.')) {
      return;
    }

    this.feedbackService.cancel(id).subscribe({
      next: () => {
        this.showAlert('Đã hủy phản hồi', 'success');
        this.loadPage(this.page.number);
      },
      error: (err) => {
        this.showAlert(
          err?.error?.message || 'Không thể hủy phản hồi',
          'error'
        );
      }
    });
  }

  // Đóng modal (ẩn dấu tick đen)
  closeModal() {
    const checkbox = document.getElementById('resolve-modal') as HTMLInputElement;
    if (checkbox) checkbox.checked = false;
  }

  // Toast thông báo đẹp
  private showAlert(message: string, type: 'success' | 'error' | 'info') {
    this.alertMessage = message;
    this.alertType = type;
    setTimeout(() => {
      this.alertMessage = '';
      this.alertType = '';
    }, 4500);
  }

  // Trạng thái đẹp
  getStatusText(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'Chưa xử lý',
      PROCESSING: 'Đang xử lý',
      RESOLVED: 'Đã xử lý xong (chờ khách xác nhận)',
      CANCELED: 'Đã hủy',
      TENANT_CONFIRMED: 'Khách đã xác nhận hài lòng',
      TENANT_REJECTED: 'Khách chưa hài lòng'
    };
    return map[status] || status;
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'badge-warning',
      PROCESSING: 'badge-info',
      RESOLVED: 'badge-primary',
      CANCELED: 'badge-ghost',
      TENANT_CONFIRMED: 'badge-success',
      TENANT_REJECTED: 'badge-error'
    };
    return `badge badge-lg font-bold ${map[status] || 'badge-neutral'}`;
  }
  
}