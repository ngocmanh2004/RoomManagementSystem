// ============================================================
// NOTIFICATION MODELS - FRONTEND
// ============================================================

export interface Notification {
  id: number;
  userId: number;
  title: string;
  message: string;
  type: NotificationType;
  status: 'DRAFT' | 'SENT'; 
  isRead: boolean;
  createdAt: string;
  loading?: boolean; // UI state
}

// ============================================================
// NOTIFICATION TYPES - Đồng bộ với Backend Enum
// ============================================================
export type NotificationType = 
  // SYSTEM
  | 'SYSTEM'
  
  // LANDLORD REQUEST
  | 'LANDLORD_REQUEST'
  | 'LANDLORD_APPROVED'
  | 'LANDLORD_REJECTED'
  | 'LANDLORD_REVOKED'
  
  // BOOKING
  | 'BOOKING_CREATED'
  | 'BOOKING_APPROVED'
  | 'BOOKING_REJECTED'
  | 'BOOKING_CANCELLED'
  
  // CONTRACT
  | 'CONTRACT_CREATED'
  | 'CONTRACT_PENDING'
  | 'CONTRACT_APPROVED'
  | 'CONTRACT_REJECTED'
  | 'CONTRACT_ACTIVE'
  | 'CONTRACT_EXPIRED'
  | 'CONTRACT_CANCELLED'
  | 'CONTRACT_RENEWED'
  
  // INVOICE & PAYMENT
  | 'INVOICE_CREATED'
  | 'INVOICE_REMINDER'
  | 'PAYMENT_PENDING'
  | 'PAYMENT_RECEIVED'
  | 'PAYMENT_OVERDUE'
  | 'PAYMENT_REJECTED'
  
  // UTILITY
  | 'UTILITY_SUBMITTED'
  | 'UTILITY_REQUEST'
  | 'UTILITY_CONFIRMED'
  | 'UTILITY_REJECTED'
  
  // FEEDBACK
  | 'FEEDBACK_CREATED'
  | 'FEEDBACK_PROCESSING'
  | 'FEEDBACK_RESOLVED'
  | 'FEEDBACK_REJECTED'
  | 'FEEDBACK_CANCELLED'
  
  // REVIEW
  | 'REVIEW_POSTED'
  | 'REVIEW_REPORTED'
  | 'REVIEW_REPORT_RESOLVED'
  | 'REVIEW_DELETED'
  
  // ROOM
  | 'ROOM_STATUS_CHANGED'
  | 'ROOM_MAINTENANCE'
  | 'ROOM_AVAILABLE'
  
  // USER
  | 'USER_BANNED'
  | 'USER_UNBANNED'
  | 'USER_ROLE_CHANGED'
  
  // MAINTENANCE
  | 'MAINTENANCE_SCHEDULED'
  | 'MAINTENANCE_COMPLETED'
  | 'MAINTENANCE_CANCELLED';

// ============================================================
// SEND NOTIFICATION REQUEST (Landlord gửi cho Tenant)
// ============================================================
export interface SendNotificationRequest {
  title: string;
  message: string;
  sendTo: 'ALL' | 'ALL_TENANTS' | 'USERS' | 'ROOMS';
  userIds?: number[];
  roomIds?: number[];
  sendEmail?: boolean;
}

export interface SendNotificationResponse {
  success: boolean;
  message: string;
  sentToCount?: number;
}

// ============================================================
// NOTIFICATION HELPER FUNCTIONS
// ============================================================

/**
 * Lấy icon cho notification type
 */
