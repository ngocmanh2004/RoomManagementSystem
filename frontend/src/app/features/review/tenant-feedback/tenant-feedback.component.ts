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
    title: '',
    content: '',
    attachmentUrl: ''
  };
  selectedFile: File | null = null;
  editingFeedbackId: number | null = null;

  filterStatus: 'all' | 'PENDING' | 'PROCESSING' | 'RESOLVED' = 'all';

  private apiUrl = 'http://localhost:8081/api/feedbacks';

  constructor(private http: HttpClient) { }

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
    formData.append('buildingId', '1'); // Default building ID
    formData.append('roomId', this.currentRoomId?.toString() || '1');

    console.log('📤 Uploading file:', this.selectedFile.name);
    console.log('📤 BuildingId: 1, RoomId:', this.currentRoomId || 1);

    this.http.post<any>('http://localhost:8081/api/upload', formData)
      .subscribe({
        next: res => {
          console.log('✅ RAW Upload response:', res);
          console.log('✅ Response type:', typeof res);
          console.log('✅ Response keys:', Object.keys(res || {}));

          let imageUrl = '';

          if (typeof res === 'string') {
            imageUrl = res;
            console.log('📌 Format: Direct string');
          } else if (res.url) {
            imageUrl = res.url;
            console.log('📌 Format: res.url');
          } else if (res.data && res.data.url) {
            imageUrl = res.data.url;
            console.log('📌 Format: res.data.url');
          } else if (res.data && typeof res.data === 'string') {
            imageUrl = res.data;
            console.log('📌 Format: res.data (string)');
          }

          console.log('🔗 Final imageUrl:', imageUrl);

          // Fix URL if backend returns localhost:8080 instead of 8081
          if (imageUrl && imageUrl.includes('localhost:8080')) {
            imageUrl = imageUrl.replace('localhost:8080', 'localhost:8081');
            console.log('🔧 Fixed URL to use port 8081:', imageUrl);
          }

          if (imageUrl) {
            this.newFeedback.attachmentUrl = imageUrl;
            console.log('✅ Image URL set to newFeedback.attachmentUrl:', this.newFeedback.attachmentUrl);
            console.log('✅ Current newFeedback object:', JSON.stringify(this.newFeedback, null, 2));
            alert('Upload ảnh thành công! URL: ' + imageUrl);
          } else {
            console.error('❌ No URL in response:', res);
            alert('Upload ảnh thất bại: Không nhận được URL từ server');
          }
        },
        error: err => {
          console.error('❌ Upload error:', err);
          console.error('❌ Error status:', err.status);
          console.error('❌ Error message:', err.message);
          let errorMsg = 'Upload ảnh thất bại';

          if (err.error?.message) {
            errorMsg += ': ' + err.error.message;
          } else if (err.status === 413) {
            errorMsg = 'Ảnh quá lớn! Vui lòng chọn ảnh nhỏ hơn 5MB';
          } else if (err.status === 415) {
            errorMsg = 'Định dạng file không hợp lệ! Chỉ chấp nhận: JPG, PNG, GIF';
          } else if (err.status === 0) {
            errorMsg = 'Không kết nối được server! Kiểm tra backend đang chạy không';
          } else if (err.status === 400) {
            errorMsg = 'Yêu cầu không hợp lệ: ' + (err.error?.message || 'Thiếu thông tin cần thiết');
          }

          alert(errorMsg);
          this.selectedFile = null;
        }
      });
  }
  editFeedback(feedback: Feedback) {
    this.editingFeedbackId = feedback.id;

    this.newFeedback = {
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

  removeImage() {
    this.newFeedback.attachmentUrl = '';
    this.selectedFile = null;
    // Reset file input
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

}