package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.RegisterRequest;
import com.techroom.roommanagement.model.ReviewReport;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.service.UserService;
import com.techroom.roommanagement.repository.ReviewReportRepository;
import com.techroom.roommanagement.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewReportRepository reviewReportRepository;

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) User.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(userService.getAllUsers(keyword, role, status, page, size));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable int id, @RequestBody Map<String, String> payload) {
        try {
            String statusStr = payload.get("status");
            User.Status status = User.Status.valueOf(statusStr);
            userService.updateUserStatus(id, status);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Xóa tài khoản thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/review-reports")
    public ResponseEntity<?> getReviewReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewReport> pageResult;
        if (status != null) {
            ReviewReport.ReportStatus reportStatus = ReviewReport.ReportStatus.valueOf(status);
            pageResult = reviewReportRepository.findByStatus(reportStatus, pageable);
        } else {
            pageResult = reviewReportRepository.findAll(pageable);
        }

        // Map entity sang DTO để tránh lỗi proxy
        Page<Map<String, Object>> dtoPage = pageResult.map(report -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", report.getId());
            dto.put("reviewId", report.getReview().getId());
            dto.put("reporterId", report.getReporter().getId());
            dto.put("reporterName", report.getReporter().getFullName());
            dto.put("reportedUserId", report.getReview().getTenant().getId());
            dto.put("reportedUserName", report.getReview().getTenant().getFullName());
            dto.put("reason", report.getReason());
            dto.put("description", report.getDescription());
            dto.put("createdAt", report.getCreatedAt());
            dto.put("status", report.getStatus().name());
            dto.put("note", report.getNote());

            // Thêm các trường cho FE
            dto.put("reviewContent", report.getReview().getComment());
            dto.put("reviewRating", report.getReview().getRating());
            // LẤY ID PHÒNG VÀ TÊN PHÒNG
            dto.put("reviewRoomId", report.getReview().getRoom().getId());
            dto.put("reviewRoomName", report.getReview().getRoom().getName());
            return dto;
        });

        return ResponseEntity.ok(dtoPage);
    }

    @PutMapping("/review-reports/{id}")
    public ResponseEntity<?> updateReviewReport(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {

        ReviewReport report = reviewReportRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy báo cáo"));

        // Chuyển status từ String sang Enum
        String statusStr = (String) payload.get("status");
        ReviewReport.ReportStatus status = ReviewReport.ReportStatus.valueOf(statusStr);

        String note = (String) payload.getOrDefault("note", "");

        report.setStatus(status);
        report.setNote(note);

        reviewReportRepository.save(report);

        return ResponseEntity.ok(Map.of("message", "Cập nhật báo cáo thành công"));
    }
}