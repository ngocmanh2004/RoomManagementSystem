package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.BookingDTO;
import com.techroom.roommanagement.dto.ApiResponse;
import com.techroom.roommanagement.dto.PageResponseDTO;
import com.techroom.roommanagement.exception.NotFoundException;
import com.techroom.roommanagement.model.Contract;
import com.techroom.roommanagement.model.ContractStatus;
import com.techroom.roommanagement.model.Tenant;
import com.techroom.roommanagement.service.BookingService;
import com.techroom.roommanagement.security.CustomUserDetails;
import com.techroom.roommanagement.repository.TenantRepository;
import com.techroom.roommanagement.repository.ContractRepository;
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
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ContractRepository contractRepository;

    private Integer getCurrentUserId() {
        System.out.println("========== GET CURRENT USER ID ==========");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + auth);

        if (auth != null) {
            System.out.println("Is Authenticated: " + auth.isAuthenticated());
            System.out.println("Principal type: " + auth.getPrincipal().getClass().getName());
            System.out.println("Principal: " + auth.getPrincipal());

            Object principal = auth.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                Integer userId = userDetails.getId();
                System.out.println(" User ID extracted: " + userId);
                return userId;
            } else {
                System.out.println(" Principal is NOT CustomUserDetails");
            }
        } else {
            System.out.println(" Authentication is NULL");
        }

        System.out.println("========== RETURN NULL ==========");
        return null;
    }

    /**
     * Lấy hợp đồng đang hoạt động của tenant
     */
    @GetMapping("/my-contract")
    public ResponseEntity<?> getMyActiveContract() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized", null));
        }

        try {
            Tenant tenant = tenantRepository.findByUserId(userId)
                    .orElseThrow(() -> new NotFoundException("Thông tin người thuê không tồn tại"));

            Optional<Contract> contract = contractRepository
                    .findFirstByTenantIdAndStatusInOrderByCreatedAtDesc(
                            tenant.getId(),
                            Arrays.asList(ContractStatus.ACTIVE, ContractStatus.APPROVED)
                    );

            if (contract.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "Bạn chưa có hợp đồng thuê phòng", null));
            }

            return ResponseEntity.ok(new ApiResponse(true, "Lấy hợp đồng thành công", contract.get()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Tạo yêu cầu đặt phòng mới
     */
    @PostMapping
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingDTO bookingDTO,
            BindingResult bindingResult
    ) {
        System.out.println("========== CREATE BOOKING ENDPOINT ==========");
        System.out.println("Request body: " + bookingDTO);

        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            System.out.println("Validation error: " + error);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, error, null));
        }

        Integer userId = getCurrentUserId();
        System.out.println("User ID from getCurrentUserId(): " + userId);

        if (userId == null) {
            System.out.println(" User ID is NULL - Returning 401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Vui lòng đăng nhập", null));
        }

        try {
            System.out.println(" Calling bookingService.createBooking()...");
            Contract contract = bookingService.createBooking(bookingDTO, userId);
            System.out.println(" Contract created: " + contract.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Tạo yêu cầu đặt phòng thành công", contract));
        } catch (Exception e) {
            System.out.println(" Error creating booking: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Lấy danh sách tất cả hợp đồng của tenant (có phân trang)
     */
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

            // Chuyển đổi sang PageResponseDTO
            PageResponseDTO<Contract> pageResponse = PageResponseDTO.of(contracts);

            return ResponseEntity.ok(new ApiResponse(true, "Lấy danh sách hợp đồng thành công", pageResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Lấy chi tiết một hợp đồng cụ thể
     */
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