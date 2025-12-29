package com.techroom.roommanagement.model;

/**
 * Enum định nghĩa các loại thông báo trong hệ thống
 * Đồng bộ với database enum và frontend
 */
public enum NotificationType {
    // ============================================================
    // SYSTEM
    // ============================================================
    SYSTEM("Hệ thống", "Thông báo hệ thống chung"),

    // ============================================================
    // LANDLORD REQUEST (Đăng ký chủ trọ)
    // ============================================================
    LANDLORD_REQUEST("Đăng ký chủ trọ", "Yêu cầu đăng ký chủ trọ mới"),
    LANDLORD_APPROVED("Chấp nhận", "Yêu cầu đăng ký chủ trọ được chấp nhận"),
    LANDLORD_REJECTED("Từ chối", "Yêu cầu đăng ký chủ trọ bị từ chối"),
    LANDLORD_REVOKED("Thu hồi", "Quyền chủ trọ bị thu hồi"),

    // ============================================================
    // BOOKING (Đặt phòng)
    // ============================================================
    BOOKING_CREATED("Đặt phòng", "Có yêu cầu đặt phòng mới"),
    BOOKING_APPROVED("Chấp nhận", "Đặt phòng được chấp nhận"),
    BOOKING_REJECTED("Từ chối", "Đặt phòng bị từ chối"),
    BOOKING_CANCELLED("Hủy", "Đặt phòng đã bị hủy"),

    // ============================================================
    // CONTRACT (Hợp đồng)
    // ============================================================
    CONTRACT_CREATED("Hợp đồng", "Hợp đồng mới được tạo"),
    CONTRACT_PENDING("Chờ duyệt", "Hợp đồng chờ phê duyệt"),
    CONTRACT_APPROVED("Đã duyệt", "Hợp đồng được phê duyệt"),
    CONTRACT_REJECTED("Từ chối", "Hợp đồng bị từ chối"),
    CONTRACT_ACTIVE("Kích hoạt", "Hợp đồng đã được kích hoạt"),
    CONTRACT_EXPIRED("Hết hạn", "Hợp đồng sắp/đã hết hạn"),
    CONTRACT_CANCELLED("Hủy", "Hợp đồng đã bị hủy"),
    CONTRACT_RENEWED("Gia hạn", "Hợp đồng được gia hạn"),

    // ============================================================
    // INVOICE & PAYMENT (Hóa đơn & Thanh toán)
    // ============================================================
    INVOICE_CREATED("Hóa đơn", "Hóa đơn mới được tạo"),
    INVOICE_REMINDER("Nhắc nhở", "Nhắc nhở thanh toán hóa đơn"),
    PAYMENT_PENDING("Chờ xác nhận", "Thanh toán đang chờ xác nhận"),
    PAYMENT_RECEIVED("Đã thanh toán", "Thanh toán đã được xác nhận"),
    PAYMENT_OVERDUE("Quá hạn", "Thanh toán đã quá hạn"),
    PAYMENT_REJECTED("Từ chối", "Thanh toán bị từ chối"),

    // ============================================================
    // UTILITY (Tiện ích - Điện/Nước)
    // ============================================================
    UTILITY_SUBMITTED("Gửi chỉ số", "Chỉ số điện/nước đã được gửi"),
    UTILITY_REQUEST("Yêu cầu", "Yêu cầu cập nhật chỉ số điện/nước"),
    UTILITY_CONFIRMED("Xác nhận", "Chỉ số điện/nước được xác nhận"),
    UTILITY_REJECTED("Từ chối", "Chỉ số điện/nước bị từ chối"),

    // ============================================================
    // FEEDBACK (Phản hồi)
    // ============================================================
    FEEDBACK_CREATED("Phản hồi", "Phản hồi mới từ khách thuê"),
    FEEDBACK_PROCESSING("Đang xử lý", "Phản hồi đang được xử lý"),
    FEEDBACK_RESOLVED("Đã xử lý", "Phản hồi đã được xử lý"),
    FEEDBACK_REJECTED("Từ chối", "Phản hồi bị từ chối"),
    FEEDBACK_CANCELLED("Hủy", "Phản hồi đã bị hủy"),

    // ============================================================
    // REVIEW (Đánh giá)
    // ============================================================
    REVIEW_POSTED("Đánh giá", "Đánh giá mới về phòng trọ"),
    REVIEW_REPORTED("Báo cáo", "Đánh giá bị báo cáo vi phạm"),
    REVIEW_REPORT_RESOLVED("Xử lý", "Báo cáo đánh giá đã được xử lý"),
    REVIEW_DELETED("Xóa", "Đánh giá đã bị xóa"),

    // ============================================================
    // ROOM (Phòng trọ)
    // ============================================================
    ROOM_STATUS_CHANGED("Thay đổi", "Trạng thái phòng thay đổi"),
    ROOM_MAINTENANCE("Bảo trì", "Phòng cần bảo trì"),
    ROOM_AVAILABLE("Sẵn sàng", "Phòng đã sẵn sàng cho thuê"),

    // ============================================================
    // USER (Người dùng)
    // ============================================================
    USER_BANNED("Khóa tài khoản", "Tài khoản bị khóa"),
    USER_UNBANNED("Mở khóa", "Tài khoản được mở khóa"),
    USER_ROLE_CHANGED("Thay đổi", "Vai trò người dùng thay đổi"),

    // ============================================================
    // MAINTENANCE (Bảo trì)
    // ============================================================
    MAINTENANCE_SCHEDULED("Lên lịch", "Lịch bảo trì được đặt"),
    MAINTENANCE_COMPLETED("Hoàn thành", "Bảo trì đã hoàn thành"),
    MAINTENANCE_CANCELLED("Hủy", "Lịch bảo trì bị hủy");

    private final String displayName;
    private final String description;

    NotificationType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check xem type này có phải là thông báo quan trọng không
     */
    public boolean isImportant() {
        return this == PAYMENT_OVERDUE
                || this == CONTRACT_EXPIRED
                || this == USER_BANNED
                || this == LANDLORD_REJECTED
                || this == BOOKING_REJECTED
                || this == CONTRACT_REJECTED;
    }

    /**
     * Check xem type này có cần action từ user không
     */
    public boolean requiresAction() {
        return this == CONTRACT_PENDING
                || this == PAYMENT_PENDING
                || this == UTILITY_SUBMITTED
                || this == BOOKING_CREATED
                || this == FEEDBACK_CREATED
                || this == REVIEW_REPORTED
                || this == LANDLORD_REQUEST;
    }
}