import { Component, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Feedback, FeedbackService } from '../../../services/feedback.service';
import { UploadService } from '../../../services/upload.service';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

interface PagedFeedback {
  content: Feedback[];
  totalElements: number;
}

@Component({
  selector: 'app-tenant-feedback',
  imports: [CommonModule, FormsModule],
  templateUrl: './tenant-feedback.component.html',
  styleUrls: ['./tenant-feedback.component.css']
})
export class TenantFeedbackComponent implements OnDestroy {
  @ViewChild('f') feedbackForm!: NgForm;
  newFeedback = { title: '', content: '', attachmentUrl: '' };
  myFeedback: Feedback[] = [];
  loading = false;
  submitted = false;

  // Modal & thông báo
  rejectReason = '';
  alertMessage = '';
  selectedFile: File | null = null;
  alertType: 'success' | 'error' | 'info' | '' = '';
  showRejectBoxId: number | null = null;
  imageModalUrl: string | null = null;
  showImageModal = false;
  previewUrl: string | null = null;

  private autoReloadSub?: Subscription;

  currentPage = 1;
  pageSize = 5;
  totalItems = 0;
  totalPages = 0;

  constructor(private feedbackService: FeedbackService, private uploadService: UploadService) {
    // Load trạng thái từ localStorage
    const saved = localStorage.getItem('tenantFeedback');
    if (saved) {
      const state = JSON.parse(saved);
      this.myFeedback = state.myFeedback || [];
      this.currentPage = state.currentPage || 1;
      this.pageSize = state.pageSize || 5;
      this.totalItems = state.totalItems || 0;
      this.totalPages = state.totalPages || 0;
    }

    // Load dữ liệu mới từ server (vẫn giữ paging)
    this.loadMyFeedback(this.currentPage);

    // Polling để cập nhật real-time
    this.autoReloadSub = interval(5000).pipe(
      switchMap(() => this.feedbackService.getMyFeedbackPaged(this.currentPage - 1, this.pageSize))
    ).subscribe({
      next: (res: PagedFeedback) => {
        // Chỉ cập nhật nếu có thay đổi
        if (JSON.stringify(this.myFeedback) !== JSON.stringify(res.content)) {
          this.myFeedback = res.content || [];
          this.totalItems = res.totalElements || 0;
          this.totalPages = Math.ceil(this.totalItems / this.pageSize);
          this.saveState();
        }
      },
      error: (err) => console.error('Polling error:', err)
    });
  }

  // Lưu trạng thái paging + data vào localStorage
  private saveState() {
    localStorage.setItem('tenantFeedback', JSON.stringify({
      myFeedback: this.myFeedback,
      currentPage: this.currentPage,
      pageSize: this.pageSize,
      totalItems: this.totalItems,
      totalPages: this.totalPages
    }));
  }

  submit() {
    this.submitted = true;
    this.alertMessage = ''; // xóa thông báo cũ trước khi kiểm tra
    if (!this.newFeedback.title.trim() || !this.newFeedback.content.trim()) {
      this.showAlert('Vui lòng nhập tiêu đề và nội dung', 'error');
      return;
    }

    this.loading = true;

    if (this.selectedFile) {
      this.uploadService.uploadImage(this.selectedFile).subscribe({
        next: (res) => {
          this.newFeedback.attachmentUrl = res.url;
          this.createFeedback();
        },
        error: () => {
          this.showAlert('Upload ảnh thất bại', 'error');
          this.loading = false;
          this.submitted = false;
        }
      });
    } else {
      this.createFeedback();
    }
  }

  private createFeedback() {
    this.feedbackService.create(this.newFeedback).subscribe({
      next: () => {
        this.showAlert('Đã gửi phản hồi thành công!', 'success');
        
        this.newFeedback = { title: '', content: '', attachmentUrl: '' };
        this.selectedFile = null;
        const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
        if (fileInput) fileInput.value = '';
        this.loading = false;
        this.submitted = false;
        this.loadMyFeedback(this.currentPage);
      },
      error: (err) => {
        const msg = err?.error?.message || 'Gửi thất bại, vui lòng thử lại';
        this.showAlert(msg, 'error');
        this.loading = false;
      }
    });
  }

