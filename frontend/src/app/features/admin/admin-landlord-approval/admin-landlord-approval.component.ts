import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LandlordService } from '../../../services/landlord.service';
import { LandlordRequest } from '../../../models/landlord.model';

@Component({
  selector: 'app-admin-landlord-approval',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-landlord-approval.component.html',
  styleUrls: ['./admin-landlord-approval.component.css']
})
export class AdminLandlordApprovalComponent implements OnInit {
  pendingRequests: LandlordRequest[] = [];
  allRequests: LandlordRequest[] = [];
  loading = true;
  
  selectedRequest: LandlordRequest | null = null;
  showRejectModal = false;
  rejectionReason = '';

  statistics: { [key: string]: number } = {};

  constructor(private landlordService: LandlordService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    
    // Load pending requests
    this.landlordService.getPendingRequests().subscribe({
      next: (response) => {
        this.pendingRequests = response.data || [];
      },
      error: (err) => console.error('Lỗi tải pending:', err)
    });

    // Load all requests
    this.landlordService.getAllRequests().subscribe({
      next: (response) => {
        this.allRequests = response.data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Lỗi tải all:', err);
        this.loading = false;
      }
    });

    // Load statistics
    this.landlordService.getRequestStatistics().subscribe({
      next: (response) => {
        this.statistics = response.data || {};
      },
      error: (err) => console.error('Lỗi tải stats:', err)
    });
  }

  viewDetail(request: LandlordRequest): void {
    this.selectedRequest = request;
  }

  closeDetail(): void {
    this.selectedRequest = null;
  }

  approveRequest(requestId: number): void {
    if (!confirm('Xác nhận duyệt đăng ký này?')) {
      return;
    }

    this.landlordService.approveRequest(requestId).subscribe({
      next: (response) => {
        alert(response.message);
        this.loadData();
        this.closeDetail();
      },
      error: (err) => {
        console.error('Lỗi:', err);
        alert(err.error?.message || 'Có lỗi xảy ra');
      }
    });
  }

  openRejectModal(request: LandlordRequest): void {
    this.selectedRequest = request;
    this.showRejectModal = true;
    this.rejectionReason = '';
  }

  closeRejectModal(): void {
    this.showRejectModal = false;
    this.rejectionReason = '';
  }

  confirmReject(): void {
    if (!this.selectedRequest) return;

    if (!this.rejectionReason.trim()) {
      alert('Vui lòng nhập lý do từ chối');
      return;
    }

    this.landlordService.rejectRequest(
      this.selectedRequest.id,
      this.rejectionReason
    ).subscribe({
      next: (response) => {
        alert(response.message);
        this.loadData();
        this.closeRejectModal();
        this.closeDetail();
      },
      error: (err) => {
        console.error('Lỗi:', err);
        alert(err.error?.message || 'Có lỗi xảy ra');
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    const classes: { [key: string]: string } = {
      'PENDING': 'badge-pending',
      'APPROVED': 'badge-approved',
      'REJECTED': 'badge-rejected'
    };
    return classes[status] || '';
  }

  getStatusText(status: string): string {
    const texts: { [key: string]: string } = {
      'PENDING': 'Chờ duyệt',
      'APPROVED': 'Đã duyệt',
      'REJECTED': 'Từ chối'
    };
    return texts[status] || status;
  }
}