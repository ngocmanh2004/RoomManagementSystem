package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.SendNotificationRequest;
import com.techroom.roommanagement.dto.SendNotificationResponse;
import com.techroom.roommanagement.model.Notification;
import com.techroom.roommanagement.security.CustomUserDetails;
import com.techroom.roommanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

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
    @GetMapping("/my/paged")
    public ResponseEntity<?> getMyNotificationsPaged(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy thông tin xác thực.");
        }

        Page<Notification> pageResult = notificationService.getMyNotificationsPaged(userDetails.getId(), page, size);

        // Chuyển sang format Angular cần
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalElements", pageResult.getTotalElements());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.markAsRead(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
