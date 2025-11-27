package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.ApiResponse;
import com.techroom.roommanagement.model.LandlordRequest;
import com.techroom.roommanagement.service.LandlordRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/landlord-registration")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class LandlordRegistrationController {

    @Autowired
    private LandlordRequestService landlordRequestService;

    @GetMapping("/status/{userId}")
    @PreAuthorize("hasRole('TENANT') or hasRole('LANDLORD')")
    public ResponseEntity<?> getRegistrationStatus(@PathVariable int userId) {
        Optional<LandlordRequest> request = landlordRequestService.getRequestByUserId(userId);

        if (request.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "registered", false,
                    "status", "NOT_REGISTERED",
                    "message", "Bạn chưa đăng ký làm chủ trọ"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "registered", true,
                "status", request.get().getStatus().name(),
                "request", request.get()
        ));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<?> registerLandlord(
            @RequestParam("userId") int userId,
            @RequestParam("cccd") String cccd,
            @RequestParam("address") String address,
            @RequestParam("expectedRoomCount") Integer expectedRoomCount,
            @RequestParam(value = "provinceCode", required = false) Integer provinceCode,
            @RequestParam(value = "districtCode", required = false) Integer districtCode,
            @RequestParam("frontImage") MultipartFile frontImage,
            @RequestParam("backImage") MultipartFile backImage,
            @RequestParam("businessLicense") MultipartFile businessLicense
    ) {
        try {
            LandlordRequest request = landlordRequestService.createRequest(
                    userId, cccd, address, expectedRoomCount,
                    provinceCode, districtCode,
                    frontImage, backImage, businessLicense
            );

            return ResponseEntity.ok(
                    ApiResponse.success("Gửi yêu cầu thành công. Vui lòng chờ admin duyệt.", request)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("Có lỗi xảy ra khi xử lý yêu cầu: " + e.getMessage())
            );
        }
    }


    @GetMapping("/check/{userId}")
    @PreAuthorize("hasRole('TENANT') or hasRole('LANDLORD')")
    public ResponseEntity<?> checkRegistration(@PathVariable int userId) {
        Optional<LandlordRequest> request = landlordRequestService.getRequestByUserId(userId);

        if (request.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "registered", false,
                    "status", "NOT_REGISTERED"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "registered", true,
                "status", request.get().getStatus().name(),
                "canReapply", request.get().getStatus() == LandlordRequest.Status.REJECTED
        ));
    }

    @DeleteMapping("/cancel/{userId}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<?> cancelRegistration(@PathVariable int userId) {
        try {
            landlordRequestService.cancelRequest(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã hủy đăng ký thành công"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}