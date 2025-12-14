import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Feedback, FeedbackService } from '../../../services/feedback.service';
import { UploadService } from '../../../services/upload.service';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
@Component({
  selector: 'app-tenant-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tenant-feedback.component.html',
  styleUrl: './tenant-feedback.component.css'
})
export class TenantFeedbackComponent {
  newFeedback = { title: '', content: '', attachmentUrl: '' };
  myFeedback: Feedback[] = [];
  loading = false;

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

  constructor(private feedbackService: FeedbackService, private uploadService: UploadService) {
    this.loadMyFeedback();
    // Polling mỗi 5 giây để cập nhật real-time
    this.autoReloadSub = interval(5000).pipe(
      switchMap(() => this.feedbackService.getMyFeedback())
    ).subscribe({
      next: (data) => {
        // Chỉ cập nhật nếu có thay đổi
        if (JSON.stringify(this.myFeedback) !== JSON.stringify(data)) {
          this.myFeedback = data;
        }
      },
      error: (err) => console.error('Polling error:', err)
    });
  }

  submit() {
    console.log('SUBMIT CLICKED');

    if (!this.newFeedback.title.trim() || !this.newFeedback.content.trim()) {
      this.showAlert('Vui lòng nhập tiêu đề và nội dung', 'error');
      return;
    }

    this.loading = true;

    if (this.selectedFile) {
      console.log('Uploading file:', this.selectedFile);

      this.uploadService.uploadImage(this.selectedFile).subscribe({
        next: (res) => {
          console.log('UPLOAD OK:', res);
          this.newFeedback.attachmentUrl = res.url;
          this.createFeedback();
        },
        error: (err) => {
          console.error('UPLOAD FAIL:', err);
          this.showAlert('Upload ảnh thất bại', 'error');
          this.loading = false;
        }
      });

    } else {
      this.createFeedback();
    }
  }

  // TÁCH HÀM TẠO FEEDBACK
  private createFeedback() {
    this.feedbackService.create(this.newFeedback).subscribe({
      next: () => {
        this.showAlert('Đã gửi phản hồi thành công!', 'success');
        // Reset form
        this.newFeedback = { title: '', content: '', attachmentUrl: '' };
        this.selectedFile = null;
        // Reset input file
        const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
        if (fileInput) fileInput.value = '';
        this.loading = false;
        this.loadMyFeedback();
      },
      error: (err) => {
        const msg = err?.error?.message || 'Gửi thất bại, vui lòng thử lại';
        this.showAlert(msg, 'error');
        this.loading = false;
      }
    });
  }

  loadMyFeedback() {
    this.feedbackService.getMyFeedback().subscribe({
      next: (data) => this.myFeedback = [...data],
      error: () => this.showAlert('Không tải được lịch sử', 'error')
    });
  }

  confirmSatisfied(id: number) {
    this.feedbackService.tenantConfirm(id).subscribe({
      next: () => {
        this.showAlert('Cảm ơn bạn đã xác nhận hài lòng!', 'success');
        this.loadMyFeedback();
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
        this.loadMyFeedback();
      },
      error: () => this.showAlert('Gửi thất bại', 'error')
    });
  }

  // Hiển thị thông báo nổi (giống landlord)
  private showAlert(message: string, type: 'success' | 'error' | 'info') {
    this.alertMessage = message;
    this.alertType = type;
    setTimeout(() => this.alertMessage = '', 5000);
  }

  // Trạng thái
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
  cancel(feedbackId: number) {
    if (!confirm('Bạn có chắc muốn hủy phản ánh này?')) return;

    this.feedbackService.cancel(feedbackId).subscribe({
      next: () => {
        this.showAlert('Hủy phản ánh thành công', 'info');
        this.loadMyFeedback();
        (document.getElementById('reject-modal') as HTMLInputElement).checked = false;
      },
      error: err => {
        const msg = err?.error?.message || 'Hủy thất bại';
        this.showAlert(msg, 'error');
      }
    });
  }
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      // Tạo preview URL một lần duy nhất
    this.previewUrl = URL.createObjectURL(this.selectedFile);
    }
  }
  deleteFeedback(id: number) {
    if (!confirm('Bạn có chắc chắn muốn xóa phản hồi này?')) return;

    this.feedbackService.delete(id).subscribe({
      next: () => {
        this.showAlert('Đã xóa phản hồi', 'info');
        this.myFeedback = this.myFeedback.filter(f => f.id !== id);
      },
      error: err => {
        const msg = err?.error?.message || 'Xóa thất bại';
        this.showAlert(msg, 'error');
      }
    });
  }
  // XÓA FILE ĐÃ CHỌN
  removeFile() {
    this.selectedFile = null;
    this.newFeedback.attachmentUrl = '';
    // Revoke URL cũ để giải phóng bộ nhớ
    if (this.previewUrl) {
      URL.revokeObjectURL(this.previewUrl);
      this.previewUrl = null;
    }
    // reset input file
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  // MỞ MODAL XEM ẢNH PHÓNG TO
  openImageModal(url: string) {
    this.imageModalUrl = url;
    this.showImageModal = true;
  }

  // ĐÓNG MODAL
  closeImageModal() {
    this.showImageModal = false;
    this.imageModalUrl = null;
  }

  // FORMAT DUNG LƯỢNG FILE
  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }
  ngOnDestroy() {
    this.autoReloadSub?.unsubscribe();
  }
}