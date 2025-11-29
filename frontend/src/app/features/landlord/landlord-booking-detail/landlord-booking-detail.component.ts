import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LandlordBookingService } from '../../../services/landlord-booking.service';
import { Contract } from '../../../models/contract.model';

@Component({
  selector: 'app-landlord-booking-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './landlord-booking-detail.component.html',
  styleUrl: './landlord-booking-detail.component.css'
})
export class LandlordBookingDetailComponent implements OnInit {
  contract: Contract | null = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  
  showExtendModal = false;
  newEndDate = '';
  extendNotes = '';
  isExtending = false;

  // Reject modal
  showRejectModal = false;
  rejectionReason = '';
  isRejecting = false;
  
  // Approve confirmation
  showApproveModal = false;
  isApproving = false;

  // ✅ NEW: Terminate modal
  showTerminateModal = false;
  terminationType: 'EXPIRED' | 'CANCELLED' = 'EXPIRED';
  terminationReason = '';
  isTerminating = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private landlordBookingService: LandlordBookingService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadContractDetail(+id);
    }
  }

  loadContractDetail(id: number) {
    this.isLoading = true;
    this.errorMessage = '';

    this.landlordBookingService.getContractDetail(id).subscribe({
      next: (contract) => {
        this.contract = contract;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.message;
        this.isLoading = false;
      }
    });
  }

  openApproveModal() {
    this.showApproveModal = true;
  }

  closeApproveModal() {
    this.showApproveModal = false;
  }

  confirmApprove() {
    if (!this.contract) return;

    this.isApproving = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.landlordBookingService.approveContract(this.contract.id).subscribe({
      next: (updatedContract) => {
        this.contract = updatedContract;
        this.successMessage = 'Đã duyệt hợp đồng thành công!';
        this.isApproving = false;
        this.closeApproveModal();
        
        setTimeout(() => {
          this.router.navigate(['/landlord/bookings']);
        }, 2000);
      },
      error: (error) => {
        this.errorMessage = error.message;
        this.isApproving = false;
      }
    });
  }

  openRejectModal() {
    this.showRejectModal = true;
    this.rejectionReason = '';
  }

  closeRejectModal() {
    this.showRejectModal = false;
    this.rejectionReason = '';
  }

  confirmReject() {
    if (!this.contract) return;
    
    if (!this.rejectionReason || this.rejectionReason.trim().length === 0) {
      alert('Vui lòng nhập lý do từ chối');
      return;
    }

    this.isRejecting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.landlordBookingService.rejectContract(this.contract.id, this.rejectionReason).subscribe({
      next: (updatedContract) => {
        this.contract = updatedContract;
        this.successMessage = 'Đã từ chối hợp đồng!';
        this.isRejecting = false;
        this.closeRejectModal();
        
        setTimeout(() => {
          this.router.navigate(['/landlord/bookings']);
        }, 2000);
      },
      error: (error) => {
        this.errorMessage = error.message;
        this.isRejecting = false;
      }
    });
  }

  // ✅ NEW: Terminate Contract Methods
  openTerminateModal() {
    this.showTerminateModal = true;
    this.terminationType = 'EXPIRED'; // Default
    this.terminationReason = '';
  }

  closeTerminateModal() {
    this.showTerminateModal = false;
    this.terminationType = 'EXPIRED';
    this.terminationReason = '';
  }

  confirmTerminate() {
    if (!this.contract) return;

    this.isTerminating = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.landlordBookingService.terminateContract(
      this.contract.id,
      this.terminationType,
      this.terminationReason
    ).subscribe({
      next: (updatedContract) => {
        this.contract = updatedContract;
        this.successMessage = 'Đã thanh lý hợp đồng thành công! Phòng đã sẵn sàng cho thuê mới.';
        this.isTerminating = false;
        this.closeTerminateModal();
        
        setTimeout(() => {
          this.router.navigate(['/landlord/bookings']);
        }, 2500);
      },
      error: (error) => {
        this.errorMessage = error.message;
        this.isTerminating = false;
      }
    });
  }

  goBack() {
    this.router.navigate(['/landlord/bookings']);
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'status-pending',
      'ACTIVE': 'status-active',
      'APPROVED': 'status-approved',
      'REJECTED': 'status-rejected',
      'EXPIRED': 'status-expired',
      'CANCELLED': 'status-cancelled'
    };
    return statusMap[status] || '';
  }

  getStatusLabel(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'Chờ duyệt',
      'ACTIVE': 'Đang hiệu lực',
      'APPROVED': 'Đã duyệt',
      'REJECTED': 'Đã từ chối',
      'EXPIRED': 'Hết hạn',
      'CANCELLED': 'Đã hủy'
    };
    return statusMap[status] || status;
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('vi-VN').format(price);
  }

  formatDate(dateString: string | null | undefined): string {
  if (!dateString) return 'Không xác định';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
}

  isPending(): boolean {
    return this.contract?.status === 'PENDING';
  }

  // ✅ NEW: Check if contract is ACTIVE
  isActive(): boolean {
    return this.contract?.status === 'ACTIVE';
  }

  openExtendModal() {
  this.showExtendModal = true;
  
  // Set minimum date to today
  const today = new Date();
  const minDate = today.toISOString().split('T')[0];
  
  // Set default new end date to 1 year from now or from current end date
  if (this.contract?.endDate) {
    const currentEnd = new Date(this.contract.endDate);
    currentEnd.setFullYear(currentEnd.getFullYear() + 1);
    this.newEndDate = currentEnd.toISOString().split('T')[0];
  } else {
    const oneYearLater = new Date();
    oneYearLater.setFullYear(oneYearLater.getFullYear() + 1);
    this.newEndDate = oneYearLater.toISOString().split('T')[0];
  }
  
  this.extendNotes = '';
}

// ✅ ADD: Close extend modal
closeExtendModal() {
  this.showExtendModal = false;
  this.newEndDate = '';
  this.extendNotes = '';
}

// ✅ ADD: Confirm extend
confirmExtend() {
  if (!this.contract) return;
  
  if (!this.newEndDate) {
    alert('Vui lòng chọn ngày kết thúc mới');
    return;
  }
  
  // Validate new end date
  const newDate = new Date(this.newEndDate);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  if (newDate < today) {
    alert('Ngày kết thúc mới phải lớn hơn hoặc bằng ngày hiện tại');
    return;
  }
  
  if (this.contract.endDate) {
    const currentEnd = new Date(this.contract.endDate);
    if (newDate <= currentEnd) {
      alert('Ngày kết thúc mới phải lớn hơn ngày kết thúc hiện tại');
      return;
    }
  }
  
  this.isExtending = true;
  this.errorMessage = '';
  this.successMessage = '';
  
  this.landlordBookingService.extendContract(
    this.contract.id,
    this.newEndDate,
    this.extendNotes
  ).subscribe({
    next: (updatedContract) => {
      this.contract = updatedContract;
      this.successMessage = 'Gia hạn hợp đồng thành công!';
      this.isExtending = false;
      this.closeExtendModal();
      
      // Reload contract detail
      setTimeout(() => {
        this.successMessage = '';
      }, 3000);
    },
    error: (error) => {
      this.errorMessage = error.message;
      this.isExtending = false;
    }
  });
}

// ✅ Helper: Get minimum date for date input (today)
getMinEndDate(): string {
  const today = new Date();
  return today.toISOString().split('T')[0];
}
}