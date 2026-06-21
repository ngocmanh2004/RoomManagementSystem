import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BookingService } from '../../services/booking.service';
import { CommonModule } from '@angular/common';
import { Contract } from '../../models/contract.model';

interface ApiResponse {
  success: boolean;
  message: string;
  data: Contract;
}

@Component({
  selector: 'app-contract-detail',  
  standalone: true,
  imports: [CommonModule],
  templateUrl: './contract-detail.component.html',
  styleUrls: ['./contract-detail.component.css']
})
export class ContractDetailComponent implements OnInit {  
  contract: Contract | null = null;
  loading = false;
  error = '';
  hasContract = false;

  constructor(
    private bookingService: BookingService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadContract();
  }

  loadContract(): void {
    this.loading = true;
    this.error = '';
    this.bookingService.getMyActiveContract().subscribe({
      next: (contract) => {
        console.log('Contract loaded:', contract);
        this.contract = contract;
        this.hasContract = true;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading contract:', err);
        this.loading = false;
        if (err.message && err.message.includes('Bạn chưa có hợp đồng')) {
          this.hasContract = false;
          this.error = 'Bạn chưa có hợp đồng thuê phòng';
        } else if (err.message && err.message.includes('Unauthorized')) {
          this.error = 'Vui lòng đăng nhập để xem hợp đồng';
          setTimeout(() => this.router.navigate(['/login']), 2000);
        } else {
          this.error = err.message || 'Không thể tải hợp đồng';
        }
      }
    });
  }

  getContractCode(): string {
    if (!this.contract) return '';
    return this.contract.contractCodeGenerated || this.contract.contractCode || '';
  }

  getMonthlyRent(): number {
    if (!this.contract) return 0;
    return this.contract.monthlyRentCalculated || this.contract.monthlyRent || 0;
  }

  getStatusClass(): string {
    if (!this.contract) return '';
    
    switch (this.contract.status) {
      case 'PENDING':
        return 'status-pending';
      case 'ACTIVE':
        return 'status-active';
      case 'APPROVED':
        return 'status-approved';
      case 'EXPIRED':
        return 'status-expired';
      case 'CANCELLED':
        return 'status-cancelled';
      case 'REJECTED':
        return 'status-rejected';
      default:
        return 'status-pending';
    }
  }
  getStatusDisplayName(): string {
    if (!this.contract) return '';
    return this.contract.statusDisplayName || this.contract.status || '';
  }

  downloadPDF(): void {
    if (!this.contract) return;
    
    alert(`Đang tải PDF cho hợp đồng ${this.getContractCode()}...\nChức năng đang được phát triển`);
  }

  goToRoomSearch(): void {
    this.router.navigate(['/rooms']);
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }
}