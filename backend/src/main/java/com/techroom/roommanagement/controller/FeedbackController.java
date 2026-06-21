package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.FeedbackCreateRequest;
import com.techroom.roommanagement.dto.FeedbackProcessRequest;
import com.techroom.roommanagement.dto.FeedbackUpdateRequest;
import com.techroom.roommanagement.dto.TenantConfirmRequest;
import com.techroom.roommanagement.model.Feedback;
import com.techroom.roommanagement.security.CustomUserDetails;
import com.techroom.roommanagement.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /* ================= TENANT ================= */

    // 1️⃣ Tenant gửi phản hồi
    @PostMapping
    public ResponseEntity<Feedback> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody FeedbackCreateRequest request
    ) {
        return ResponseEntity.ok(
                feedbackService.create(user.getId(), request)
        );
    }

    // 2️⃣ Tenant xem phản hồi của mình
    @GetMapping("/my")
    public ResponseEntity<?> myFeedbacks(
            @AuthenticationPrincipal CustomUserDetails user,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                feedbackService.getMyFeedbacks(user.getId(), pageable)
        );
    }

    // 3️⃣ Tenant xác nhận đã xử lý
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Feedback> tenantConfirm(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody TenantConfirmRequest request
    ) {
        return ResponseEntity.ok(
                feedbackService.tenantConfirm(id, user.getId(), request)
        );
    }

    /* ================= LANDLORD ================= */

    // 4️⃣ Chủ trọ xem phản hồi
    @GetMapping("/landlord")
    public ResponseEntity<?> landlordFeedbacks(
            @AuthenticationPrincipal CustomUserDetails user,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                feedbackService.getForLandlord(user.getId(), pageable)
        );
    }

    // 5️⃣ Chủ trọ xử lý (PROCESSING / RESOLVED / CANCELED)
    @PutMapping("/{id}/process")
    public ResponseEntity<Feedback> process(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody FeedbackProcessRequest request
    ) {
        return ResponseEntity.ok(
                feedbackService.process(id, user.getId(), request)
        );
    }

    // 6️⃣ Chủ trọ xoá
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        feedbackService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(
            @PathVariable Integer id,
            @RequestBody FeedbackUpdateRequest request
    ) {
        feedbackService.update(id, request);
        return ResponseEntity.ok().build();
    }

}
