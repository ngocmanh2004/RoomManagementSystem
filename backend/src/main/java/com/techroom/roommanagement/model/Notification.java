package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_is_read", columnList = "is_read"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_user_read", columnList = "user_id,is_read")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "sender_id")
    private Integer senderId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    // ==========================
    // TRẠNG THÁI GỬI
    // ==========================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status; // DRAFT | SENT

    // ==========================
    // CÁCH GỬI
    // ==========================
    @Column(length = 20)
    private String sendTo; // ROOMS | ALL_TENANTS

    @Column(columnDefinition = "JSON")
    private String roomIds; // "[1,2,3]"

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
        if (type == null) {
            type = NotificationType.SYSTEM;
        }
        if (status == null) status = NotificationStatus.DRAFT;
    }

    /**
     * Helper method để check xem notification có phải là quan trọng không
     */
    public boolean isImportant() {
        return type == NotificationType.PAYMENT_OVERDUE
                || type == NotificationType.CONTRACT_EXPIRED
                || type == NotificationType.USER_BANNED
                || type == NotificationType.LANDLORD_REJECTED;
    }

    /**
     * Helper method để check xem notification có cần action không
     */
    public boolean requiresAction() {
        return type == NotificationType.CONTRACT_PENDING
                || type == NotificationType.PAYMENT_PENDING
                || type == NotificationType.UTILITY_SUBMITTED
                || type == NotificationType.BOOKING_CREATED
                || type == NotificationType.FEEDBACK_CREATED;
    }
}