package com.techroom.roommanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;


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
    /*@Transactional
    public SendNotificationResponse send(SendNotificationRequest req, Integer senderId){
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

                // người nhận
                n.setUserId(user.getId());

                // 🔥 người gửi (CHỦ TRỌ)
                n.setSenderId(
                        userRepository.findByRole(1).get(0).getId()
                        // hoặc tốt hơn: truyền senderId từ controller
                );

                n.setTitle(req.getTitle());
                n.setMessage(req.getMessage());
                n.setType(NotificationType.SYSTEM);

                n.setStatus(NotificationStatus.SENT);
                n.setSentAt(LocalDateTime.now());

                n.setIsRead(false);
                n.setCreatedAt(LocalDateTime.now());

                notificationRepository.save(n);
                saved.add(n);
            }
            /*Integer landlordId = userRepository.findByRole(1).get(0).getId();

            // 🔔 Notification cho CHỦ TRỌ (lịch sử gửi + chuông)
            Notification historyNoti = new Notification();
            historyNoti.setUserId(landlordId);      // 👈 CHỦ TRỌ
            historyNoti.setSenderId(landlordId);
            historyNoti.setTitle(req.getTitle());
            historyNoti.setMessage(
                    "Bạn đã gửi thông báo đến " + saved.size() + " khách thuê."
            );
            historyNoti.setType(NotificationType.SYSTEM);
            historyNoti.setStatus(NotificationStatus.SENT);
            historyNoti.setIsRead(false);
            historyNoti.setCreatedAt(LocalDateTime.now());
            historyNoti.setSentAt(LocalDateTime.now());

            // 👉 Dùng để phân biệt với noti tenant
            historyNoti.setSendTo("HISTORY");

            notificationRepository.save(historyNoti);*
            // ======================
            // 🔔 LỊCH SỬ GỬI (CHO CHỦ TRỌ)
            // ======================
            Notification historyNoti = new Notification();
            historyNoti.setUserId(senderId);      // 👈 CHỦ TRỌ ĐANG LOGIN
            historyNoti.setSenderId(senderId);
            historyNoti.setTitle(req.getTitle());
            historyNoti.setMessage(
                    "Bạn đã gửi thông báo đến " + saved.size() + " khách thuê."
            );
            historyNoti.setType(NotificationType.SYSTEM);
            historyNoti.setStatus(NotificationStatus.SENT);
            historyNoti.setIsRead(false);
            historyNoti.setCreatedAt(LocalDateTime.now());
            historyNoti.setSentAt(LocalDateTime.now());

            // 🔥 ĐÁNH DẤU HISTORY
            historyNoti.setSendTo("HISTORY");

            notificationRepository.save(historyNoti);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Gửi thông báo thất bại: " + e.getMessage());
            return response;
        }

        response.setSuccess(true);
        response.setMessage("Gửi thông báo thành công đến " + saved.size() + " khách.");
        response.setSentToCount(saved.size());
        return response;
    }*/
    @Transactional
    public SendNotificationResponse send(SendNotificationRequest req, Integer senderId) {

        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new IllegalArgumentException("Tiêu đề không được để trống");
        }
        if (req.getMessage() == null || req.getMessage().isBlank()) {
            throw new IllegalArgumentException("Nội dung không được để trống");
        }

        Set<Integer> recipientUserIds = new HashSet<>();

        // ====== XÁC ĐỊNH NGƯỜI NHẬN ======
        if ("ROOMS".equalsIgnoreCase(req.getSendTo()) && req.getRoomIds() != null) {
            for (Integer roomId : req.getRoomIds()) {
                contractRepository
                        .findByRoomIdAndStatus(roomId, ContractStatus.ACTIVE)
                        .forEach(c -> {
                            if (c.getTenant() != null && c.getTenant().getUser() != null) {
                                recipientUserIds.add(c.getTenant().getUser().getId());
                            }
                        });
            }
        } else if ("ALL_TENANTS".equalsIgnoreCase(req.getSendTo())) {
            userRepository.findByRole(2)
                    .forEach(u -> recipientUserIds.add(u.getId()));
        }

        if (recipientUserIds.isEmpty()) {
            SendNotificationResponse res = new SendNotificationResponse();
            res.setSuccess(false);
            res.setMessage("Không có người nhận hợp lệ");
            res.setSentToCount(recipientUserIds.size());
            return res;
        }

        // ====== TẠO NOTIFICATION CHO TENANT (SENT) ======
        for (Integer userId : recipientUserIds) {
            Notification n = new Notification();
            n.setUserId(userId);                 // người nhận
            n.setSenderId(senderId);             // chủ trọ
            n.setTitle(req.getTitle());
            n.setMessage(req.getMessage());
            n.setType(NotificationType.SYSTEM);

            n.setStatus(NotificationStatus.SENT);      // 🔥 SENT
            n.setSentAt(LocalDateTime.now());          // 🔥 CÓ sentAt

            n.setIsRead(false);
            n.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(n);
        }

        // ====== LỊCH SỬ GỬI CHO CHỦ TRỌ ======
        Notification history = new Notification();
        history.setUserId(senderId);
        history.setSenderId(senderId);
        history.setTitle(req.getTitle());
        history.setMessage("Bạn đã gửi thông báo đến " + recipientUserIds.size() + " khách thuê.");
        history.setType(NotificationType.SYSTEM);
        history.setStatus(NotificationStatus.SENT);
        history.setSendTo("HISTORY");
        history.setIsRead(false);
        history.setCreatedAt(LocalDateTime.now());
        history.setSentAt(LocalDateTime.now());

        notificationRepository.save(history);

        SendNotificationResponse res = new SendNotificationResponse();
        res.setSuccess(true);
        res.setMessage("Gửi thông báo thành công");
        res.setSentToCount(recipientUserIds.size());
        return res;
    }

    @Transactional
    public Notification saveDraft(SendNotificationRequest req, Integer senderId) throws JsonProcessingException {

        Notification draft = new Notification();
        draft.setUserId(senderId);          // chủ trọ
        draft.setSenderId(senderId);

        draft.setTitle(req.getTitle());
        draft.setMessage(req.getMessage());
        draft.setType(NotificationType.SYSTEM);

        draft.setStatus(NotificationStatus.DRAFT); // 🔥 DRAFT
        draft.setSentAt(null);                     // 🔥 KHÔNG CÓ sentAt

        draft.setSendTo(req.getSendTo());
        draft.setRoomIds(
                req.getRoomIds() != null
                        ? new ObjectMapper().writeValueAsString(req.getRoomIds())
                        : null
        );

        draft.setIsRead(true); // draft không cần unread
        draft.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(draft);
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

        // 🔥 CHỈ LẤY NOTIFICATION ĐÃ GỬI
        return notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        NotificationStatus.SENT
                );
    }

    /**
     * Đếm số notifications CHƯA ĐỌC
     */
    public long getUnreadCount(Integer userId) {
        if (userId == null) {
            return 0;
        }
        return notificationRepository
                .countByUserIdAndStatusAndIsReadFalse(
                        userId,
                        NotificationStatus.SENT
                );
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