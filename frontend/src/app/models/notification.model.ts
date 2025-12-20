export interface SendNotificationRequest {
  title: string;
  message: string;
  sendTo: 'ALL' | 'ALL_TENANTS' | 'USERS' | 'ROOMS';  // ép kiểu cho dễ dùng
  userIds?: number[];
  roomIds?: number[];
  sendEmail?: boolean;
}

// Response từ backend sau khi sửa
export interface SendNotificationResponse {
  success: boolean;
  message: string;
  sentToCount?: number;
}
export interface Notification {
    id: number;     
    title: string;
    message: string;
    isRead: boolean; 
    createdAt: string;   
    type?: string;
    loading?: boolean; 
}