package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.BookingDTO;
import com.techroom.roommanagement.dto.ApiResponse;
import com.techroom.roommanagement.model.Contract;
import com.techroom.roommanagement.service.BookingService;
import com.techroom.roommanagement.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal).getId();
            }
        }
        return null;
    }
    
    @PostMapping
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingDTO bookingDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, error, null));
        }
        
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Vui lòng đăng nhập", null));
        }
        
        try {
            Contract contract = bookingService.createBooking(bookingDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Tạo yêu cầu đặt phòng thành công", contract));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/my-contracts")
    public ResponseEntity<?> getMyContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized", null));
        }
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Contract> contracts = bookingService.getTenantContracts(userId, pageable);
            return ResponseEntity.ok(new ApiResponse(true, "Lấy danh sách hợp đồng thành công", contracts));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getContractById(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized", null));
        }
        
        try {
            Contract contract = bookingService.getTenantContractById(id, userId);
            return ResponseEntity.ok(new ApiResponse(true, "Lấy chi tiết hợp đồng thành công", contract));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
