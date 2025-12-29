USE roommanagement_db;

-- ============================================================
-- CẬP NHẬT ĐẦY ĐỦ NOTIFICATION TYPES
-- ============================================================

ALTER TABLE notifications 
MODIFY COLUMN type ENUM(
    -- SYSTEM
    'SYSTEM',                    -- Thông báo hệ thống chung
    
    -- LANDLORD REQUEST (Đăng ký chủ trọ)
    'LANDLORD_REQUEST',          -- User gửi yêu cầu đăng ký chủ trọ (gửi cho ADMIN)
    'LANDLORD_APPROVED',         -- Yêu cầu được chấp nhận (gửi cho USER)
    'LANDLORD_REJECTED',         -- Yêu cầu bị từ chối (gửi cho USER)
    'LANDLORD_REVOKED',          -- Quyền chủ trọ bị thu hồi (gửi cho LANDLORD)
    
    -- BOOKING (Đặt phòng)
    'BOOKING_CREATED',           -- Khách đặt phòng mới (gửi cho LANDLORD)
    'BOOKING_APPROVED',          -- Booking được chấp nhận (gửi cho TENANT)
    'BOOKING_REJECTED',          -- Booking bị từ chối (gửi cho TENANT)
    'BOOKING_CANCELLED',         -- Booking bị hủy (gửi cho cả 2 bên)
    
    -- CONTRACT (Hợp đồng)
    'CONTRACT_CREATED',          -- Hợp đồng mới được tạo (gửi cho TENANT)
    'CONTRACT_PENDING',          -- Hợp đồng chờ duyệt (gửi cho LANDLORD)
    'CONTRACT_APPROVED',         -- Hợp đồng được duyệt (gửi cho TENANT)
    'CONTRACT_REJECTED',         -- Hợp đồng bị từ chối (gửi cho TENANT)
    'CONTRACT_ACTIVE',           -- Hợp đồng đã kích hoạt (gửi cho cả 2)
    'CONTRACT_EXPIRED',          -- Hợp đồng sắp/đã hết hạn (gửi cho cả 2)
    'CONTRACT_CANCELLED',        -- Hợp đồng bị hủy (gửi cho cả 2)
    'CONTRACT_RENEWED',          -- Hợp đồng được gia hạn (gửi cho cả 2)
    
    -- INVOICE & PAYMENT (Hóa đơn & Thanh toán)
    'INVOICE_CREATED',           -- Hóa đơn mới được tạo (gửi cho TENANT)
    'INVOICE_REMINDER',          -- Nhắc nhở thanh toán hóa đơn (gửi cho TENANT)
    'PAYMENT_PENDING',           -- Thanh toán đang chờ xác nhận (gửi cho LANDLORD)
    'PAYMENT_RECEIVED',          -- Thanh toán đã được xác nhận (gửi cho TENANT)
    'PAYMENT_OVERDUE',           -- Thanh toán quá hạn (gửi cho TENANT)
    'PAYMENT_REJECTED',          -- Thanh toán bị từ chối (gửi cho TENANT)
    
    -- UTILITY (Tiện ích - Điện/Nước)
    'UTILITY_SUBMITTED',         -- Tenant gửi chỉ số điện/nước (gửi cho LANDLORD)
    'UTILITY_REQUEST',           -- Landlord yêu cầu cập nhật chỉ số (gửi cho TENANT)
    'UTILITY_CONFIRMED',         -- Chỉ số được xác nhận (gửi cho TENANT)
    'UTILITY_REJECTED',          -- Chỉ số bị từ chối (gửi cho TENANT)
    
    -- FEEDBACK (Phản hồi)
    'FEEDBACK_CREATED',          -- Phản hồi mới (gửi cho LANDLORD)
    'FEEDBACK_PROCESSING',       -- Đang xử lý phản hồi (gửi cho TENANT)
    'FEEDBACK_RESOLVED',         -- Đã xử lý xong (gửi cho TENANT)
    'FEEDBACK_REJECTED',         -- Phản hồi bị từ chối (gửi cho TENANT)
    'FEEDBACK_CANCELLED',        -- Phản hồi bị hủy (gửi cho cả 2)
    
    -- REVIEW (Đánh giá)
    'REVIEW_POSTED',             -- Đánh giá mới (gửi cho LANDLORD)
    'REVIEW_REPORTED',           -- Đánh giá bị báo cáo (gửi cho ADMIN)
    'REVIEW_REPORT_RESOLVED',   -- Báo cáo được xử lý (gửi cho REPORTER)
    'REVIEW_DELETED',            -- Đánh giá bị xóa (gửi cho TENANT viết review)
    
    -- ROOM (Phòng trọ)
    'ROOM_STATUS_CHANGED',       -- Trạng thái phòng thay đổi (gửi cho TENANT nếu đang ở)
    'ROOM_MAINTENANCE',          -- Phòng cần bảo trì (gửi cho TENANT)
    'ROOM_AVAILABLE',            -- Phòng đã sẵn sàng cho thuê (gửi cho người booking)
    
    -- USER (Người dùng)
    'USER_BANNED',               -- Tài khoản bị khóa (gửi cho USER)
    'USER_UNBANNED',             -- Tài khoản được mở khóa (gửi cho USER)
    'USER_ROLE_CHANGED',         -- Vai trò thay đổi (gửi cho USER)
    
    -- MAINTENANCE (Bảo trì)
    'MAINTENANCE_SCHEDULED',     -- Lịch bảo trì được đặt (gửi cho TENANT)
    'MAINTENANCE_COMPLETED',     -- Bảo trì hoàn thành (gửi cho TENANT)
    'MAINTENANCE_CANCELLED'      -- Bảo trì bị hủy (gửi cho TENANT)
    
) DEFAULT 'SYSTEM';
