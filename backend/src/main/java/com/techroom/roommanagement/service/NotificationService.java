package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.SendNotificationRequest;
import com.techroom.roommanagement.model.*;
import com.techroom.roommanagement.repository.ContractRepository;
import com.techroom.roommanagement.repository.NotificationRepository;
import com.techroom.roommanagement.repository.TenantRepository;
import com.techroom.roommanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import com.techroom.roommanagement.dto.SendNotificationResponse;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;



/**
 * Service để gửi notification cho user
 * Có thể mở rộng để tích hợp với WebSocket, Email, SMS, etc
 */
@Service
@Transactional
@RequiredArgsConstructor
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
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public SendNotificationResponse send(SendNotificationRequest req) {

        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề không được để trống");
        }
        if (req.getMessage() == null || req.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung không được để trống");
        }

        Set<Integer> recipientUserIds = new HashSet<>();
        List<String> emptyRooms = new ArrayList<>();

        // === XỬ LÝ GỬI THEO PHÒNG ===
        if ("ROOMS".equalsIgnoreCase(req.getSendTo()) && req.getRoomIds() != null && !req.getRoomIds().isEmpty()) {

            for (Integer roomId : req.getRoomIds()) {
                // Giả định ContractStatus.ACTIVE là enum/class hợp lệ
                List<Contract> contracts = contractRepository.findByRoomIdAndStatus(roomId, ContractStatus.ACTIVE);

                if (contracts.isEmpty()) {
                    emptyRooms.add("Phòng " + roomId);
                    continue;
                }

                contracts.forEach(c -> {
                    if (c.getTenant() != null && c.getTenant().getUser() != null) {
                        recipientUserIds.add(c.getTenant().getUser().getId());
                    }
                });
            }
        }
        // Các case khác giữ nguyên (ALL, USERS, ALL_TENANTS)...
        else if ("ALL".equalsIgnoreCase(req.getSendTo()) || "ALL_TENANTS".equalsIgnoreCase(req.getSendTo())) {
            // Giả định role 2 là Khách thuê
            userRepository.findByRole(2).forEach(u -> recipientUserIds.add(u.getId()));
        }
        else if ("USERS".equalsIgnoreCase(req.getSendTo()) && req.getUserIds() != null) {
            recipientUserIds.addAll(req.getUserIds());
        }

        // === KHỞI TẠO RESPONSE ===
        SendNotificationResponse response = new SendNotificationResponse();

        if (recipientUserIds.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Không có người nhận hợp lệ.");
            return response;
        }

        List<Notification> saved = new ArrayList<>();

        try {
            // Lấy danh sách User Entity để thiết lập mối quan hệ
            List<User> targetUsers = userRepository.findAllById(recipientUserIds);

            if (targetUsers.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Không tìm thấy người dùng hợp lệ trong CSDL.");
                return response;
            }

            for (User user : targetUsers) {
                Notification n = new Notification();
                // Giả định Notification Entity đã được sửa để có trường 'user'
                n.setUserId(user.getId());

                n.setTitle(req.getTitle());
                n.setMessage(req.getMessage());
                n.setType(NotificationType.SYSTEM); // Hoặc dùng type khác nếu có
                n.setIsRead(false);

                Notification s = notificationRepository.save(n);

                if (s.getId() == null) {
                    throw new RuntimeException("Lỗi lưu notification vào CSDL.");
                }
                saved.add(s);
            }

            // 📢 LẮP ĐẶT LOGIC GỬI EMAIL (NẾU CÓ)
            if (req.isSendEmail()) {
                // TODO: Thực hiện gửi email cho user.getEmail()
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Gửi thông báo thất bại: " + e.getMessage());
            return response;
        }

        // --- Trường hợp thành công ---
        response.setSuccess(true);
        response.setMessage("Gửi thông báo thành công đến " + saved.size() + " khách.");
        response.setSentToCount(saved.size());
        return response;

    }
    public Page<Notification> getMyNotificationsPaged(Integer userId, int page, int size) {
        if (userId == null) {
            return Page.empty();
        }
        return notificationRepository.findByUserId(userId, PageRequest.of(page, size));
    }
    public Notification markAsRead(Integer id, Integer userId) {
        Notification n = notificationRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy thông báo"
                        )
                );

        if (!n.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền thao tác thông báo này"
            );
        }

        if (!n.getIsRead()) {
            n.setIsRead(true);
            notificationRepository.save(n);
        }

        return n;
    }


}
