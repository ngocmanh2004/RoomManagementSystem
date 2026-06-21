package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.*;
import com.techroom.roommanagement.model.Contract;
import com.techroom.roommanagement.model.Landlord;
import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.repository.LandlordRepository;
import com.techroom.roommanagement.repository.RoomRepository;
import com.techroom.roommanagement.security.CustomUserDetails;
import com.techroom.roommanagement.service.BookingService;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/landlord/contracts")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class LandlordController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private LandlordRepository landlordRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getId();
        }
        return null;
    }

    private Integer getCurrentLandlordId() {
        Integer userId = getCurrentUserId();
        if (userId == null) return null;

        return landlordRepository.findByUserId(userId)
                .map(Landlord::getId)
                .orElse(null);
    }

    @GetMapping
    public ResponseEntity<?> getMyContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {
        Integer landlordId = getCurrentLandlordId();
        if (landlordId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized", null));
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Contract> contracts;

            if (status != null && !status.isEmpty()) {
                contracts = bookingService.getLandlordContractsByStatus(landlordId, status, pageable);
            } else {
                contracts = bookingService.getLandlordContracts(landlordId, pageable);
            }

            // Chuyển đổi sang PageResponseDTO
            PageResponseDTO<Contract> pageResponse = PageResponseDTO.of(contracts);

            return ResponseEntity.ok(new ApiResponse(true, "Lấy danh sách hợp đồng thành công", pageResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContractDetail(@PathVariable Integer id) {
        Integer landlordId = getCurrentLandlordId();
        if (landlordId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized", null));
        }

        try {
            Contract contract = bookingService.getLandlordContractById(id, landlordId);
            return ResponseEntity.ok(new ApiResponse(true, "Lấy chi tiết hợp đồng thành công", contract));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveContract(@PathVariable Integer id) {
        Integer landlordId = getCurrentLandlordId();
        if (landlordId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized", null));
        }

        try {
            Contract contract = bookingService.approveContract(id, landlordId);
            return ResponseEntity.ok(new ApiResponse(true, "Duyệt hợp đồng thành công", contract));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectContract(
            @PathVariable Integer id,
            @RequestBody RejectContractRequest request
    ) {
        Integer landlordId = getCurrentLandlordId();
        if (landlordId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized", null));
        }

        try {
            String reason = request.getReason();
            if (reason == null || reason.trim().isEmpty()) {
                reason = "Không đáp ứng yêu cầu";
            }

            Contract contract = bookingService.rejectContract(id, reason, landlordId);
            return ResponseEntity.ok(new ApiResponse(true, "Từ chối hợp đồng thành công", contract));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{id}/terminate")
    public ResponseEntity<?> terminateContract(
            @PathVariable Integer id,
            @Valid @RequestBody TerminateContractRequest request,
            BindingResult bindingResult
    ) {
        Integer landlordId = getCurrentLandlordId();
        if (landlordId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Vui lòng đăng nhập", null));
        }

        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, error, null));
        }

        try {
            Contract contract = bookingService.terminateContract(id, request, landlordId);

            String message;
            if (request.getTerminationType() == TerminateContractRequest.TerminationType.EXPIRED) {
                message = "Đã thanh lý hợp đồng (hết hạn), phòng đã sẵn sàng cho thuê mới";
            } else {
                message = "Đã thanh lý hợp đồng (chấm dứt sớm), phòng đã sẵn sàng cho thuê mới";
            }

            return ResponseEntity.ok(new ApiResponse(true, message, contract));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/direct")
    public ResponseEntity<?> createDirectContract(
            @Valid @RequestBody DirectContractDTO dto,
            BindingResult bindingResult
    ) {
        Integer landlordId = getCurrentLandlordId();
        if (landlordId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Vui lòng đăng nhập", null));
        }

        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, error, null));
        }

        try {
            Contract contract = bookingService.createDirectContract(dto, landlordId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(
                            true,
                            "Tạo hợp đồng trực tiếp thành công. Phòng đã chuyển sang trạng thái đang thuê.",
                            contract
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/rooms/available")
    public ResponseEntity<?> getMyAvailableRooms() {
        Integer landlordId = getCurrentLandlordId();
        if (landlordId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized", null));
        }

        try {
            List<Room> rooms = roomRepository.findByBuildingLandlordIdAndStatus(
                    landlordId,
                    Room.RoomStatus.AVAILABLE
            );

            // Convert to DTO để trả về thông tin cần thiết
            List<RoomDTO> roomDTOs = rooms.stream()
                    .map(RoomDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse(true, "Lấy danh sách phòng thành công", roomDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }


    @PostMapping("/{id}/extend")
    public ResponseEntity<?> extendContract(
            @PathVariable Integer id,
            @Valid @RequestBody ExtendContractRequest request,
            BindingResult bindingResult
    ) {
        Integer landlordId = getCurrentLandlordId();
        if (landlordId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Vui lòng đăng nhập", null));
        }

        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, error, null));
        }

        try {
            Contract contract = bookingService.extendContract(id, request, landlordId);
            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Gia hạn hợp đồng thành công",
                    contract
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

}