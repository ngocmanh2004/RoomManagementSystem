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
  @Input() review!: Review;
  @Input() isOwner = false;
  @Input() currentUserId?: number;
  
  @Output() edit = new EventEmitter<Review>();
  @Output() delete = new EventEmitter<number>();
  @Output() report = new EventEmitter<Review>();

  editReview() {
    this.edit.emit(this.review);
  }

  deleteReview() {
    this.delete.emit(this.review.id);
  }

  reportReview() {
    this.report.emit(this.review);
  }

  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

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