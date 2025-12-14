package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.SendNotificationRequest;
import com.techroom.roommanagement.dto.SendNotificationResponse;
import com.techroom.roommanagement.model.Notification;
import com.techroom.roommanagement.security.CustomUserDetails;
import com.techroom.roommanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /*@PostMapping("/send")
    public ResponseEntity<Map<String, Object>> send(@RequestBody SendNotificationRequest req) {
        try {
            Map<String, Object> result = notificationService.send(req);
            boolean success = (Boolean) result.get("success");
            if (success) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }*/
    // Sửa return type của endpoint /send:
    @PostMapping("/send")
    public ResponseEntity<SendNotificationResponse> send(@RequestBody SendNotificationRequest req) {
        try {
            SendNotificationResponse result = notificationService.send(req);

            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                // Trường hợp service trả về lỗi logic (success=false)
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            // Trường hợp lỗi ngoại lệ (ví dụ: IllegalArgumentException)
            SendNotificationResponse errorResponse = new SendNotificationResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    @GetMapping("/my")
    public List<Notification> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Cần đảm bảo userDetails không null khi được gọi.
        if (userDetails == null) {
            // Xử lý lỗi nếu không có thông tin xác thực
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy thông tin xác thực.");
        }

        return notificationService.getMyNotifications(userDetails.getId());
    }
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.markAsRead(id, userDetails.getId().longValue());
        return ResponseEntity.ok().build();
    }
}
