package com.techroom.roommanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.security.auth.UserPrincipal;
import com.techroom.roommanagement.dto.SendNotificationRequest;
import com.techroom.roommanagement.dto.SendNotificationResponse;
import com.techroom.roommanagement.model.Notification;
import com.techroom.roommanagement.model.NotificationStatus;
import com.techroom.roommanagement.model.NotificationType;
import com.techroom.roommanagement.repository.NotificationRepository;
import com.techroom.roommanagement.security.CustomUserDetails;
import com.techroom.roommanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.techroom.roommanagement.security.CustomUserDetails;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    /**
     * Gửi notification hàng loạt (Landlord gửi cho tenant)
     */
    @PostMapping("/send")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<SendNotificationResponse> send(
            @RequestBody SendNotificationRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }

        SendNotificationResponse result =
                notificationService.send(req, userDetails.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Notification>> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        // 🔥 CHỈ SENT → DRAFT TUYỆT ĐỐI KHÔNG LÊN CHUÔNG
        return ResponseEntity.ok(
                notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(
                        userDetails.getId(),
                        NotificationStatus.SENT
                )
        );
    }

    /**
     * Lấy notifications của user hiện tại - PHÂN TRANG
     * Dùng cho trang chi tiết notifications
     */
    @GetMapping("/my/paged")
    public ResponseEntity<?> getMyNotificationsPaged(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy thông tin xác thực.");
        }

        Page<Notification> pageResult = notificationService.getMyNotificationsPaged(
                userDetails.getId(), page, size
        );

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("number", pageResult.getNumber());

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy TẤT CẢ notifications của user (không phân trang)
     * Dùng cho dropdown trên header
     */
    public List<Notification> getNotificationsByUserId(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return notificationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        NotificationStatus.SENT
                );
    }

    /**
     * Đếm số notifications CHƯA ĐỌC
     * Dùng để hiển thị badge số đỏ trên chuông
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null || !userDetails.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập");
        }

        long count = notificationService.getUnreadCount(userId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Đánh dấu 1 notification đã đọc
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Notification updated = notificationService.markAsRead(id, userDetails.getId());
        return ResponseEntity.ok(updated);
    }

    /**
     * Đánh dấu TẤT CẢ notifications đã đọc
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null || !userDetails.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập");
        }

        notificationService.markAllAsRead(userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã đánh dấu tất cả thông báo là đã đọc");
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        notificationService.deleteNotification(id, userDetails.getId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã xóa thông báo");
        return ResponseEntity.ok(response);
    }

    /*@PreAuthorize("hasRole('LANDLORD')")
    @PostMapping("/draft")
    public ResponseEntity<Notification> saveDraft(
            @RequestBody SendNotificationRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws Exception {

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Notification notification = Notification.builder()
                .senderId(userDetails.getId())   // 🔥 BẮT BUỘC
                .userId(userDetails.getId())     // để thỏa NOT NULL
                .title(req.getTitle())
                .message(req.getMessage())
                .type(NotificationType.SYSTEM)   // 🔥 ép SYSTEM
                .status(NotificationStatus.DRAFT)
                .sendTo(req.getSendTo())
                .roomIds(objectMapper.writeValueAsString(req.getRoomIds()))
                .build();

        return ResponseEntity.ok(notificationRepository.save(notification));
    }*/
    @PostMapping("/draft")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Notification> saveDraft(
            @RequestBody SendNotificationRequest req,
            @AuthenticationPrincipal CustomUserDetails user
    ) throws JsonProcessingException {
        return ResponseEntity.ok(
                notificationService.saveDraft(req, user.getId())
        );
    }

    @PreAuthorize("hasRole('LANDLORD')")
    @PostMapping("/{id}/send")
    public ResponseEntity<?> sendDraft(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // check quyền: chỉ chủ trọ tạo mới được gửi
        if (!notification.getSenderId().equals(userDetails.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('LANDLORD')")
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getDetail(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // chỉ cho xem nếu là người tạo hoặc người nhận
        if (
                notification.getSenderId() != null &&
                        !notification.getSenderId().equals(userDetails.getId()) &&
                        !notification.getUserId().equals(userDetails.getId())
        ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(notification);
    }

    @PreAuthorize("hasRole('LANDLORD')")
    @PostMapping("/{id}/resend")
    public ResponseEntity<?> resend(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Notification old = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 1️⃣ GỬI LẠI CHO TENANT
        Notification resendTenant = new Notification();
        resendTenant.setUserId(old.getUserId());
        resendTenant.setSenderId(user.getId());
        resendTenant.setTitle(old.getTitle());
        resendTenant.setMessage(old.getMessage());
        resendTenant.setType(NotificationType.SYSTEM);
        resendTenant.setStatus(NotificationStatus.SENT);
        resendTenant.setIsRead(false);
        resendTenant.setCreatedAt(LocalDateTime.now());
        resendTenant.setSentAt(LocalDateTime.now());

        notificationRepository.save(resendTenant);

        // 2️⃣ HISTORY MỚI (GIỮ NGUYÊN DỮ LIỆU)
        Notification history = new Notification();
        history.setUserId(user.getId());
        history.setSenderId(user.getId());
        history.setTitle(old.getTitle());
        history.setMessage(old.getMessage());     // ✅ GIỮ MESSAGE
        history.setType(NotificationType.SYSTEM);
        history.setStatus(NotificationStatus.SENT);
        history.setSendTo("HISTORY");
        history.setRoomIds(old.getRoomIds());
        history.setIsRead(false);
        history.setCreatedAt(LocalDateTime.now());
        history.setSentAt(LocalDateTime.now());

        notificationRepository.save(history);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('LANDLORD')")
    @GetMapping("/history")
    public ResponseEntity<List<Notification>> getSendHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        List<Notification> history =
                notificationRepository.findByUserIdAndSendToOrderByCreatedAtDesc(
                        userDetails.getId(),
                        "HISTORY"
                );

        return ResponseEntity.ok(history);
    }

}