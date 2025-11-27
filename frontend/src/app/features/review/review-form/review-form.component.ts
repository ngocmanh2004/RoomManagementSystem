import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { ReviewRequest, Review } from '../../../models/review.model';
import { StarRatingComponent } from '../star-rating/star-rating.component';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-review-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, StarRatingComponent],
  templateUrl: './review-form.component.html',
  styleUrl: './review-form.component.css'
})
export class ReviewFormComponent implements OnInit, OnDestroy {
  @Input() roomId!: number;
  @Input() review?: Review;
  
  @Output() submit = new EventEmitter<ReviewRequest>();
  @Output() cancel = new EventEmitter<void>();

  reviewForm!: FormGroup;
  isEdit = false;
  isSubmitting = false;

  private destroy$ = new Subject<void>();

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.initForm();
    
    console.log('Form init - roomId:', this.roomId, 'review:', this.review);
    
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
      rating: [5, [Validators.required, this.ratingValidator.bind(this)]],
      comment: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]]
    });
  }

  private ratingValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return { required: true };
    }

    const numValue = parseInt(String(control.value), 10);
    
    if (isNaN(numValue) || numValue < 1 || numValue > 5) {
      return { invalidRating: true };
    }

    return null;
  }

  private populateForm() {
    if (this.review) {
      const rating = parseInt(String(this.review.rating), 10);
      this.reviewForm.patchValue({
        rating: rating >= 1 && rating <= 5 ? rating : 5,
        comment: this.review.comment || ''
      });
    }
  }

  onSubmit() {
    if (this.reviewForm.invalid) {
      const ratingControl = this.reviewForm.get('rating');
      const commentControl = this.reviewForm.get('comment');

      if (ratingControl?.hasError('invalidRating')) {
        alert('Vui lòng chọn xếp hạng từ 1 đến 5 sao');
        return;
      }

      if (commentControl?.hasError('required')) {
        alert('Bình luận không được để trống');
        return;
      }

      if (commentControl?.hasError('minlength')) {
        alert('Bình luận tối thiểu 10 ký tự');
        return;
      }

      alert('Vui lòng điền đầy đủ thông tin');
      return;
    }

    const ratingRaw = this.reviewForm.get('rating')?.value;
    const rating = parseInt(String(ratingRaw), 10);
    
    if (isNaN(rating) || rating < 1 || rating > 5) {
      alert('Giá trị xếp hạng không hợp lệ');
      return;
    }

    const comment = (this.reviewForm.get('comment')?.value || '').trim();

    if (comment.length < 10) {
      alert('Bình luận tối thiểu 10 ký tự');
      return;
    }

    const finalRoomId = parseInt(String(this.roomId), 10);
    
    if (!finalRoomId || finalRoomId <= 0) {
      alert('Lỗi: roomId không hợp lệ');
      console.error('Invalid roomId:', this.roomId);
      return;
    }

    const reviewRequest: ReviewRequest = {
      roomId: finalRoomId,
      rating: rating,
      comment: comment
    };

    console.log('Submitting review:', reviewRequest);
    console.log('Rating type:', typeof reviewRequest.rating);
    console.log('RoomId:', reviewRequest.roomId);

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
    const rating = parseInt(String(newRating), 10);
    this.reviewForm.patchValue({ rating: rating });
  }

  getRatingError(): string {
    const ratingControl = this.reviewForm.get('rating');
    if (ratingControl?.hasError('required')) {
      return 'Vui lòng chọn xếp hạng';
    }
    if (ratingControl?.hasError('invalidRating')) {
      return 'Xếp hạng phải từ 1 đến 5 sao';
    }
    return '';
  }

  getCommentCharCount(): number {
    return (this.reviewForm.get('comment')?.value || '').length;
  }
}