  loadMyFeedback(page: number = 1) {
    this.currentPage = page;
    this.feedbackService.getMyFeedbackPaged(this.currentPage - 1, this.pageSize)
      .subscribe((res: PagedFeedback) => {
        this.myFeedback = res.content || [];
        this.totalItems = res.totalElements || 0;
        this.totalPages = Math.ceil(this.totalItems / this.pageSize);
        this.saveState();
      });
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  goToPage(page: number) {
    if (page < 1 || page > this.totalPages) return;
    this.loadMyFeedback(page);
  }

  confirmSatisfied(id: number) {
    this.feedbackService.tenantConfirm(id).subscribe({
      next: () => {
        this.showAlert('Cảm ơn bạn đã xác nhận hài lòng!', 'success');
        this.loadMyFeedback(this.currentPage);
      },
      error: () => this.showAlert('Lỗi khi xác nhận', 'error')
    });
  }

  openRejectBox(id: number) {
    this.showRejectBoxId = id;
    this.rejectReason = '';
  }

  closeRejectBox() {
    this.showRejectBoxId = null;
    this.rejectReason = '';
  }

  submitReject() {
    if (!this.showRejectBoxId || !this.rejectReason.trim()) {
      this.showAlert('Vui lòng nhập lý do chưa hài lòng', 'error');
      return;
    }
    const feedbackId = this.showRejectBoxId;
    this.feedbackService.tenantReject(feedbackId, this.rejectReason).subscribe({
      next: () => {
        this.showAlert('Đã gửi phản hồi – chủ trọ sẽ xử lý lại', 'info');
        this.closeRejectBox();
        this.loadMyFeedback(this.currentPage);
      },
      error: () => this.showAlert('Gửi thất bại', 'error')
    });
  }

  private showAlert(message: string, type: 'success' | 'error' | 'info') {
    this.alertMessage = message;
    this.alertType = type;
    setTimeout(() => this.alertMessage = '', 5000);
  }

  cancel(feedbackId: number) {
    if (!confirm('Bạn có chắc muốn hủy phản ánh này?')) return;
    this.feedbackService.cancel(feedbackId).subscribe({
      next: () => {
        this.showAlert('Hủy phản ánh thành công', 'info');
        this.loadMyFeedback(this.currentPage);
      },
      error: (err) => this.showAlert(err?.error?.message || 'Hủy thất bại', 'error')
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.previewUrl = URL.createObjectURL(this.selectedFile);
    }
  }

  removeFile() {
    this.selectedFile = null;
    this.newFeedback.attachmentUrl = '';
    if (this.previewUrl) {
      URL.revokeObjectURL(this.previewUrl);
      this.previewUrl = null;
    }
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  deleteFeedback(id: number) {
    if (!confirm('Bạn có chắc chắn muốn xóa phản hồi này?')) return;
    this.feedbackService.delete(id).subscribe({
      next: () => {
        this.showAlert('Đã xóa phản hồi', 'info');
        this.myFeedback = this.myFeedback.filter(f => f.id !== id);
        this.saveState();
      },
      error: (err) => this.showAlert(err?.error?.message || 'Xóa thất bại', 'error')
    });
  }

  openImageModal(url: string) {
    this.imageModalUrl = url;
    this.showImageModal = true;
  }

  closeImageModal() {
    this.showImageModal = false;
    this.imageModalUrl = null;
  }
  getStatusText(s: string): string {
    const map: Record<string, string> = {
      PENDING: 'Chưa xử lý',
      PROCESSING: 'Đang xử lý',
      RESOLVED: 'Đã xử lý xong – Vui lòng xác nhận hài lòng hoặc chưa', 
      TENANT_CONFIRMED: 'Hoàn tất – Đã xác nhận hài lòng',
      TENANT_REJECTED: 'Chưa hài lòng – Chủ trọ đang xử lý lại', 
      CANCELED: 'Đã hủy'
    };
    return map[s] || s;
  }

  getStatusClass(s: string): string {
    const map: Record<string, string> = {
      PENDING: 'badge badge-warning',
      PROCESSING: 'badge badge-info',
      RESOLVED: 'badge badge-primary',
      TENANT_CONFIRMED: 'badge badge-success badge-lg',
      TENANT_REJECTED: 'badge badge-error',
      CANCELED: 'badge badge-ghost'
    };
    return map[s] || 'badge';
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  ngOnDestroy() {
    this.autoReloadSub?.unsubscribe();
    this.showImageModal = false;
    this.imageModalUrl = null;
    this.previewUrl = null;
    this.selectedFile = null;
  }
}
