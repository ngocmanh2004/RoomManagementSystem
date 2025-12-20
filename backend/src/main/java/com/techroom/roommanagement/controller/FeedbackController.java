package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.CreateFeedbackDTO;
import com.techroom.roommanagement.model.*;
import com.techroom.roommanagement.repository.FeedbackRepository;
import com.techroom.roommanagement.repository.TenantRepository;
import com.techroom.roommanagement.security.CustomUserDetails;
import com.techroom.roommanagement.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;     // ĐÃ SỬA: đúng import
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final TenantRepository tenantRepository;
    private final FeedbackRepository feedbackRepository;

    @PostMapping
    public Feedback create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CreateFeedbackDTO dto) {
        return feedbackService.create(userDetails.getId(), dto);
    }

    @GetMapping("/my")
    public List<Feedback> getMyFeedback(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Tenant tenant = tenantRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách thuê"));

        return feedbackRepository
                .findByTenantIdOrderByCreatedAtDesc(tenant.getId());
    }

    @GetMapping("/landlord")
    public Page<Feedback> getForLandlord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable p) {

        return feedbackRepository.findByLandlordUserId(userDetails.getId(), p);
    }

    @PutMapping("/{id}/process")
    public Feedback process(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return feedbackService.startProcessing(id, userDetails.getId());
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Feedback> resolve(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, String> body) {

        Feedback updated = feedbackService.resolve(
                id,
                userDetails.getId(),
                body.getOrDefault("note", "")
        );
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/tenant-confirm")
    public Feedback tenantConfirm(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return feedbackService.tenantConfirm(id, userDetails.getId());
    }

    @PutMapping("/{id}/tenant-reject")
    public Feedback tenantReject(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, String> body) {

        return feedbackService.tenantReject(
                id,
                userDetails.getId(),
                body.getOrDefault("feedback", "")
        );
    }
    @PutMapping("/{id}/cancel")
    public Feedback cancel(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return feedbackService.cancel(id, userDetails.getId());
    }
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        feedbackService.delete(id, userDetails.getId());
    }

}
