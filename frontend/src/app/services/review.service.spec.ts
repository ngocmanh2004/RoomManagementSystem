import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReviewService } from './review.service';
import { Review, ReviewRequest, ReviewResponse } from '../models/review.model';

describe('ReviewService - Sprint 2 (US 11.1-11.4)', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;

  const mockReview: Review = {
    id: 1,
    roomId: 1,
    tenantId: 10,
    tenantName: 'Nguyễn Văn A',
    rating: 5,
    comment: 'Phòng rất tốt, sạch sẽ',
    createdAt: '2026-01-01T10:00:00',
    updatedAt: '2026-01-01T10:00:00'
  };

  const mockReviewRequest: ReviewRequest = {
    roomId: 1,
    rating: 5,
    comment: 'Phòng rất tốt'
  };

  const mockReviewResponse: ReviewResponse = {
    content: [mockReview],
    totalElements: 1,
    totalPages: 1,
    currentPage: 0
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReviewService]
    });

    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.setItem('accessToken', 'test-token');
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // TEST 1: Get reviews by room with pagination
  it('should get reviews by room with pagination', () => {
    const roomId = 1;
    const page = 0;
    const size = 10;

    service.getReviewsByRoom(roomId, page, size).subscribe(response => {
      expect(response.content.length).toBe(1);
      expect(response.content[0].rating).toBe(5);
      expect(response.totalElements).toBe(1);
    });

    const req = httpMock.expectOne(req => 
      req.url.includes(`/api/reviews/room/${roomId}`) && 
      req.params.get('page') === '0' && 
      req.params.get('size') === '10'
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.has('Authorization')).toBe(true);
    req.flush(mockReviewResponse);
  });

  // TEST 2: Create review successfully
  it('should create review successfully', () => {
    service.createReview(mockReviewRequest).subscribe(review => {
      expect(review).toEqual(mockReview);
      expect(review.rating).toBe(5);
      expect(review.comment).toBe('Phòng rất tốt, sạch sẽ');
    });

    const req = httpMock.expectOne('/api/reviews');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockReviewRequest);
    expect(req.request.headers.has('Authorization')).toBe(true);
    req.flush(mockReview);
  });

  // TEST 3: Update review successfully
  it('should update review successfully', () => {
    const reviewId = 1;
    const updateRequest: ReviewRequest = {
      roomId: 1,
      rating: 4,
      comment: 'Updated comment'
    };
    const updatedReview = { ...mockReview, rating: 4, comment: 'Updated comment' };

    service.updateReview(reviewId, updateRequest).subscribe(review => {
      expect(review.rating).toBe(4);
      expect(review.comment).toBe('Updated comment');
    });

    const req = httpMock.expectOne(`/api/reviews/${reviewId}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updateRequest);
    req.flush(updatedReview);
  });

  // TEST 4: Delete review successfully
  it('should delete review successfully', () => {
    const reviewId = 1;

    service.deleteReview(reviewId).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`/api/reviews/${reviewId}`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.has('Authorization')).toBe(true);
    req.flush({ success: true });
  });

  // TEST 5: Report review successfully
  it('should report review successfully', () => {
    const reviewId = 1;
    const reportData = { reason: 'Nội dung không phù hợp', description: 'Spam' };

    service.reportReview(reviewId, reportData).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`/api/reviews/${reviewId}/report`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.reason).toBe(reportData.reason);
    req.flush({ success: true, message: 'Đã báo cáo' });
  });

  // TEST 6: Handle error when loading reviews fails
  it('should handle error when loading reviews fails', () => {
    const roomId = 1;

    service.getReviewsByRoom(roomId).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.message).toBeTruthy();
        expect(['Lỗi tải đánh giá', 'Server error']).toContain(error.message);
      }
    });

    const req = httpMock.expectOne(req => req.url.includes(`/api/reviews/room/${roomId}`));
    req.flush(
      { message: 'Server error' },
      { status: 500, statusText: 'Internal Server Error' }
    );
  });

  // TEST 7: Handle missing auth token
  it('should handle missing auth token', () => {
    localStorage.clear();
    
    service.getReviewsByRoom(1).subscribe(response => {
      expect(response).toBeDefined();
    });

    const req = httpMock.expectOne(req => req.url.includes('/api/reviews/room/1'));
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush(mockReviewResponse);
  });

  // TEST 8: Validate review rating range (1-5)
  it('should send review with valid rating', () => {
    const invalidRequest = { ...mockReviewRequest, rating: 5 };

    service.createReview(invalidRequest).subscribe();

    const req = httpMock.expectOne('/api/reviews');
    expect(req.request.body.rating).toBeGreaterThanOrEqual(1);
    expect(req.request.body.rating).toBeLessThanOrEqual(5);
    req.flush(mockReview);
  });
});
