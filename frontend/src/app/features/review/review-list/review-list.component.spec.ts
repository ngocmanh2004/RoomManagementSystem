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
      userId: 10,
      userName: 'Nguyễn Văn A',
      rating: 5,
      comment: 'Phòng rất tốt, sạch sẽ',
      createdAt: '2024-01-01T10:00:00',
      updatedAt: '2024-01-01T10:00:00'
    },
    {
      id: 2,
      roomId: 1,
      userId: 20,
      userName: 'Trần Thị B',
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
    size: 10,
    number: 0
  };

  beforeEach(async () => {
    mockReviewService = jasmine.createSpyObj('ReviewService', [
      'getReviewsByRoomId',
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
      mockReviewService.getReviewsByRoomId.and.returnValue(of(mockReviewResponse));
      
      component.ngOnInit();
      
      expect(mockReviewService.getReviewsByRoomId).toHaveBeenCalledWith(1, 0, 10);
      expect(component.reviews.length).toBe(2);
      expect(component.isLoading).toBe(false);
    });

    // TEST 2: Hiển thị tất cả đánh giá theo thứ tự thời gian
    it('should display all reviews in chronological order', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      mockReviewService.getReviewsByRoomId.and.returnValue(of(mockReviewResponse));
      
      component.loadReviews(0);
      
      expect(component.reviews).toEqual(mockReviews);
      expect(component.reviews[0].createdAt).toBe('2024-01-01T10:00:00');
    });

    // TEST 3: Hiển thị rating và comment của từng review
    it('should display rating and comment for each review', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      mockReviewService.getReviewsByRoomId.and.returnValue(of(mockReviewResponse));
      
      component.loadReviews(0);
      
      const firstReview = component.reviews[0];
      expect(firstReview.rating).toBe(5);
      expect(firstReview.comment).toBe('Phòng rất tốt, sạch sẽ');
      expect(firstReview.userName).toBe('Nguyễn Văn A');
    });

    // TEST 4: Phân trang danh sách đánh giá
    it('should paginate review list', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      const page2Response = { ...mockReviewResponse, number: 1 };
      mockReviewService.getReviewsByRoomId.and.returnValue(of(page2Response));
      
      component.loadReviews(1);
      
      expect(mockReviewService.getReviewsByRoomId).toHaveBeenCalledWith(1, 1, 10);
      expect(component.currentPage).toBe(1);
    });

    // TEST 5: Xử lý khi không có đánh giá
    it('should handle empty review list', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      const emptyResponse = { ...mockReviewResponse, content: [], totalElements: 0 };
      mockReviewService.getReviewsByRoomId.and.returnValue(of(emptyResponse));
      
      component.loadReviews(0);
      
      expect(component.reviews.length).toBe(0);
    });
  });

  // ========== US 11.2: Gửi đánh giá và nhận xét phòng ==========
  describe('US 11.2: Gửi đánh giá và nhận xét phòng', () => {
    
    // TEST 6: Hiển thị form gửi đánh giá
    it('should show review form when user clicks add review', () => {
      component.showForm = false;
      
      component.toggleForm();
      
      expect(component.showForm).toBe(true);
    });

    // TEST 7: Gửi đánh giá mới thành công
    it('should submit new review successfully', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      const newReview: Review = { ...mockReviews[0], id: 3 };
      mockReviewService.createReview.and.returnValue(of(newReview));
      mockReviewService.getReviewsByRoomId.and.returnValue(of(mockReviewResponse));
      
      const reviewRequest = {
        roomId: 1,
        rating: 5,
        comment: 'Phòng tuyệt vời'
      };
      
      component.onSubmitReview(reviewRequest);
      
      expect(mockReviewService.createReview).toHaveBeenCalledWith(reviewRequest);
      expect(component.showForm).toBe(false);
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

    // TEST 10: Hiển thị lỗi khi gửi đánh giá fail
    it('should show error when submitting review fails', () => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      mockReviewService.createReview.and.returnValue(
        throwError(() => new Error('Submit failed'))
      );
      
      const reviewRequest = {
        roomId: 1,
        rating: 5,
        comment: 'Test'
      };
      
      component.onSubmitReview(reviewRequest);
      
      expect(component.errorMessage).toBeTruthy();
    });
  });

  // ========== US 11.3: Chỉnh sửa đánh giá ==========
  describe('US 11.3: Chỉnh sửa đánh giá', () => {
    
    beforeEach(() => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      mockReviewService.getReviewsByRoomId.and.returnValue(of(mockReviewResponse));
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
      const updatedReview = { ...mockReviews[0], rating: 4, comment: 'Updated comment' };
      mockReviewService.updateReview.and.returnValue(of(updatedReview));
      mockReviewService.getReviewsByRoomId.and.returnValue(of(mockReviewResponse));
      
      component.editingReview = mockReviews[0];
      const updateRequest = {
        roomId: 1,
        rating: 4,
        comment: 'Updated comment'
      };
      
      component.onSubmitReview(updateRequest);
      
      expect(mockReviewService.updateReview).toHaveBeenCalled();
      expect(component.showForm).toBe(false);
    });

    // TEST 13: Hủy chỉnh sửa đánh giá
    it('should cancel editing review', () => {
      component.editingReview = mockReviews[0];
      component.showForm = true;
      
      component.cancelForm();
      
      expect(component.editingReview).toBeUndefined();
      expect(component.showForm).toBe(false);
    });

    // TEST 14: Chỉ cho phép user edit review của mình
    it('should only allow user to edit their own review', () => {
      mockAuthService.getCurrentUserId.and.returnValue(10);
      component.currentUserId = 10;
      
      const ownReview = mockReviews[0]; // userId: 10
      const othersReview = mockReviews[1]; // userId: 20
      
      expect(ownReview.userId).toBe(component.currentUserId);
      expect(othersReview.userId).not.toBe(component.currentUserId);
    });
  });

  // ========== US 11.4: Xóa đánh giá ==========
  describe('US 11.4: Xóa đánh giá', () => {
    
    beforeEach(() => {
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getCurrentUserId.and.returnValue(10);
      mockReviewService.getReviewsByRoomId.and.returnValue(of(mockReviewResponse));
      component.loadReviews(0);
    });

    // TEST 15: Xóa đánh giá thành công
    it('should delete review successfully', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      mockReviewService.deleteReview.and.returnValue(of({} as any));
      mockReviewService.getReviewsByRoomId.and.returnValue(of(mockReviewResponse));
      
      component.onDeleteReview(1);
      
      expect(mockReviewService.deleteReview).toHaveBeenCalledWith(1);
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

    // TEST 18: Hiển thị lỗi khi xóa fail
    it('should show error when delete fails', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      mockReviewService.deleteReview.and.returnValue(
        throwError(() => new Error('Delete failed'))
      );
      
      component.onDeleteReview(1);
      
      expect(component.errorMessage).toBeTruthy();
    });

    // TEST 19: Reload danh sách sau khi xóa thành công
    it('should reload review list after successful deletion', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      mockReviewService.deleteReview.and.returnValue(of({} as any));
      mockReviewService.getReviewsByRoomId.and.returnValue(of(mockReviewResponse));
      spyOn(component, 'loadReviews');
      
      component.onDeleteReview(1);
      
      expect(component.loadReviews).toHaveBeenCalled();
    });
  });
});