export function getNotificationIcon(type: NotificationType): string {
  const iconMap: Record<NotificationType, string> = {
    SYSTEM: '🔔',
    
    LANDLORD_REQUEST: '📝',
    LANDLORD_APPROVED: '✅',
    LANDLORD_REJECTED: '❌',
    LANDLORD_REVOKED: '🚫',
    
    BOOKING_CREATED: '🏠',
    BOOKING_APPROVED: '✅',
    BOOKING_REJECTED: '❌',
    BOOKING_CANCELLED: '🚫',
    
    CONTRACT_CREATED: '📄',
    CONTRACT_PENDING: '⏳',
    CONTRACT_APPROVED: '✅',
    CONTRACT_REJECTED: '❌',
    CONTRACT_ACTIVE: '✅',
    CONTRACT_EXPIRED: '⏰',
    CONTRACT_CANCELLED: '🚫',
    CONTRACT_RENEWED: '🔄',
    
    INVOICE_CREATED: '💰',
    INVOICE_REMINDER: '⏰',
    PAYMENT_PENDING: '⏳',
    PAYMENT_RECEIVED: '✅',
    PAYMENT_OVERDUE: '⚠️',
    PAYMENT_REJECTED: '❌',
    
    UTILITY_SUBMITTED: '📊',
    UTILITY_REQUEST: '⚡',
    UTILITY_CONFIRMED: '✅',
    UTILITY_REJECTED: '❌',
    
    FEEDBACK_CREATED: '💬',
    FEEDBACK_PROCESSING: '⏳',
    FEEDBACK_RESOLVED: '✅',
    FEEDBACK_REJECTED: '❌',
    FEEDBACK_CANCELLED: '🚫',
    
    REVIEW_POSTED: '⭐',
    REVIEW_REPORTED: '🚨',
    REVIEW_REPORT_RESOLVED: '✅',
    REVIEW_DELETED: '🗑️',
    
    ROOM_STATUS_CHANGED: '🔄',
    ROOM_MAINTENANCE: '🔧',
    ROOM_AVAILABLE: '✅',
    
    USER_BANNED: '🔒',
    USER_UNBANNED: '🔓',
    USER_ROLE_CHANGED: '👤',
    
    MAINTENANCE_SCHEDULED: '📅',
    MAINTENANCE_COMPLETED: '✅',
    MAINTENANCE_CANCELLED: '🚫'
  };
  
  return iconMap[type] || '🔔';
}

/**
 * Lấy màu cho notification type
 */
export function getNotificationColor(type: NotificationType): string {
  // Quan trọng - Đỏ
  if (['PAYMENT_OVERDUE', 'CONTRACT_EXPIRED', 'USER_BANNED', 'LANDLORD_REJECTED', 
       'BOOKING_REJECTED', 'CONTRACT_REJECTED'].includes(type)) {
    return 'red';
  }
  
  // Cần action - Vàng
  if (['CONTRACT_PENDING', 'PAYMENT_PENDING', 'UTILITY_SUBMITTED', 'BOOKING_CREATED',
       'FEEDBACK_CREATED', 'REVIEW_REPORTED', 'LANDLORD_REQUEST'].includes(type)) {
    return 'orange';
  }
  
  // Thành công - Xanh lá
  if (type.includes('APPROVED') || type.includes('CONFIRMED') || type.includes('RECEIVED') ||
      type.includes('COMPLETED') || type.includes('RESOLVED') || type.includes('ACTIVE')) {
    return 'green';
  }
  
  // Thông tin - Xanh dương
  return 'blue';
}

/**
 * Check xem notification có quan trọng không
 */
export function isImportantNotification(type: NotificationType): boolean {
  return ['PAYMENT_OVERDUE', 'CONTRACT_EXPIRED', 'USER_BANNED', 'LANDLORD_REJECTED',
          'BOOKING_REJECTED', 'CONTRACT_REJECTED'].includes(type);
}

/**
 * Check xem notification có cần action không
 */
export function requiresAction(type: NotificationType): boolean {
  return ['CONTRACT_PENDING', 'PAYMENT_PENDING', 'UTILITY_SUBMITTED', 'BOOKING_CREATED',
          'FEEDBACK_CREATED', 'REVIEW_REPORTED', 'LANDLORD_REQUEST'].includes(type);
}