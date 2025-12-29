import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Feedback {
  id: number;
  title: string;
  content: string;
  status: 'PENDING' | 'PROCESSING' | 'RESOLVED' | 'CANCELED';
  createdAt: string;
  resolvedAt?: string;
  landlordNote?: string;
  attachmentUrl?: string;
  tenant: { user: { fullName: string; email: string } };
  room: { id: number; name: string; building: { name: string } };
}

@Component({
  selector: 'app-landlord-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './landlord-feedback.component.html',
  styleUrl: './landlord-feedback.component.css'
})
export class LandlordFeedbackComponent implements OnInit {
  feedbacks: Feedback[] = [];
  filteredFeedbacks: Feedback[] = [];
  loading = false;
  
  filterStatus: 'all' | 'PENDING' | 'PROCESSING' | 'RESOLVED' | 'CANCELED' = 'all';
  selectedFeedback: Feedback | null = null;
  showDetailModal = false;
  
  processForm = {
    status: '' as 'PROCESSING' | 'RESOLVED' | 'CANCELED',
    landlordNote: ''
  };

  private apiUrl = 'http://localhost:8081/api/feedbacks';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadFeedbacks();
  }

  loadFeedbacks() {
    this.loading = true;
    this.http.get<any>(`${this.apiUrl}/landlord`, {
      params: { page: '0', size: '20' }
    }).subscribe({
      next: (response) => {
        this.feedbacks = response.content || [];
        this.applyFilter();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading feedbacks:', err);
        this.loading = false;
      }
    });
  }

  applyFilter() {
    if (this.filterStatus === 'all') {
      this.filteredFeedbacks = [...this.feedbacks];
    } else {
      this.filteredFeedbacks = this.feedbacks.filter(f => f.status === this.filterStatus);
    }
  }

  setFilter(status: typeof this.filterStatus) {
    this.filterStatus = status;
    this.applyFilter();
  }

  openDetail(feedback: Feedback) {
    this.selectedFeedback = feedback;
    this.showDetailModal = true;
    this.processForm = {
      status: 'PROCESSING',
      landlordNote: feedback.landlordNote || ''
    };
  }

  closeDetail() {
    this.showDetailModal = false;
    this.selectedFeedback = null;
  }

  startProcessing(feedback: Feedback) {
    this.processForm.status = 'PROCESSING';
    this.processFeedback(feedback.id);
  }

  resolveFeedback(feedbackId: number) {
    if (!this.processForm.landlordNote) {
      alert('Vui lòng nhập ghi chú xử lý!');
      return;
    }
    this.processForm.status = 'RESOLVED';
    this.processFeedback(feedbackId);
  }

  cancelFeedback(feedbackId: number) {
    if (!confirm('Bạn có chắc muốn hủy phản hồi này?')) return;
    this.processForm.status = 'CANCELED';
    this.processFeedback(feedbackId);
  }

  processFeedback(feedbackId: number) {
    this.http.put(`${this.apiUrl}/${feedbackId}/process`, this.processForm).subscribe({
      next: () => {
        alert('Cập nhật trạng thái thành công!');
        this.closeDetail();
        this.loadFeedbacks();
      },
      error: (err) => {
        console.error('Error processing feedback:', err);
        alert('Có lỗi xảy ra!');
      }
    });
  }

  deleteFeedback(feedbackId: number) {
    if (!confirm('Bạn có chắc muốn xóa phản hồi này?')) return;
    
    this.http.delete(`${this.apiUrl}/${feedbackId}`).subscribe({
      next: () => {
        alert('Đã xóa phản hồi!');
        this.loadFeedbacks();
      },
      error: (err) => {
        console.error('Error deleting feedback:', err);
        alert('Có lỗi xảy ra!');
      }
    });
  }

  getStatusText(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'Chưa xử lý',
      'PROCESSING': 'Đang xử lý',
      'RESOLVED': 'Đã xử lý',
      'CANCELED': 'Đã hủy'
    };
    return statusMap[status] || status;
  }

  getStatusColor(status: string): string {
    const colorMap: { [key: string]: string } = {
      'PENDING': '#ffc107',
      'PROCESSING': '#17a2b8',
      'RESOLVED': '#28a745',
      'CANCELED': '#6c757d'
    };
    return colorMap[status] || '#6c757d';
  }

  getStatusIcon(status: string): string {
    const iconMap: { [key: string]: string } = {
      'PENDING': 'clock',
      'PROCESSING': 'cog fa-spin',
      'RESOLVED': 'check-circle',
      'CANCELED': 'ban'
    };
    return iconMap[status] || 'question-circle';
  }

  get pendingCount(): number {
    return this.feedbacks.filter(f => f.status === 'PENDING').length;
  }

  get processingCount(): number {
    return this.feedbacks.filter(f => f.status === 'PROCESSING').length;
  }

  get resolvedCount(): number {
    return this.feedbacks.filter(f => f.status === 'RESOLVED').length;
  }

  get canceledCount(): number {
    return this.feedbacks.filter(f => f.status === 'CANCELED').length;
  } 
}