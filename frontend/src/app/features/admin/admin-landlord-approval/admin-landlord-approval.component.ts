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
  allRequests: LandlordRequest[] = [];
  filteredRequests: LandlordRequest[] = [];
  loading = true;
  
  selectedRequest: LandlordRequest | null = null;
  showRejectModal = false;
  rejectionReason = '';
  searchTerm = '';
  selectedStatus: string = 'ALL'; // Thêm property này

  statistics: { [key: string]: number } = {
    'PENDING': 0,
    'APPROVED': 0,
    'REJECTED': 0
  };

  constructor(private landlordService: LandlordService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;

    // Load all requests
    this.landlordService.getAllRequests().subscribe({
      next: (response) => {
        this.allRequests = response.data || [];
        this.filteredRequests = [...this.allRequests];
        this.calculateStatistics();
        this.loading = false;
      },
      error: (err) => {
        console.error('Lỗi tải dữ liệu:', err);
        this.loading = false;
      }
    });
  }

  calculateStatistics(): void {
    this.statistics = {
      'PENDING': 0,
      'APPROVED': 0,
      'REJECTED': 0
    };

    this.allRequests.forEach(request => {
      if (this.statistics[request.status] !== undefined) {
        this.statistics[request.status]++;
      }
    });
  }

  // CẬP NHẬT method filterRequests() này
  filterRequests(): void {
    let filtered = [...this.allRequests];

    // Lọc theo trạng thái
    if (this.selectedStatus !== 'ALL') {
      filtered = filtered.filter(request => request.status === this.selectedStatus);
    }

    // Lọc theo từ khóa tìm kiếm
    const term = this.searchTerm.toLowerCase().trim();
    if (term) {
      filtered = filtered.filter(request => {
        return (
          request.user.fullName.toLowerCase().includes(term) ||
          request.user.email.toLowerCase().includes(term) ||
          request.cccd.includes(term)
        );
      });
    }

    this.filteredRequests = filtered;
  }

  // THÊM method filterByStatus() này
  filterByStatus(status: string): void {
    this.selectedStatus = status;
    this.filterRequests();
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

  getDocumentId(request: LandlordRequest): string {
    // Tạo ID giấy phép dạng GP-KD-2024-001
    const date = new Date(request.createdAt);
    const year = date.getFullYear();
    const id = String(request.id).padStart(3, '0');
    return `GP-KD-${year}-${id}`;
  }

  getDocumentUrl(filePath: string | undefined | null): string {
    if (!filePath) return '';
    if (filePath.startsWith('http://') || filePath.startsWith('https://')) return filePath;
    if (filePath.startsWith('/')) return filePath;
    return `/images/${filePath}`;
  }

  openDocument(filePath: string | undefined | null): void {
    const url = this.getDocumentUrl(filePath);
    if (!url) return;
    window.open(url, '_blank');
  }
}