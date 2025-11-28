import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { LandlordBookingService, ContractListResponse } from '../../../services/landlord-booking.service';
import { Contract } from '../../../models/contract.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-landlord-booking',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './landlord-booking.component.html',
  styleUrl: './landlord-booking.component.css'
})
export class LandlordBookingComponent implements OnInit {
  contracts: Contract[] = [];
  filteredContracts: Contract[] = [];
  isLoading = false;
  errorMessage = '';
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  
  // Filter & Search
  selectedStatus: string = '';
  searchText: string = '';
  
  statusOptions = [
    { value: '', label: 'Chọn...' },
    { value: 'PENDING', label: 'Chờ duyệt' },
    { value: 'ACTIVE', label: 'Đang hiệu lực' },
    { value: 'APPROVED', label: 'Đã duyệt' },
    { value: 'REJECTED', label: 'Đã từ chối' },
    { value: 'EXPIRED', label: 'Hết hạn' },
    { value: 'CANCELLED', label: 'Đã hủy' }
  ];

  // ✅ Statistics counters
  private allContracts: Contract[] = [];

  constructor(
    private landlordBookingService: LandlordBookingService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadContracts();
    this.loadAllContractsForStats();
  }

  loadContracts() {
    this.isLoading = true;
    this.errorMessage = '';

    this.landlordBookingService.getMyContracts(this.currentPage, this.pageSize, this.selectedStatus)
      .subscribe({
        next: (response: ContractListResponse) => {
          this.contracts = response.content;
          this.filteredContracts = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.currentPage = response.number;
          this.isLoading = false;
          
          // Apply search if exists
          if (this.searchText) {
            this.applySearch();
          }
        },
        error: (error) => {
          this.errorMessage = error.message;
          this.isLoading = false;
        }
      });
  }

  // ✅ Load all contracts for statistics (without pagination)
  loadAllContractsForStats() {
    this.landlordBookingService.getMyContracts(0, 1000, '')
      .subscribe({
        next: (response: ContractListResponse) => {
          this.allContracts = response.content;
        },
        error: (error) => {
          console.error('Error loading stats:', error);
        }
      });
  }

  // ✅ Get count by status for statistics cards
  getCountByStatus(status: string): number {
    return this.allContracts.filter(c => c.status === status).length;
  }

  onStatusChange() {
    this.currentPage = 0;
    this.loadContracts();
  }

  // ✅ Search functionality
  onSearch() {
    this.applySearch();
  }

  private applySearch() {
    if (!this.searchText || this.searchText.trim() === '') {
      this.filteredContracts = this.contracts;
      return;
    }

    const searchLower = this.searchText.toLowerCase().trim();
    this.filteredContracts = this.contracts.filter(contract => {
      return (
        contract.contractCodeGenerated?.toLowerCase().includes(searchLower) ||
        contract.fullName?.toLowerCase().includes(searchLower) ||
        contract.roomName?.toLowerCase().includes(searchLower) ||
        contract.phone?.includes(searchLower)
      );
    });
  }

  viewDetail(contractId: number) {
    this.router.navigate(['/landlord/bookings', contractId]);
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadContracts();
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

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }
}