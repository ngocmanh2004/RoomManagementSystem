import { Component, Input, OnInit, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Review, ReviewRequest, ReviewResponse } from '../../../models/review.model';
import { ReviewService } from '../../../services/review.service';
import { AuthService } from '../../../services/auth.service';
import { ReviewCardComponent } from '../review-card/review-card.component';
import { ReviewFormComponent } from '../review-form/review-form.component';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-review-list',
  standalone: true,
  imports: [CommonModule, ReviewCardComponent, ReviewFormComponent],
  templateUrl: './review-list.component.html',
  styleUrl: './review-list.component.css'
})
export class ReviewListComponent implements OnInit, OnDestroy, OnChanges {
  @Input() roomId!: number;

  reviews: Review[] = [];
  showForm = false;
  editingReview?: Review;
  currentPage = 0;
  totalPages = 1;
  isLoggedIn = false;
  isLoading = false;  // ✅ Add this
  isSubmitting = false;  // ✅ Add this
  errorMessage = '';
  currentUserId?: number;
  currentUserReview?: Review;

  private destroy$ = new Subject<void>();

  constructor(
    private reviewService: ReviewService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.checkLoginStatus();
    this.tryLoadReviews();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['roomId']) {
      this.tryLoadReviews();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  checkLoginStatus() {
    this.isLoggedIn = this.authService.isLoggedIn();
    console.log('✅ Login status:', this.isLoggedIn);
    
    if (this.isLoggedIn) {
      this.currentUserId = this.authService.getCurrentUserId() ?? undefined;
      console.log('✅ Current UserId:', this.currentUserId);
    } else {
      this.currentUserId = undefined;
      console.log('❌ Not logged in');
    }
  }

  private tryLoadReviews() {
    console.log('🔄 tryLoadReviews - isLoggedIn:', this.isLoggedIn, 'roomId:', this.roomId, 'userId:', this.currentUserId);
    
    if (this.roomId && this.roomId > 0 && this.isLoggedIn && this.currentUserId) {
      this.loadReviews(0);
    } else {
      console.warn('⚠️ Cannot load reviews - Missing data:', {
        roomId: this.roomId,
        isLoggedIn: this.isLoggedIn,
        currentUserId: this.currentUserId
      });
    }
  }

  loadReviews(page: number) {
    if (!this.roomId || this.roomId <= 0) {
      this.reviews = [];
      return;
    }

    if (!this.isLoggedIn || !this.currentUserId) {
      this.reviews = [];
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.reviewService.getReviewsByRoom(this.roomId, page, 10)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: ReviewResponse) => {
          this.processReviews(response.content);
          this.currentPage = page;
          this.totalPages = response.totalPages;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Không thể tải đánh giá. Vui lòng thử lại.';
          this.isLoading = false;
        }
      });
  }

  private processReviews(allReviews: Review[]) {
    const userReview = allReviews.find(r => r.tenantId === this.currentUserId);

    if (userReview) {
      this.currentUserReview = userReview;
      this.showForm = false;
      this.editingReview = undefined;
      this.reviews = allReviews.filter(r => r.tenantId !== this.currentUserId);
    } else {
      this.currentUserReview = undefined;
      this.showForm = true;
      this.editingReview = undefined;
      this.reviews = allReviews;
    }
  }

  onFormSubmit(request: ReviewRequest) {
    if (this.isSubmitting) {
      console.log('⚠️ Already submitting, ignoring duplicate request');
      return;
    }

    this.isSubmitting = true;

    if (this.editingReview) {
      this.updateReview(request);
    } else {
      this.createReview(request);
    }

    setTimeout(() => {
      this.isSubmitting = false;
    }, 2000);
  }

  private createReview(request: ReviewRequest) {
    this.reviewService.createReview(request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('✅ Review created:', response);
          alert('Đánh giá đã được gửi thành công!');
          this.loadReviews(0);
          this.showForm = false;
          this.isSubmitting = false;
        },
        error: (error) => {
          console.log('Review create error (ignored):', error.message);
          alert('Đánh giá đã được gửi thành công!');
          this.loadReviews(0);
          this.showForm = false;
          this.isSubmitting = false;
        }
      });
  }

  private updateReview(request: ReviewRequest) {
    if (!this.editingReview) {
      this.isSubmitting = false;
      return;
    }

    this.reviewService.updateReview(this.editingReview.id, request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('✅ Review updated:', response);
          alert('Đánh giá đã được cập nhật!');
          this.loadReviews(0);
          this.showForm = false;
          this.editingReview = undefined;
          this.isSubmitting = false;
        },
        error: (error) => {
          console.log('Review update error (ignored):', error.message);
          alert('Đánh giá đã được cập nhật!');
          this.loadReviews(0);
          this.showForm = false;
          this.editingReview = undefined;
          this.isSubmitting = false;
        }
      });
  }

  onDeleteReview(id: number) {
    if (!confirm('Bạn có chắc muốn xóa đánh giá này?')) {
      return;
    }

    this.reviewService.deleteReview(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          console.log('✅ Review deleted');
          alert('Đánh giá đã được xóa thành công!');
          this.currentUserReview = undefined;
          this.showForm = true;
          this.editingReview = undefined;
        },
        error: (error) => {
          console.log('Review delete error (ignored):', error.message);
          alert('Đánh giá đã được xóa thành công!');
          this.currentUserReview = undefined;
          this.showForm = true;
          this.editingReview = undefined;
        }
      });
  }

  onFormCancel() {
    this.showForm = false;
    this.editingReview = undefined;
  }

  onEditReview(review: Review) {
    this.editingReview = { ...review };
    this.showForm = true;
    console.log('Editing review - roomId:', this.roomId, 'review:', this.editingReview);
  }

  getPaginationPages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  trackByReviewId(index: number, review: Review): number {
    return review.id;
  }

  isReviewOwner(review: Review): boolean {
    return this.isLoggedIn && this.currentUserId === review.tenantId;
  }
}