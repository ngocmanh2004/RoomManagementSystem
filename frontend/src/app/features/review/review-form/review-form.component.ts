import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReviewRequest, Review } from '../../../models/review.model';
import { StarRatingComponent } from '../star-rating/star-rating.component';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-review-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, StarRatingComponent],
  templateUrl: './review-form.component.html',
  styleUrl: './review-form.component.css'
})
export class ReviewFormComponent implements OnInit, OnDestroy {
  @Input() roomId!: number;
  @Input() review?: Review;  // ✅ Nếu sửa thì có review, nếu tạo mới thì không có
  
  @Output() submit = new EventEmitter<ReviewRequest>();
  @Output() cancel = new EventEmitter<void>();

  reviewForm!: FormGroup;
  isEdit = false;
  isSubmitting = false;

  private destroy$ = new Subject<void>();

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.initForm();
    
    if (this.review) {
      this.isEdit = true;
      this.populateForm();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm() {
    this.reviewForm = this.fb.group({
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]]
    });
  }

  private populateForm() {
    if (this.review) {
      this.reviewForm.patchValue({
        rating: this.review.rating || 5,
        comment: this.review.comment || ''
      });
    }
  }

  onSubmit() {
    if (this.reviewForm.invalid) {
      alert('Vui lòng điền đầy đủ thông tin');
      return;
    }

    const reviewRequest: ReviewRequest = {
      roomId: this.roomId,
      rating: Number(this.reviewForm.get('rating')?.value),
      comment: (this.reviewForm.get('comment')?.value || '').trim()
    };

    this.isSubmitting = true;
    this.submit.emit(reviewRequest);

    setTimeout(() => {
      this.isSubmitting = false;
    }, 2000);
  }

  onCancel() {
    this.cancel.emit();
    this.resetForm();
  }

  resetForm() {
    this.reviewForm.reset({ rating: 5, comment: '' });
    this.isEdit = false;
  }

  onRatingChange(newRating: number) {
    this.reviewForm.patchValue({ rating: Number(newRating) });
  }

  getRatingError(): string {
    const ratingControl = this.reviewForm.get('rating');
    if (ratingControl?.hasError('required')) {
      return 'Vui lòng chọn xếp hạng';
    }
    return '';
  }

  getCommentCharCount(): number {
    return (this.reviewForm.get('comment')?.value || '').length;
  }
}