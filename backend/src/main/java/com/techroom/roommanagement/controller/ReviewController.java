package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.ReviewDTO;
import com.techroom.roommanagement.dto.ReviewRequestDTO;
import com.techroom.roommanagement.dto.ApiResponse;       
import com.techroom.roommanagement.dto.ErrorResponse;      
import com.techroom.roommanagement.exception.BadRequestException;    
import com.techroom.roommanagement.exception.ForbiddenException;     
import com.techroom.roommanagement.exception.NotFoundException;     
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.UserRepository;
import com.techroom.roommanagement.security.JwtTokenProvider;
import com.techroom.roommanagement.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    /**
     * ✅ Extract ID user từ JWT Token trong Authorization header
     */
    private Integer getCurrentUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authorization.substring(7);
            String username = jwtTokenProvider.extractUsername(token);

            User user = userRepository.findByUsername(username)
                    .orElse(null);

            if (user != null) {
                logger.debug("✅ Current user: {} (ID: {})", username, user.getId());
                return user.getId();
            }
        } catch (Exception e) {
            logger.warn("Cannot extract userId from token: " + e.getMessage());
        }
        return null;
    }

    /**
     * US 11.1: Lấy danh sách đánh giá của phòng - KHÔNG CẦN AUTH
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<?> getReviewsByRoom(
            @PathVariable Integer roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            // ✅ Optional: lấy currentUserId nếu có token (để check canEdit, canDelete)
            Integer currentUserId = getCurrentUserId(authorization);

            Page<ReviewDTO> reviews = reviewService.getReviewsByRoom(roomId, pageable, currentUserId);

            logger.info("Fetched {} reviews for room {}", reviews.getContent().size(), roomId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            logger.error("Error fetching reviews: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * US 11.2: Tạo đánh giá mới - CẦN ĐĂNG NHẬP
     */
    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestBody ReviewRequestDTO request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            // ✅ Extract currentUserId từ token
            Integer currentUserId = getCurrentUserId(authorization);

            if (currentUserId == null) {
                logger.warn("Unauthorized review creation attempt");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Vui lòng đăng nhập để đánh giá");
            }

            ReviewDTO review = reviewService.createReview(request, currentUserId);

            logger.info("✅ Review created by user {}", currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (Exception e) {
            logger.error("Error creating review: ", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * US 11.3: Chỉnh sửa đánh giá - CẦN ĐĂNG NHẬP
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Integer id,
            @RequestBody ReviewRequestDTO request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            // ✅ Extract currentUserId từ token
            Integer currentUserId = getCurrentUserId(authorization);

            if (currentUserId == null) {
                logger.warn("Unauthorized review update attempt");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Vui lòng đăng nhập để chỉnh sửa");
            }

            ReviewDTO review = reviewService.updateReview(id, request, currentUserId);

            logger.info("✅ Review {} updated by user {}", id, currentUserId);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            logger.error("Error updating review: ", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * US 11.4: Xóa đánh giá - CẦN ĐĂNG NHẬP
     * ✅ FIXED: Use Authorization header instead of HttpServletRequest
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            System.out.println("🗑️ ReviewController: Delete review: " + id);

            // ✅ Extract userId from Authorization header
            Integer userId = getCurrentUserId(authorization);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Bạn phải đăng nhập"));
            }

            reviewService.deleteReview(id, userId);

            System.out.println("✅ ReviewController: Review " + id + " deleted by user " + userId);

            // ✅ Return proper response
            return ResponseEntity.ok(new ApiResponse<>(
                    "success",
                    "Xóa đánh giá thành công",
                    null
            ));
        } catch (NotFoundException e) {
            System.out.println("❌ ReviewController: Review not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (ForbiddenException e) {
            System.out.println("❌ ReviewController: Forbidden: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.out.println("❌ ReviewController: Error deleting review: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi xóa đánh giá: " + e.getMessage()));
        }
    }
}