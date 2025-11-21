import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Review } from '../../../models/review.model';
import { StarRatingComponent } from '../star-rating/star-rating.component';

@Component({
  selector: 'app-review-card',
  standalone: true,
  imports: [CommonModule, StarRatingComponent],
  templateUrl: './review-card.component.html',
  styleUrl: './review-card.component.css'
})
export class ReviewCardComponent {
  @Input() review!: Review;           // ✅ Nhận review data
  @Input() isOwner = false;           // ✅ Nhận flag isOwner
  
  @Output() onEdit = new EventEmitter<Review>();      // ✅ Emit Review object
  @Output() onDelete = new EventEmitter<number>();    // ✅ Emit review ID

  /**
   * ✅ Edit handler
   */
  editReview() {
    console.log('✏️ ReviewCard: Edit clicked for review', this.review.id);
    this.onEdit.emit(this.review);
  }

  /**
   * ✅ Delete handler
   */
  deleteReview() {
    console.log('🗑️ ReviewCard: Delete clicked for review', this.review.id);
    this.onDelete.emit(this.review.id);
  }

  /**
   * ✅ Format date
   */
  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  /**
   * ✅ Get rating label
   */
  getRatingLabel(rating: number): string {
    const labels: { [key: number]: string } = {
      5: 'Rất tốt',
      4: 'Tốt',
      3: 'Bình thường',
      2: 'Kém',
      1: 'Rất kém'
    };
    return labels[rating] || 'Không xác định';
  }
}