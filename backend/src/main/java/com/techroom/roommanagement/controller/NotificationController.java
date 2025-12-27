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
import org.springframework.security.access.prepost.PreAuthorize;
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
            @RequestParam(defaultValue = "5") int size) {

        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không tìm thấy thông tin xác thực.");
        }

        Page<Notification> pageResult = notificationService.getMyNotificationsPaged(userDetails.getId(), page, size);

        // Chuyển sang format Angular cần
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("number", pageResult.getNumber());

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

        Notification updated =notificationService.markAsRead(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

}
