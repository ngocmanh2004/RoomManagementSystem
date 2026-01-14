import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Feedback {
  id: number;
  title: string;
  content: string;
  status: 'PENDING' | 'PROCESSING' | 'RESOLVED' | 'TENANT_CONFIRMED' | 'TENANT_REJECTED' | 'CANCELED';
  createdAt: string;
  resolvedAt?: string;
  landlordNote?: string;
  attachmentUrl?: string;
  room: { id: number; name: string };
}

@Component({
  selector: 'app-tenant-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tenant-feedback.component.html',
  styleUrls: ['./tenant-feedback.component.css']
})
export class TenantFeedbackComponent implements OnInit {
  currentRoomId: number = 1;
  feedbacks: Feedback[] = [];
  loading = false;
  showCreateForm = false;
  
  // Form data
  newFeedback = {
    roomId: this.currentRoomId,
    title: '',
    content: '',
    attachmentUrl: ''
  };
  selectedFile: File | null = null;
  editingFeedbackId: number | null = null;

  filterStatus: 'all' | 'PENDING' | 'PROCESSING' | 'RESOLVED' = 'all';
  currentBuildingId: number = 1; // hoặc lấy động từ phòng

  private apiUrl = 'http://localhost:8081/api/feedbacks';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadFeedbacks();
  }

  loadFeedbacks() {
    this.loading = true;
    this.http.get<any>(`${this.apiUrl}/my`, {
      params: { page: '0', size: '10' }
    }).subscribe({
      next: (response) => {
        this.feedbacks = response.content || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading feedbacks:', err);
        this.loading = false;
      }
    });
  }

  toggleCreateForm() {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) {
      this.resetForm();
    }
  }

  public createFeedback() {
    if (!this.newFeedback.title || !this.newFeedback.content) {
      alert('Vui lòng điền đầy đủ tiêu đề và nội dung!');
      return;
    }

    // 🔁 UPDATE
    if (this.editingFeedbackId) {
      this.http.put(
        `${this.apiUrl}/${this.editingFeedbackId}`,
        this.newFeedback
      ).subscribe({
        next: () => {
          alert('Cập nhật phản hồi thành công!');
          this.afterSave();
        },
        error: err => {
          console.error(err);
          alert('Cập nhật thất bại!');
        }
      });
      return;
    }

    // ➕ CREATE
    this.http.post<Feedback>(this.apiUrl, this.newFeedback).subscribe({
      next: () => {
        alert('Gửi phản hồi thành công!');
        this.afterSave();
      },
      error: err => {
        console.error(err);
        alert('Có lỗi xảy ra khi gửi phản hồi!');
      }
    });
  }
  afterSave() {
    this.showCreateForm = false;
    this.editingFeedbackId = null;
    this.resetForm();
    this.loadFeedbacks();
  }

  confirmFeedback(id: number) {
    const satisfied = confirm('Bạn có hài lòng với cách xử lý không?');
    const tenantFeedback = prompt('Nhập ý kiến của bạn (tùy chọn):');

    this.http.put(`${this.apiUrl}/${id}/confirm`, {
      satisfied,
      tenantFeedback
    }).subscribe({
      next: () => {
        alert('Đã xác nhận phản hồi!');
        this.loadFeedbacks();
      },
      error: (err) => {
        console.error('Error confirming feedback:', err);
        alert('Có lỗi xảy ra!');
      }
    });
  }

  resetForm() {
    this.newFeedback = {
      roomId: this.currentRoomId,
      title: '',
      content: '',
      attachmentUrl: ''
    };
    this.selectedFile = null;
    this.editingFeedbackId = null;
  }

  getStatusText(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'Chưa xử lý',
      'PROCESSING': 'Đang xử lý',
      'RESOLVED': 'Đã xử lý',
      'TENANT_CONFIRMED': 'Đã xác nhận',
      'TENANT_REJECTED': 'Chưa hài lòng',
      'CANCELED': 'Đã hủy'
    };
    return statusMap[status] || status;
  }

  getStatusColor(status: string): string {
    const colorMap: { [key: string]: string } = {
      'PENDING': '#ffc107',
      'PROCESSING': '#17a2b8',
      'RESOLVED': '#28a745',
      'TENANT_CONFIRMED': '#28a745',
      'TENANT_REJECTED': '#dc3545',
      'CANCELED': '#6c757d'
    };
    return colorMap[status] || '#6c757d';
  }

  getStatusIcon(status: string): string {
    const iconMap: { [key: string]: string } = {
      'PENDING': 'clock',
      'PROCESSING': 'spinner',
      'RESOLVED': 'check-circle',
      'TENANT_CONFIRMED': 'check-double',
      'TENANT_REJECTED': 'times-circle',
      'CANCELED': 'ban'
    };
    return iconMap[status] || 'question-circle';
  }

  getStatusIconCard(status: string): string {
    const iconMap: { [key: string]: string } = {
      'PENDING': 'exclamation-circle',
      'PROCESSING': 'hourglass-half',
      'RESOLVED': 'check-circle',
      'TENANT_CONFIRMED': 'check-double',
      'TENANT_REJECTED': 'times-circle',
      'CANCELED': 'ban'
    };
    return iconMap[status] || 'circle';
  }
  
  get filteredFeedbacks(): Feedback[] {
    if (this.filterStatus === 'all') {
      return this.feedbacks;
    }
    return this.feedbacks.filter(f => f.status === this.filterStatus);
  }
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.uploadImage();
    }
  }
  uploadImage() {
  if (!this.selectedFile) return;

  const formData = new FormData();
  formData.append('file', this.selectedFile);
  formData.append('buildingId', this.currentBuildingId.toString());
  formData.append('roomId', this.currentRoomId.toString());

  this.http.post<any>('http://localhost:8081/api/upload', formData)
    .subscribe({
      next: res => {
        console.log('UPLOAD OK:', res);
        this.newFeedback.attachmentUrl = res.url;
      },
      error: err => {
        console.error('UPLOAD ERROR:', err);
        alert('Upload ảnh thất bại');
      }
    });
  }

  editFeedback(feedback: Feedback) {
    this.editingFeedbackId = feedback.id;

    this.newFeedback = {
      roomId: this.currentRoomId,
      title: feedback.title,
      content: feedback.content,
      attachmentUrl: feedback.attachmentUrl || ''
    };

    this.showCreateForm = true;
  }
  deleteFeedback(id: number) {
    if (!confirm('Bạn có chắc chắn muốn xóa phản hồi này không?')) {
      return;
    }

    this.http.delete(`${this.apiUrl}/${id}`).subscribe({
      next: () => {
        alert('Đã xóa phản hồi!');
        this.loadFeedbacks();
      },
      error: err => {
        console.error(err);
        alert('Xóa phản hồi thất bại!');
      }
    });
  }

}