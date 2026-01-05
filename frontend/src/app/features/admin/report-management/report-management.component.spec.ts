import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { ReportManagementComponent } from './report-management.component';
import { ReviewService } from '../../../services/review.service';
import { UserService } from '../../../services/user.service';
import { ReviewReport, ReviewReportStatus } from '../../../models/review-report.model';

describe('ReportManagementComponent', () => {
  let component: ReportManagementComponent;
  let fixture: ComponentFixture<ReportManagementComponent>;
  let mockReviewService: jasmine.SpyObj<ReviewService>;
  let mockUserService: jasmine.SpyObj<UserService>;

  const mockReports: ReviewReport[] = [
    {
      id: 1,
      reviewId: 101,
      reviewContent: 'Phòng tệ quá',
      reviewRating: 1,
      reporterId: 5,
      reporterName: 'Nguyễn Văn A',
      reportedUserId: 10,
      reportedUserName: 'Trần Thị B',
      reason: 'SPAM',
      status: 'PENDING' as ReviewReportStatus,
      createdAt: '2024-01-01T10:00:00',
      reviewRoomName: 'Phòng 101'
    },
    {
      id: 2,
      reviewId: 102,
      reviewContent: 'Chủ trọ xấu tính',
      reviewRating: 2,
      reporterId: 6,
      reporterName: 'Lê Văn C',
      reportedUserId: 20,
      reportedUserName: 'Phạm Văn D',
      reason: 'OFFENSIVE',
      status: 'PROCESSING' as ReviewReportStatus,
      createdAt: '2024-01-02T11:00:00',
      note: 'Đang xem xét',
      reviewRoomName: 'Phòng 202'
    }
  ];

  beforeEach(async () => {
    mockReviewService = jasmine.createSpyObj('ReviewService', [
      'getReviewReports',
      'updateReviewReport',
      'deleteReview',
      'updateReview'
    ]);
    mockUserService = jasmine.createSpyObj('UserService', ['updateStatus']);

    await TestBed.configureTestingModule({
      imports: [ReportManagementComponent, ReactiveFormsModule, FormsModule],
      providers: [
        { provide: ReviewService, useValue: mockReviewService },
        { provide: UserService, useValue: mockUserService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReportManagementComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('US 14.1: Xem danh sách báo cáo vi phạm', () => {
    it('should load reports on init', () => {
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      
      component.ngOnInit();
      
      expect(mockReviewService.getReviewReports).toHaveBeenCalled();
      expect(component.reports.length).toBe(2);
      expect(component.filteredReports.length).toBe(2);
      expect(component.isLoading).toBe(false);
    });

    it('should handle reports with Page structure', () => {
      const pageResponse: any = { content: mockReports, totalElements: 2 };
      mockReviewService.getReviewReports.and.returnValue(of(pageResponse));
      
      component.loadReports();
      
      expect(component.reports.length).toBe(2);
      expect(component.reports).toEqual(mockReports);
    });

    it('should display error message when loading fails', () => {
      mockReviewService.getReviewReports.and.returnValue(
        throwError(() => new Error('Network error'))
      );
      
      component.loadReports();
      
      expect(component.errorMessage).toBe('Lỗi tải báo cáo!');
    });

    it('should filter reports by status', () => {
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      component.ngOnInit();
      
      component.selectedStatus = 'PENDING';
      component.applyFilters();
      
      expect(component.filteredReports.length).toBe(1);
    });
  });

  describe('US 14.2: Xử lý báo cáo vi phạm', () => {
    beforeEach(() => {
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      component.ngOnInit();
    });

    it('should open detail modal with selected report', () => {
      const report = mockReports[0];
      
      component.openDetailModal(report);
      
      expect(component.selectedReport).toBe(report);
      expect(component.isDetailModalOpen).toBe(true);
      expect(component.processForm.value.status).toBe('PROCESSING');
    });

    it('should close detail modal and reset selected report', () => {
      component.selectedReport = mockReports[0];
      component.isDetailModalOpen = true;
      
      component.closeDetailModal();
      
      expect(component.isDetailModalOpen).toBe(false);
      expect(component.selectedReport).toBeNull();
    });

    it('should process report successfully', () => {
      spyOn(window, 'alert');
      mockReviewService.updateReviewReport.and.returnValue(of({}));
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      
      component.selectedReport = mockReports[0];
      component.processForm.patchValue({
        status: 'RESOLVED',
        note: 'Đã xử lý'
      });
      
      component.processReport();
      
      expect(mockReviewService.updateReviewReport).toHaveBeenCalledWith(
        1,
        { status: 'RESOLVED', note: 'Đã xử lý' }
      );
      expect(window.alert).toHaveBeenCalledWith('Báo cáo đã được xử lý thành công!');
    });
  });

  describe('US 11.5: Báo cáo đánh giá sai phạm - Admin Actions', () => {
    beforeEach(() => {
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      component.ngOnInit();
    });

    it('should lock user account successfully', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      spyOn(window, 'alert');
      mockUserService.updateStatus.and.returnValue(of({}));
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      
      component.selectedReport = mockReports[0];
      component.lockAccount(10);
      
      expect(window.confirm).toHaveBeenCalledWith(
        'Khóa tài khoản này? Người dùng sẽ bị đăng xuất ngay lập tức.'
      );
      expect(mockUserService.updateStatus).toHaveBeenCalledWith(10, 'BANNED');
      expect(window.alert).toHaveBeenCalledWith('Đã khóa tài khoản thành công!');
    });

    it('should delete review successfully', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      spyOn(window, 'alert');
      const deleteResponse: any = { message: 'Success', data: null };
      mockReviewService.deleteReview.and.returnValue(of(deleteResponse));
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      
      component.selectedReport = mockReports[0];
      component.deleteReview(101);
      
      expect(window.confirm).toHaveBeenCalledWith('Xóa đánh giá này?');
      expect(mockReviewService.deleteReview).toHaveBeenCalledWith(101);
      expect(window.alert).toHaveBeenCalledWith('Đánh giá đã bị xóa!');
    });

    it('should update review successfully', () => {
      spyOn(window, 'alert');
      const mockReview: any = { id: 101, roomId: 1, rating: 3, comment: 'Updated comment' };
      mockReviewService.updateReview.and.returnValue(of(mockReview));
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      
      component.editReviewId = 101;
      component.editReviewForm.patchValue({
        rating: 3,
        comment: 'Updated comment'
      });
      
      component.submitEditReview();
      
      expect(mockReviewService.updateReview).toHaveBeenCalledWith(
        101,
        jasmine.objectContaining({ rating: 3, comment: 'Updated comment' })
      );
      expect(window.alert).toHaveBeenCalledWith('Đã cập nhật đánh giá thành công!');
    });
  });

  describe('Helper Methods', () => {
    it('should return correct reason label', () => {
      expect(component.getReasonLabel('SPAM')).toBe('Spam');
      expect(component.getReasonLabel('OFFENSIVE')).toBe('Ngôn từ không phù hợp');
      expect(component.getReasonLabel('FALSE')).toBe('Sai sự thật');
      expect(component.getReasonLabel('OTHER')).toBe('Khác');
    });
  });
});
