import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReviewRequest } from '../../../models/review.model';
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
  @Input() editingReview?: any;                
  
  @Output() submit = new EventEmitter<ReviewRequest>();  
  @Output() cancel = new EventEmitter<void>();           

  reviewForm!: FormGroup;
  isEdit = false;
  isSubmitting = false;

  private destroy$ = new Subject<void>();

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    console.log('🔍 ReviewForm: roomId received:', this.roomId);  
    this.initForm();
    
    if (this.editingReview) {
      this.isEdit = true;
      this.populateForm();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * ✅ Khởi tạo form
   */
  private initForm() {
    this.reviewForm = this.fb.group({
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.maxLength(1000)]]
    });

    console.log('📝 ReviewForm: Form initialized');
  }

  /**
   * ✅ Điền dữ liệu khi edit
   */
  private populateForm() {
    if (this.editingReview) {
      this.reviewForm.patchValue({
        rating: this.editingReview.rating || 5,
        comment: this.editingReview.comment || ''
      });
      console.log('✏️ ReviewForm: Populated with existing review data:', this.editingReview);
    }
  }

  /**
   * ✅ Submit form
   */
  onSubmit() {
    console.log('🎯 ReviewForm: Submit clicked');
    console.log('   roomId:', this.roomId);
    console.log('   rating:', this.reviewForm.get('rating')?.value);
    console.log('   comment:', this.reviewForm.get('comment')?.value);

    // ✅ 1. Validate roomId FIRST
    if (!this.roomId || this.roomId <= 0) {
      console.error('❌ ReviewForm: MISSING roomId!', { roomId: this.roomId });
      alert('Lỗi: Không tìm thấy ID phòng. Vui lòng reload trang.');
      return;
    }

    // ✅ 2. Validate form
    if (this.reviewForm.invalid) {
      console.error('❌ ReviewForm: Form invalid');
      alert('Vui lòng điền đầy đủ và chính xác thông tin');
      return;
    }

    // ✅ 3. Validate rating explicitly
    const rating = this.reviewForm.get('rating')?.value;
    if (rating === null || rating === undefined || rating < 1 || rating > 5) {
      console.error('❌ ReviewForm: Rating invalid:', rating);
      alert('Vui lòng chọn xếp hạng từ 1 đến 5 sao');
      return;
    }

    // ✅ 4. Tạo request object WITH roomId
    const reviewRequest: ReviewRequest = {
      roomId: this.roomId,           // ✅ MUST HAVE
      rating: Number(rating),        // ✅ Convert to number
      comment: (this.reviewForm.get('comment')?.value || '').trim()
    };

    console.log('✅ ReviewForm: All validations passed');
    console.log('📤 ReviewForm: Submitting review:', JSON.stringify(reviewRequest));

    this.isSubmitting = true;
    this.submit.emit(reviewRequest);

    // Reset after 2 second
    setTimeout(() => {
      this.isSubmitting = false;
    }, 2000);
  }

  /**
   * ✅ Cancel form
   */
  onCancel() {
    console.log('❌ ReviewForm: Cancel clicked');
    this.cancel.emit();
    this.reviewForm.reset({ rating: 5 });
    this.isEdit = false;
  }

  /**
   * ✅ Xử lý rating change từ Star Rating component
   */
  onRatingChange(newRating: number) {
    console.log('⭐ ReviewForm: Rating changed to:', newRating);
    this.reviewForm.patchValue({ rating: Number(newRating) });
  }

  /**
   * ✅ Get rating error message
   */
  getRatingError(): string {
    const ratingControl = this.reviewForm.get('rating');
    if (ratingControl?.hasError('required')) {
      return 'Vui lòng chọn xếp hạng';
    }
    if (ratingControl?.hasError('min') || ratingControl?.hasError('max')) {
      return 'Xếp hạng phải từ 1 đến 5 sao';
    }
    return '';
  }

  /**
   * ✅ Get comment character count
   */
  getCommentCharCount(): number {
    return (this.reviewForm.get('comment')?.value || '').length;
  }
}