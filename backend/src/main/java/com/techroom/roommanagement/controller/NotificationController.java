package com.techroom.roommanagement.controller;

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

    @PostMapping("/send")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<SendNotificationResponse> send(
            @RequestBody SendNotificationRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }

        SendNotificationResponse result = notificationService.send(req);
        return ResponseEntity.ok(result);
    }

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null || !userDetails.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập");
        }

        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

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

    @PreAuthorize("hasRole('LANDLORD')")
    @PostMapping("/draft")
    public ResponseEntity<Notification> saveDraft(
            @RequestBody SendNotificationRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws Exception {

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Notification notification = Notification.builder()
                .senderId(userDetails.getId())
                .userId(userDetails.getId())
                .title(req.getTitle())
                .message(req.getMessage())
                .type(NotificationType.SYSTEM)
                .status(NotificationStatus.DRAFT)
                .sendTo(req.getSendTo())
                .roomIds(objectMapper.writeValueAsString(req.getRoomIds()))
                .build();

        return ResponseEntity.ok(notificationRepository.save(notification));
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
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Notification old = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Notification resend = Notification.builder()
                .senderId(userDetails.getId())
                .userId(old.getUserId())
                .title(old.getTitle())
                .message(old.getMessage())
                .type(old.getType())
                .sendTo(old.getSendTo())
                .roomIds(old.getRoomIds())
                .status(NotificationStatus.SENT)
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(resend);

        return ResponseEntity.ok().build();
    }

}