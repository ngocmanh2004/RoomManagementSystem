import { Component, Input, OnInit, OnDestroy } from '@angular/core';
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
export class ReviewListComponent implements OnInit, OnDestroy {
  @Input() roomId!: number;

  reviews: Review[] = [];
  showForm = false;
  editingReview?: Review;
  currentPage = 0;
  totalPages = 1;
  isLoggedIn = false;
  isLoading = false;
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
    if (this.isLoggedIn && this.currentUserId) {
      this.loadReviews(0);
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  checkLoginStatus() {
    this.isLoggedIn = this.authService.isLoggedIn();
    
    if (this.isLoggedIn) {
      this.currentUserId = this.authService.getCurrentUserId() ?? undefined;
      console.log('✅ Review Component - Current User ID:', this.currentUserId);
      console.log('✅ Review Component - Is Logged In:', this.isLoggedIn);
    } else {
      this.currentUserId = undefined;
      console.log('⚠️ Review Component - Not logged in');
    }
  }

  loadReviews(page: number) {
    if (!this.isLoggedIn || !this.currentUserId) {
      console.warn('⚠️ Cannot load reviews - not logged in or no user ID');
      this.showForm = false;
      this.currentUserReview = undefined;
      this.reviews = [];
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    console.log('📍 Loading reviews for room:', this.roomId, 'with userId:', this.currentUserId);

    this.reviewService.getReviewsByRoom(this.roomId, page, 10)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: ReviewResponse) => {
          console.log('📥 Reviews received:', response.content);
          this.processReviews(response.content);
          this.currentPage = page;
          this.totalPages = response.totalPages;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('❌ Error loading reviews:', error);
          this.errorMessage = 'Không thể tải đánh giá. Vui lòng thử lại.';
          this.isLoading = false;
        }
      });
  }

  private processReviews(allReviews: Review[]) {
    console.log('🔍 ===== PROCESSING REVIEWS =====');
    console.log('📊 Current User ID:', this.currentUserId);
    console.log('📥 All reviews from backend:', allReviews);
    
    // ✅ Print từng review để debug
    allReviews.forEach((r, index) => {
      console.log(`Review ${index}:`, {
        id: r.id,
        tenantId: r.tenantId,
        tenantName: r.tenantName,
        rating: r.rating,
        match: r.tenantId === this.currentUserId
      });
    });

    // ✅ So sánh tenantId (Backend trả tenantId, không phải userId)
    const userReview = allReviews.find(r => {
      const match = r.tenantId === this.currentUserId;
      console.log(`  Checking review ${r.id}: tenantId=${r.tenantId}, currentUserId=${this.currentUserId}, match=${match}`);
      return match;
    });

    if (userReview) {
      console.log('✅ Found user review:', userReview);
      this.currentUserReview = userReview;
      this.showForm = false;
      this.editingReview = undefined;
      this.reviews = allReviews.filter(r => r.tenantId !== this.currentUserId);
    } else {
      console.log('❌ No user review found - showing form');
      this.currentUserReview = undefined;
      this.showForm = true;
      this.editingReview = undefined;
      this.reviews = allReviews;
    }

    console.log('📊 Final state - showForm:', this.showForm, 'userReview:', this.currentUserReview);
    console.log('🔍 ===== END PROCESSING =====\n');
  }

  onFormSubmit(request: ReviewRequest) {
    if (this.editingReview) {
      this.updateReview(request);
    } else {
      this.createReview(request);
    }
  }

  private createReview(request: ReviewRequest) {
    this.reviewService.createReview(request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('✅ Review created:', response);
          alert('Đánh giá đã được gửi thành công!');
          
          const reviewWithTenantId: Review = {
            ...response,
            tenantId: this.currentUserId || 0
          };
          this.currentUserReview = reviewWithTenantId;
          this.showForm = false;
          this.editingReview = undefined;
          this.reviews = this.reviews.filter(r => r.id !== response.id);
        },
        error: (error) => {
          console.error('❌ Error creating review:', error);
          let message = 'Lỗi gửi đánh giá';
          
          if (error.error?.message) {
            message = error.error.message;
            
            if (message.includes('đã đánh giá')) {
              setTimeout(() => {
                console.log('🔄 Reloading reviews...');
                this.loadReviews(0);
              }, 1000);
            }
          } else if (error.error) {
            message = typeof error.error === 'string' ? error.error : JSON.stringify(error.error);
          } else if (error.message) {
            message = error.message;
          }
          
          alert('Lỗi: ' + message);
        }
      });
  }

  private updateReview(request: ReviewRequest) {
    if (!this.editingReview) return;

    this.reviewService.updateReview(this.editingReview.id, request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('✅ Review updated:', response);
          alert('Đánh giá đã được cập nhật!');
          
          const reviewWithTenantId: Review = {
            ...response,
            tenantId: this.currentUserId || 0
          };
          this.currentUserReview = reviewWithTenantId;
          this.showForm = false;
          this.editingReview = undefined;
        },
        error: (error) => {
          console.error('❌ Error updating review:', error);
          let message = 'Lỗi cập nhật đánh giá';
          if (error.error?.message) {
            message = error.error.message;
          } else if (error.error) {
            message = typeof error.error === 'string' ? error.error : JSON.stringify(error.error);
          } else if (error.message) {
            message = error.message;
          }
          alert('Lỗi: ' + message);
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
        next: (response) => {
          console.log('✅ Review deleted');
          alert('Đánh giá đã được xóa thành công!');
          this.currentUserReview = undefined;
          this.showForm = true;
          this.editingReview = undefined;
        },
        error: (error) => {
          console.error('❌ Error deleting review:', error);
          let message = 'Không thể xóa đánh giá';
          
          if (error?.error?.message) {
            message = error.error.message;
          } else if (error?.error?.text) {
            message = error.error.text;
          } else if (typeof error?.error === 'string') {
            message = error.error;
          } else if (error?.message) {
            message = error.message;
          }
          
          alert('Lỗi: ' + message);
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