import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReviewReport, ReviewReportStatus } from '../../../models/review-report.model';
import { ReviewService } from '../../../services/review.service';
import { UserService } from '../../../services/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-report-management',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule /*, các module khác nếu cần */],
  templateUrl: './report-management.component.html',
  styleUrl: './report-management.component.css'
})
export class ReportManagementComponent implements OnInit {
  reports: ReviewReport[] = [];
  filteredReports: ReviewReport[] = [];
  selectedStatus: ReviewReportStatus | 'ALL' = 'ALL';
  selectedReason: string = 'ALL';
  isLoading = false;
  errorMessage = '';
  selectedReport: ReviewReport | null = null;
  isDetailModalOpen = false;
  processForm: FormGroup;
  editReviewId: number | null = null;
  editReviewData: any = null;
  isEditReviewModalOpen = false;
  editReviewForm: FormGroup;

  constructor(
    private reviewService: ReviewService,
    private fb: FormBuilder,
    private userService: UserService
  ) {
    this.processForm = this.fb.group({
      status: ['PROCESSING'],
      note: ['']
    });
    this.editReviewForm = this.fb.group({
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadReports();
  }

  loadReports() {
    this.isLoading = true;
    this.reviewService.getReviewReports().subscribe({
      next: (data: any) => {
        // Nếu trả về dạng Page, lấy content
        this.reports = Array.isArray(data) ? data : data.content;
        this.applyFilters();
        this.isLoading = false;
      },
      error: err => {
        this.errorMessage = 'Lỗi tải báo cáo!';
        this.isLoading = false;
      }
    });
  }

  applyFilters() {
    this.filteredReports = this.reports.filter(r => 
      (this.selectedStatus === 'ALL' || r.status === this.selectedStatus) &&
      (this.selectedReason === 'ALL' || r.reason === this.selectedReason)
    );
  }

  onStatusFilterChange() {
    this.applyFilters();
  }

  onReasonFilterChange() {
    this.applyFilters();
  }

  openDetailModal(report: ReviewReport) {
    this.selectedReport = report;
    this.processForm.patchValue({
      status: report.status === 'PENDING' ? 'PROCESSING' : report.status,
      note: report.note || ''
    });
    this.isDetailModalOpen = true;
  }

  closeDetailModal() {
    this.isDetailModalOpen = false;
    this.selectedReport = null;
  }

  processReport() {
    if (!this.selectedReport) return;
    const { status, note } = this.processForm.value;
    this.reviewService.updateReviewReport(this.selectedReport.id, { status, note }).subscribe({
      next: () => {
        alert('Báo cáo đã được xử lý thành công!');
        this.closeDetailModal();
        this.loadReports();
      },
      error: err => alert('Lỗi xử lý báo cáo!')
    });
  }

  lockAccount(userId: number | undefined) {
    if (!userId) return;
    if (confirm('Khóa tài khoản này? Người dùng sẽ bị đăng xuất ngay lập tức.')) {
      this.userService.updateStatus(userId, 'BANNED').subscribe({
        next: () => {
          alert('Đã khóa tài khoản thành công!');
          this.closeDetailModal();
          this.loadReports();
        },
        error: err => alert(err.error?.message || 'Lỗi khóa tài khoản')
      });
    }
  }

  deleteReview(reviewId: number | undefined) {
    if (!reviewId) return;
    if (confirm('Xóa đánh giá này?')) {
      this.reviewService.deleteReview(reviewId).subscribe({
        next: () => {
          alert('Đánh giá đã bị xóa!');
          this.closeDetailModal();
          this.loadReports();
        },
        error: () => alert('Lỗi xóa đánh giá!')
      });
    }
  }

  editReview(reviewId: number | undefined) {
    if (!reviewId || !this.selectedReport) return;
    this.editReviewId = reviewId;
    this.editReviewForm.patchValue({
      rating: this.selectedReport.reviewRating,
      comment: this.selectedReport.reviewContent
    });
    this.isEditReviewModalOpen = true;
  }

  closeEditReviewModal() {
    this.isEditReviewModalOpen = false;
    this.editReviewId = null;
  }

  submitEditReview() {
    if (!this.editReviewId) return;
    const reviewRequest = this.editReviewForm.value;
    this.reviewService.updateReview(this.editReviewId, reviewRequest).subscribe({
      next: () => {
        alert('Đã cập nhật đánh giá thành công!');
        this.isEditReviewModalOpen = false;
        this.closeDetailModal();
        this.loadReports();
      },
      error: err => alert(err.error?.message || 'Lỗi cập nhật đánh giá')
    });
  }

  getReasonLabel(reason: string | undefined): string {
    switch (reason) {
      case 'SPAM': return 'Spam';
      case 'OFFENSIVE': return 'Ngôn từ không phù hợp';
      case 'FALSE': return 'Sai sự thật';
      case 'OTHER': return 'Khác';
      default: return reason || '';
    }
  }
}
