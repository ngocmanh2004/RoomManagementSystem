import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Review, ReviewRequest, ReviewResponse } from '../../../models/review.model';
import { ReviewService } from '../../../services/review.service';
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

  private destroy$ = new Subject<void>();

  constructor(private reviewService: ReviewService) {}

ngOnInit() {
  console.log('📍 ReviewList: Component initialized with roomId:', this.roomId);  // ✅ DEBUG
  this.checkLoginStatus();
  this.loadReviews(0);
}

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * ✅ Check xem user có token hay không
   */
  checkLoginStatus() {
    // ✅ FIX: Check 'accessToken' thay vì 'token'
    const token = localStorage.getItem('accessToken');
    this.isLoggedIn = !!token;
    console.log('✅ ReviewList: Login status:', this.isLoggedIn, 'Token:', token?.substring(0, 20) + '...');
  }

  loadReviews(page: number) {
    this.isLoading = true;
    this.errorMessage = '';

    console.log('📥 ReviewList: Loading reviews for room', this.roomId, 'page', page);

    this.reviewService.getReviewsByRoom(this.roomId, page, 10)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: ReviewResponse) => {
          console.log('✅ ReviewList: Reviews loaded:', response.content.length);
          this.reviews = response.content;
          this.currentPage = page;
          this.totalPages = response.totalPages;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('❌ ReviewList: Error loading reviews:', error);
          this.errorMessage = 'Không thể tải đánh giá. Vui lòng thử lại.';
          this.isLoading = false;
        }
      });
  }

  onFormSubmit(request: ReviewRequest) {
    console.log('🎬 ReviewList: Form submitted:', request);
    
    if (this.editingReview) {
      this.updateReview(request);
    } else {
      this.createReview(request);
    }
  }
// ...existing code...

private createReview(request: ReviewRequest) {
  console.log('➕ ReviewList: Creating new review');
  console.log('   Request:', JSON.stringify(request));
  
  this.reviewService.createReview(request)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (response) => {
        console.log('✅ ReviewList: Review created successfully', response);
        alert('Đánh giá đã được gửi thành công!');
        this.showForm = false;
        this.loadReviews(0);
      },
      error: (error) => {
        console.error('❌ ReviewList: Error creating review:', error);
        // ✅ FIX: Better error message extraction
        let message = 'Lỗi gửi đánh giá';
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

private updateReview(request: ReviewRequest) {
  if (!this.editingReview) return;

  console.log('✏️ ReviewList: Updating review', this.editingReview.id);
  console.log('   Request:', JSON.stringify(request));

  this.reviewService.updateReview(this.editingReview.id, request)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (response) => {
        console.log('✅ ReviewList: Review updated successfully', response);
        alert('Đánh giá đã được cập nhật!');
        this.showForm = false;
        this.editingReview = undefined;
        this.loadReviews(this.currentPage);
      },
      error: (error) => {
        console.error('❌ ReviewList: Error updating review:', error);
        // ✅ FIX: Better error message extraction
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
  console.log('🗑️ ReviewList: Delete review:', id);
  
  if (!confirm('Bạn có chắc muốn xóa đánh giá này?')) {
    console.log('❌ ReviewList: Delete cancelled by user');
    return;
  }

  this.reviewService.deleteReview(id)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (response) => {
        console.log('✅ ReviewList: Delete response:', response);
        
        // ✅ Remove from local array immediately (optimistic update)
        this.reviews = this.reviews.filter(r => r.id !== id);
        console.log('✅ ReviewList: Review removed from list immediately');
        
        alert('Đánh giá đã được xóa thành công!');
        
        // ✅ Reload list after short delay
        setTimeout(() => {
          this.loadReviews(this.currentPage);
        }, 500);
      },
      error: (error) => {
        console.error('❌ ReviewList: Error deleting review:', error);
        
        // ✅ Better error extraction
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
        
        console.error('Error message:', message);
        alert('Lỗi: ' + message);
      }
    });
}

  onFormCancel() {
    console.log('❌ ReviewList: Form cancelled');
    this.showForm = false;
    this.editingReview = undefined;
  }

  onEditReview(review: Review) {
    console.log('✏️ ReviewList: Edit review:', review.id);
    
    if (!this.isLoggedIn) {
      alert('Vui lòng đăng nhập để chỉnh sửa đánh giá');
      return;
    }
    
    this.editingReview = review;
    this.showForm = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  openReviewForm() {
    console.log('📝 ReviewList: Open review form. Logged in:', this.isLoggedIn);
    
    if (!this.isLoggedIn) {
      alert('Vui lòng đăng nhập để viết đánh giá');
      return;
    }
    this.showForm = true;
  }

  getPaginationPages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  trackByReviewId(index: number, review: Review): number {
    return review.id;
  }
}