package com.techroom.roommanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service để gửi notification cho user
 * Có thể mở rộng để tích hợp với WebSocket, Email, SMS, etc
 */
@Service
@Transactional
public class NotificationService {
    
    /**
     * Gửi notification cho user
     * @param userId ID của user nhận notification
     * @param title Tiêu đề
     * @param message Nội dung
     * @param type Loại notification (BOOKING_CREATED, BOOKING_REQUEST, CONTRACT_APPROVED, etc)
     */
    public void sendNotification(Integer userId, String title, String message, String type) {
        if (userId == null) {
            System.out.println("⚠️ [NotificationService] userId is null, skipping notification");
            return;
        }
        
        System.out.println("📢 [NotificationService] Sending notification:");
        System.out.println("   → User ID: " + userId);
        System.out.println("   → Title: " + title);
        System.out.println("   → Message: " + message);
        System.out.println("   → Type: " + type);
        System.out.println("   → Time: " + LocalDateTime.now());
        
        // TODO: Implement later with actual notification system
        // - Save to database (Notification table)
        // - Send WebSocket message to user
        // - Send Email/SMS
        // - Push notification to mobile app
    }
}
