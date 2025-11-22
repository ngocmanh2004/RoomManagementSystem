package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.ApiResponse;
import com.techroom.roommanagement.model.Landlord;
import com.techroom.roommanagement.model.LandlordRequest;
import com.techroom.roommanagement.service.LandlordService;
import com.techroom.roommanagement.service.LandlordRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/landlords")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLandlordController {

    @Autowired
    private LandlordService landlordService;

    @Autowired
    private LandlordRequestService landlordRequestService;

    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse<List<LandlordRequest>>> getPendingRequests() {
        List<LandlordRequest> list = landlordRequestService.getPendingRequests();
        return ResponseEntity.ok(ApiResponse.success("Loaded pending requests", list));
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<LandlordRequest>>> getAllRequests() {
        List<LandlordRequest> list = landlordRequestService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success("Loaded all requests", list));
    }

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<ApiResponse<LandlordRequest>> getRequestDetailById(@PathVariable int requestId) {
        return landlordRequestService.getById(requestId)
                .map(req -> ResponseEntity.ok(ApiResponse.success("Loaded request", req)))
                .orElse(ResponseEntity.ok(ApiResponse.error("Request not found")));
    }

    @GetMapping("/requests/statistics")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getRequestStatistics() {
        Map<String, Long> stats = landlordRequestService.getStatistics();
        return ResponseEntity.ok(ApiResponse.success("Loaded request statistics", stats));
    }

    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveRequest(@PathVariable int requestId) {
        try {
            landlordRequestService.approveRequest(requestId);

            // optional audit logic omitted

            return ResponseEntity.ok(ApiResponse.success("Đã duyệt yêu cầu thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/requests/{requestId}/reject")
        public ResponseEntity<ApiResponse<Void>> rejectRequest(
            @PathVariable int requestId,
            @RequestBody Map<String, String> body
        ) {
        try {
            String reason = body.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Vui lòng nhập lý do từ chối"));
            }

            landlordRequestService.rejectRequest(requestId, reason);

            return ResponseEntity.ok(ApiResponse.success("Đã từ chối yêu cầu"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<Landlord>>> getApprovedLandlords() {
        List<Landlord> approvedList = landlordService.getApprovedLandlords();
        return ResponseEntity.ok(ApiResponse.success("Loaded approved landlords", approvedList));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getStatistics() {
        Map<String, Long> stats = landlordService.getStatistics();
        return ResponseEntity.ok(ApiResponse.success("Loaded stats", stats));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Landlord>>> getAllLandlords() {
        List<Landlord> landlords = landlordService.getAllLandlords();
        return ResponseEntity.ok(ApiResponse.success("Loaded landlords", landlords));
    }

    @GetMapping("/{landlordId}")
    public ResponseEntity<ApiResponse<Landlord>> getLandlordDetail(@PathVariable int landlordId) {
        return landlordService.getLandlordById(landlordId)
                .map(landlord -> ResponseEntity.ok(ApiResponse.success("Loaded landlord", landlord)))
                .orElse(ResponseEntity.ok(ApiResponse.error("Landlord not found")));
    }
}