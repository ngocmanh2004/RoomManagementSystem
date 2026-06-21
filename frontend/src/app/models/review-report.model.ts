export type ReviewReportStatus = 'PENDING' | 'PROCESSING' | 'RESOLVED';

export interface ReviewReport {
  id: number;
  reviewId: number;
  reporterId: number;
  reporterName: string;
  reportedUserId: number;
  reportedUserName: string;
  reason: 'SPAM' | 'OFFENSIVE' | 'FALSE' | 'OTHER';
  description?: string;
  createdAt: string;
  status: ReviewReportStatus;
  note?: string;

  // Thêm các trường dưới đây để FE nhận được từ BE
  reviewContent?: string;
  reviewRating?: number;
  reviewRoomId?: number;
  reviewRoomName?: string;
}