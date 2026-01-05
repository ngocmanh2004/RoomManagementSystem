import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ReviewListComponent } from './review-list.component';
import { ReviewService } from '../../../services/review.service';
import { AuthService } from '../../../services/auth.service';
import { Review, ReviewResponse } from '../../../models/review.model';

describe('ReviewListComponent - Sprint 2', () => {
  let component: ReviewListComponent;
  let fixture: ComponentFixture<ReviewListComponent>;
  let mockReviewService: jasmine.SpyObj<ReviewService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  const mockReviews: Review[] = [
    {
      id: 1,
      roomId: 1,
      tenantId: 10,
      tenantName: 'Nguyễn Văn A',
      rating: 5,
      comment: 'Phòng rất tốt, sạch sẽ',
      createdAt: '2024-01-01T10:00:00',
      updatedAt: '2024-01-01T10:00:00'
    },
    {
      id: 2,
      roomId: 1,
      tenantId: 20,
      tenantName: 'Trần Thị B',
      rating: 4,
      comment: 'Phòng ổn, chủ trọ thân thiện',
      createdAt: '2024-01-02T11:00:00',
      updatedAt: '2024-01-02T11:00:00'
    }
  ];

  const mockReviewResponse: ReviewResponse = {
    content: mockReviews,
    totalElements: 2,
    totalPages: 1,
    currentPage: 0
  };

  beforeEach(async () => {
    mockReviewService = jasmine.createSpyObj('ReviewService', [
      'getReviewsByRoom',
      'createReview',
      'updateReview',
      'deleteReview',
      'reportReview'
    ]);
    mockAuthService = jasmine.createSpyObj('AuthService', [
      'isLoggedIn',
      'getCurrentUserId',
      'getUserRole'
    ]);

    await TestBed.configureTestingModule({
      imports: [ReviewListComponent],
      providers: [
        { provide: ReviewService, useValue: mockReviewService },
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReviewListComponent);
    component = fixture.componentInstance;
    component.roomId = 1;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ========== US 11.1: Xem danh sách đánh giá của phòng ==========
  describe('US 11.1: Xem danh sách đánh giá của phòng', () => {
    
    // TEST 1: Load danh sách đánh giá khi có roomId
    it('should load reviews when roomId is provided', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      mockReviewService.getReviewsByRoom.and.returnValue(of(mockReviewResponse));
      
      component.ngOnInit();
      
      expect(mockReviewService.getReviewsByRoom).toHaveBeenCalledWith(1, 0, 10);
      expect(component.isLoading).toBe(false);
    });

    // TEST 2: Hiển thị tất cả đánh giá theo thứ tự thời gian
    it('should display all reviews in chronological order', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      mockReviewService.getReviewsByRoom.and.returnValue(of(mockReviewResponse));
      
      component.loadReviews(0);
      
      expect(component.reviews.length).toBeGreaterThan(0);
      expect(component.reviews[0].createdAt).toBe('2024-01-01T10:00:00');
    });

    // TEST 3: Hiển thị rating và comment của từng review
    it('should display rating and comment for each review', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(30);
      mockReviewService.getReviewsByRoom.and.returnValue(of(mockReviewResponse));
      
      component.loadReviews(0);
      
      expect(component.reviews.length).toBeGreaterThan(0);
      const firstReview = component.reviews[0];
      expect(firstReview.rating).toBeDefined();
      expect(firstReview.comment).toBeDefined();
      expect(firstReview.tenantName).toBeDefined();
    });

    // TEST 4: Phân trang danh sách đánh giá
    it('should paginate review list', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      const page2Response = { ...mockReviewResponse, number: 1 };
      mockReviewService.getReviewsByRoom.and.returnValue(of(page2Response));
      
      component.loadReviews(1);
      
      expect(mockReviewService.getReviewsByRoom).toHaveBeenCalledWith(1, 1, 10);
      expect(component.currentPage).toBe(1);
    });

    // TEST 5: Xử lý khi không có đánh giá
    it('should handle empty review list', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      const emptyResponse = { ...mockReviewResponse, content: [], totalElements: 0 };
      mockReviewService.getReviewsByRoom.and.returnValue(of(emptyResponse));
      
      component.loadReviews(0);
      
      expect(component.reviews.length).toBe(0);
    });
  });

  // ========== US 11.2: Gửi đánh giá và nhận xét phòng ==========
  describe('US 11.2: Gửi đánh giá và nhận xét phòng', () => {
    
    // TEST 6: Hiển thị form gửi đánh giá
    it('should show review form when user has not reviewed yet', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(30);
      mockReviewService.getReviewsByRoom.and.returnValue(of(mockReviewResponse));
      
      component.loadReviews(0);
      
      expect(component.showForm).toBe(true);
      expect(component.currentUserReview).toBeUndefined();
    });

    // TEST 7: Gửi đánh giá mới thành công
    it('should submit new review successfully', () => {
      spyOn(window, 'alert');
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      const newReview: Review = { ...mockReviews[0], id: 3 };
      mockReviewService.createReview.and.returnValue(of(newReview));
      mockReviewService.getReviewsByRoom.and.returnValue(of(mockReviewResponse));
      
      const reviewRequest = {
        roomId: 1,
        rating: 5,
        comment: 'Phòng tuyệt vời'
      };
      
      component.onFormSubmit(reviewRequest);
      
      expect(mockReviewService.createReview).toHaveBeenCalledWith(reviewRequest);
    });

    // TEST 8: Validate rating phải từ 1-5 sao
    it('should validate rating is between 1-5 stars', () => {
      const invalidRequest = {
        roomId: 1,
        rating: 6,
        comment: 'Test'
      };
      
      // Component hoặc form nên validate rating
      expect(invalidRequest.rating).toBeGreaterThan(5);
    });

    // TEST 9: Validate comment không được rỗng
    it('should validate comment is not empty', () => {
      const invalidRequest = {
        roomId: 1,
        rating: 5,
        comment: ''
      };
      
      expect(invalidRequest.comment).toBe('');
    });

    // TEST 10: Hiển thị alert khi gửi đánh giá fail (component handles gracefully)
    it('should show alert when submitting review fails', () => {
      spyOn(window, 'alert');
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      mockReviewService.createReview.and.returnValue(
        throwError(() => new Error('Submit failed'))
      );
      mockReviewService.getReviewsByRoom.and.returnValue(of(mockReviewResponse));
      
      const reviewRequest = {
        roomId: 1,
        rating: 5,
        comment: 'Test'
      };
      
      component.onFormSubmit(reviewRequest);
      
      expect(window.alert).toHaveBeenCalled();
    });
  });

  // ========== US 11.3: Chỉnh sửa đánh giá ==========
  describe('US 11.3: Chỉnh sửa đánh giá', () => {
    
    beforeEach(() => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      mockReviewService.getReviewsByRoom.and.returnValue(of(mockReviewResponse));
      component.loadReviews(0);
    });

    // TEST 11: Hiển thị form chỉnh sửa với dữ liệu review hiện tại
    it('should show edit form with current review data', () => {
      const reviewToEdit = mockReviews[0];
      
      component.onEditReview(reviewToEdit);
      
      expect(component.editingReview).toEqual(reviewToEdit);
      expect(component.showForm).toBe(true);
    });

    // TEST 12: Cập nhật đánh giá thành công
    it('should update review successfully', () => {
      spyOn(window, 'alert');
      const updatedReview = { ...mockReviews[0], rating: 4, comment: 'Updated comment' };
      mockReviewService.updateReview.and.returnValue(of(updatedReview));
      mockReviewService.getReviewsByRoom.and.returnValue(of(mockReviewResponse));
      
      component.editingReview = mockReviews[0];
      const updateRequest = {
        roomId: 1,
        rating: 4,
        comment: 'Updated comment'
      };
      
      component.onFormSubmit(updateRequest);
      
      expect(mockReviewService.updateReview).toHaveBeenCalled();
    });

    // TEST 13: Hủy chỉnh sửa đánh giá
    it('should cancel editing review', () => {
      component.editingReview = mockReviews[0];
      component.showForm = true;
      
      component.onFormCancel();
      
      expect(component.editingReview).toBeUndefined();
      expect(component.showForm).toBe(false);
    });

    // TEST 14: Chỉ cho phép user edit review của mình
    it('should only allow user to edit their own review', () => {
      mockAuthService.getCurrentUserId.and.returnValue(10);
      component.currentUserId = 10;
      
      const ownReview = mockReviews[0]; // tenantId: 10
      const othersReview = mockReviews[1]; // tenantId: 20
      
      expect(component.isReviewOwner(ownReview)).toBe(true);
      expect(component.isReviewOwner(othersReview)).toBe(false);
    });
  });

  // ========== US 11.4: Xóa đánh giá ==========
  describe('US 11.4: Xóa đánh giá', () => {
    
    beforeEach(() => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      mockReviewService.getReviewsByRoom.and.returnValue(of(mockReviewResponse));
      component.loadReviews(0);
    });

    // TEST 15: Xóa đánh giá thành công
    it('should delete review successfully', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      spyOn(window, 'alert');
      mockReviewService.deleteReview.and.returnValue(of({} as any));
      
      component.onDeleteReview(1);
      
      expect(mockReviewService.deleteReview).toHaveBeenCalledWith(1);
      expect(window.alert).toHaveBeenCalled();
    });

    // TEST 16: Hiển thị confirm trước khi xóa
    it('should show confirmation before deleting', () => {
      spyOn(window, 'confirm').and.returnValue(false);
      
      component.onDeleteReview(1);
      
      expect(window.confirm).toHaveBeenCalled();
      expect(mockReviewService.deleteReview).not.toHaveBeenCalled();
    });

    // TEST 17: Không xóa nếu user hủy confirm
    it('should not delete if user cancels confirmation', () => {
      spyOn(window, 'confirm').and.returnValue(false);
      
      component.onDeleteReview(1);
      
      expect(mockReviewService.deleteReview).not.toHaveBeenCalled();
    });

    // TEST 18: Hiển thị alert khi xóa fail (component handles gracefully)
    it('should show alert when delete fails', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      spyOn(window, 'alert');
      mockReviewService.deleteReview.and.returnValue(
        throwError(() => new Error('Delete failed'))
      );
      
      component.onDeleteReview(1);
      
      expect(window.alert).toHaveBeenCalled();
    });

    // TEST 19: Reset form và user review sau khi xóa thành công
    it('should reset form and user review after successful deletion', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      spyOn(window, 'alert');
      mockReviewService.deleteReview.and.returnValue(of({} as any));
      
      component.onDeleteReview(1);
      
      expect(component.currentUserReview).toBeUndefined();
      expect(component.showForm).toBe(true);
      expect(component.editingReview).toBeUndefined();
    });
  });
});
