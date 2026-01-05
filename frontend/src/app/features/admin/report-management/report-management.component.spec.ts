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

  // ========== US 14.1: Xem danh sách báo cáo vi phạm ==========
  describe('US 14.1: Xem danh sách báo cáo vi phạm', () => {
    
    // TEST 1: Load danh sách báo cáo khi khởi tạo component
    it('should load reports when component initializes', () => {
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      
      component.ngOnInit();
      
      expect(mockReviewService.getReviewReports).toHaveBeenCalled();
      expect(component.reports.length).toBe(2);
      expect(component.isLoading).toBe(false);
    });

    // TEST 2: Hiển thị thông báo lỗi khi load fail
    it('should show error message when loading reports fails', () => {
      mockReviewService.getReviewReports.and.returnValue(
        throwError(() => new Error('Network error'))
      );
      
      component.loadReports();
      
      expect(component.errorMessage).toBe('Lỗi tải báo cáo!');
      expect(component.isLoading).toBe(false);
    });

    // TEST 3: Lọc báo cáo theo trạng thái (PENDING, PROCESSING, RESOLVED)
    it('should filter reports by status', () => {
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      component.ngOnInit();
      
      component.selectedStatus = 'PENDING';
      component.applyFilters();
      
      expect(component.filteredReports.length).toBe(1);
      expect(component.filteredReports[0].status).toBe('PENDING');
    });

    // TEST 4: Lọc báo cáo theo lý do (SPAM, OFFENSIVE, FALSE, OTHER)
    it('should filter reports by reason', () => {
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      component.ngOnInit();
      
      component.selectedReason = 'SPAM';
      component.applyFilters();
      
      expect(component.filteredReports.length).toBe(1);
      expect(component.filteredReports[0].reason).toBe('SPAM');
    });

    // TEST 5: Hiển thị tất cả báo cáo khi không có filter
    it('should show all reports when no filters applied', () => {
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      component.ngOnInit();
      
      component.selectedStatus = 'ALL';
      component.selectedReason = 'ALL';
      component.applyFilters();
      
      expect(component.filteredReports.length).toBe(2);
    });
  });

  // ========== US 14.2: Xử lý báo cáo vi phạm ==========
  describe('US 14.2: Xử lý báo cáo vi phạm', () => {
    
    beforeEach(() => {
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      component.ngOnInit();
    });

    // TEST 6: Mở modal chi tiết báo cáo
    it('should open detail modal with selected report', () => {
      const report = mockReports[0];
      
      component.openDetailModal(report);
      
      expect(component.selectedReport).toBe(report);
      expect(component.isDetailModalOpen).toBe(true);
    });

    // TEST 7: Cập nhật trạng thái báo cáo thành công
    it('should update report status successfully', () => {
      spyOn(window, 'alert');
      mockReviewService.updateReviewReport.and.returnValue(of({}));
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      
      component.selectedReport = mockReports[0];
      component.processForm.patchValue({
        status: 'RESOLVED',
        note: 'Đã xử lý'
      });
      
      component.processReport();
      
      expect(mockReviewService.updateReviewReport).toHaveBeenCalled();
      expect(window.alert).toHaveBeenCalledWith('Báo cáo đã được xử lý thành công!');
    });

    // TEST 8: Xử lý lỗi khi cập nhật báo cáo
    it('should handle error when updating report fails', () => {
      spyOn(window, 'alert');
      mockReviewService.updateReviewReport.and.returnValue(
        throwError(() => new Error('Update failed'))
      );
      
      component.selectedReport = mockReports[0];
      component.processReport();
      
      expect(window.alert).toHaveBeenCalledWith('Lỗi xử lý báo cáo!');
    });
  });

  // ========== US 11.5: Báo cáo đánh giá sai phạm - Admin Actions ==========
  describe('US 11.5: Báo cáo đánh giá sai phạm', () => {
    
    beforeEach(() => {
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      component.ngOnInit();
    });

    // TEST 9: Khóa tài khoản người dùng vi phạm
    it('should lock user account when admin confirms', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      spyOn(window, 'alert');
      mockUserService.updateStatus.and.returnValue(of({}));
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      
      component.selectedReport = mockReports[0];
      component.lockAccount(10);
      
      expect(mockUserService.updateStatus).toHaveBeenCalledWith(10, 'BANNED');
      expect(window.alert).toHaveBeenCalledWith('Đã khóa tài khoản thành công!');
    });

    // TEST 10: Không khóa tài khoản nếu admin hủy
    it('should not lock account when admin cancels', () => {
      spyOn(window, 'confirm').and.returnValue(false);
      
      component.lockAccount(10);
      
      expect(mockUserService.updateStatus).not.toHaveBeenCalled();
    });

    // TEST 11: Xóa đánh giá vi phạm
    it('should delete review when admin confirms', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      spyOn(window, 'alert');
      const deleteResponse: any = { message: 'Success', data: null };
      mockReviewService.deleteReview.and.returnValue(of(deleteResponse));
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      
      component.selectedReport = mockReports[0];
      component.deleteReview(101);
      
      expect(mockReviewService.deleteReview).toHaveBeenCalledWith(101);
      expect(window.alert).toHaveBeenCalledWith('Đánh giá đã bị xóa!');
    });

    // TEST 12: Sửa nội dung đánh giá vi phạm
    it('should edit review content successfully', () => {
      spyOn(window, 'alert');
      const mockReview: any = { id: 101, roomId: 1, rating: 3, comment: 'Updated' };
      mockReviewService.updateReview.and.returnValue(of(mockReview));
      mockReviewService.getReviewReports.and.returnValue(of(mockReports));
      
      component.editReviewId = 101;
      component.editReviewForm.patchValue({
        rating: 3,
        comment: 'Updated'
      });
      
      component.submitEditReview();
      
      expect(mockReviewService.updateReview).toHaveBeenCalled();
      expect(window.alert).toHaveBeenCalledWith('Đã cập nhật đánh giá thành công!');
    });
  });
});

