package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.SendNotificationRequest;
import com.techroom.roommanagement.dto.SendNotificationResponse;
import com.techroom.roommanagement.model.*;
import com.techroom.roommanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;

    /**
     * Tạo notification đơn cho 1 user
     * Dùng trong các trường hợp: Admin duyệt/từ chối, feedback, booking...
     */
    public Notification createNotification(
            Integer userId,
            String title,
            String message,
            NotificationType type
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("userId không được null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy user với id: " + userId
                ));

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * Gửi notification cho tất cả ADMIN
     * Dùng khi: User đăng ký chủ trọ, có booking mới, feedback mới...
     */
    public void notifyAllAdmins(String title, String message, NotificationType type) {
        List<User> admins = userRepository.findByRole(0); // role 0 = ADMIN

        for (User admin : admins) {
            createNotification(admin.getId(), title, message, type);
        }
    }

    /**
     * Gửi notification hàng loạt (cho landlord gửi nhiều tenant)
     */
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

        // Xử lý gửi theo PHÒNG
        if ("ROOMS".equalsIgnoreCase(req.getSendTo()) && req.getRoomIds() != null && !req.getRoomIds().isEmpty()) {
            for (Integer roomId : req.getRoomIds()) {
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
        // Gửi cho TẤT CẢ TENANT
        else if ("ALL".equalsIgnoreCase(req.getSendTo()) || "ALL_TENANTS".equalsIgnoreCase(req.getSendTo())) {
            userRepository.findByRole(2).forEach(u -> recipientUserIds.add(u.getId()));
        }
        // Gửi cho USERS cụ thể
        else if ("USERS".equalsIgnoreCase(req.getSendTo()) && req.getUserIds() != null) {
            recipientUserIds.addAll(req.getUserIds());
        }

        SendNotificationResponse response = new SendNotificationResponse();

        if (recipientUserIds.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Không có người nhận hợp lệ.");
            return response;
        }

        List<Notification> saved = new ArrayList<>();

        try {
            List<User> targetUsers = userRepository.findAllById(recipientUserIds);

            if (targetUsers.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Không tìm thấy người dùng hợp lệ trong CSDL.");
                return response;
            }

            for (User user : targetUsers) {
                Notification n = new Notification();
                n.setUserId(user.getId());
                n.setTitle(req.getTitle());
                n.setMessage(req.getMessage());
                n.setType(NotificationType.SYSTEM);
                n.setIsRead(false);
                n.setCreatedAt(LocalDateTime.now());

                Notification s = notificationRepository.save(n);

                if (s.getId() == null) {
                    throw new RuntimeException("Lỗi lưu notification vào CSDL.");
                }
                saved.add(s);
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Gửi thông báo thất bại: " + e.getMessage());
            return response;
        }

        response.setSuccess(true);
        response.setMessage("Gửi thông báo thành công đến " + saved.size() + " khách.");
        response.setSentToCount(saved.size());
        return response;
    }

    /**
     * Lấy notifications theo user - PHÂN TRANG
     */
    public Page<Notification> getMyNotificationsPaged(Integer userId, int page, int size) {
        if (userId == null) {
            return Page.empty();
        }
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(
                userId,
                PageRequest.of(page, size)
        );
    }

    /**
     * Lấy tất cả notifications của user (không phân trang) - Dùng cho dropdown
     */
    public List<Notification> getNotificationsByUserId(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Đếm số notifications CHƯA ĐỌC
     */
    public long getUnreadCount(Integer userId) {
        if (userId == null) {
            return 0;
        }
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    /**
     * Đánh dấu 1 notification đã đọc
     */
    @Transactional
    public Notification markAsRead(Integer id, Integer userId) {
        Notification n = notificationRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy thông báo"
                ));

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

    /**
     * Đánh dấu TẤT CẢ notifications của user đã đọc
     */
    @Transactional
    public void markAllAsRead(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId không được null");
        }

        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndIsRead(userId, false);

        for (Notification n : unreadNotifications) {
            n.setIsRead(true);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Xóa notification
     */
    @Transactional
    public void deleteNotification(Integer id, Integer userId) {
        Notification n = notificationRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy thông báo"
                ));

        if (!n.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền xóa thông báo này"
            );
        }

        notificationRepository.delete(n);
    }
}