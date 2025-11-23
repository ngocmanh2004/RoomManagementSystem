package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.ReviewDTO;
import com.techroom.roommanagement.dto.ReviewRequestDTO;
import com.techroom.roommanagement.exception.BadRequestException;
import com.techroom.roommanagement.exception.ForbiddenException;
import com.techroom.roommanagement.exception.NotFoundException;
import com.techroom.roommanagement.model.Review;
import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.model.ContractStatus;  // ✅ ADD THIS
import com.techroom.roommanagement.repository.ContractRepository;
import com.techroom.roommanagement.repository.ReviewRepository;
import com.techroom.roommanagement.repository.RoomRepository;
import com.techroom.roommanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractRepository contractRepository;

    /**
     * US 11.1: Lấy danh sách đánh giá công khai của phòng
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByRoom(Integer roomId, Pageable pageable, Integer currentUserId) {
        if (!roomRepository.existsById(roomId)) {
            throw new NotFoundException("Phòng không tồn tại");
        }

        Page<Review> reviews = reviewRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);

        List<ReviewDTO> dtoList = reviews.getContent().stream()
                .map(review -> convertToDTO(review, currentUserId))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, reviews.getTotalElements());
    }

    /**
     * US 11.2: Tạo đánh giá mới
     */
    public ReviewDTO createReview(ReviewRequestDTO requestDTO, Integer currentUserId) {
        // ✅ 1. Validate currentUserId
        if (currentUserId == null || currentUserId <= 0) {
            throw new ForbiddenException("Bạn phải đăng nhập để đánh giá");
        }

        // ✅ 2. Validate roomId từ requestDTO
        if (requestDTO.getRoomId() == null || requestDTO.getRoomId() <= 0) {
            throw new BadRequestException("ID phòng không hợp lệ");
        }

        System.out.println("📝 ReviewService: Creating review - userId: " + currentUserId + ", roomId: " + requestDTO.getRoomId());

        // ✅ 3. Lấy Room
        Room room = roomRepository.findById(requestDTO.getRoomId())
                .orElseThrow(() -> new NotFoundException("Phòng không tồn tại"));

        // ✅ 4. Lấy User
        User tenant = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        // ✅ 5. Kiểm tra user có contract với phòng không
        // ✅ NẾU MUỐN SKIP KIỂM TRA CONTRACT CHO TEST, HÃY COMMENT ĐOẠN NÀY
        List<ContractStatus> validStatuses = Arrays.asList(ContractStatus.ACTIVE, ContractStatus.EXPIRED);
        boolean hasValidContract = contractRepository.existsByTenantIdAndRoomIdAndStatusIn(
                currentUserId,
                requestDTO.getRoomId(),
                validStatuses
        );

        System.out.println("🔍 ReviewService: Has valid contract: " + hasValidContract);

        if (!hasValidContract) {
            // ✅ OPTION 1: Throw error (strict mode)
            // throw new ForbiddenException("Bạn chỉ có thể đánh giá phòng đã từng thuê hoặc đang thuê");

            // ✅ OPTION 2: Allow review (test mode - comment out for production)
            System.out.println("⚠️ ReviewService: User không có contract, nhưng vẫn cho phép đánh giá (TEST MODE)");
        }

        // ✅ 6. Kiểm tra không được đánh giá 2 lần
        if (reviewRepository.findByRoomIdAndTenantId(
                requestDTO.getRoomId(),
                currentUserId
        ).isPresent()) {
            throw new BadRequestException("Bạn đã đánh giá phòng này rồi");
        }

        // ✅ 7. Tạo Review
        Review review = Review.builder()
                .room(room)
                .tenant(tenant)
                .rating(requestDTO.getRating())
                .comment(requestDTO.getComment())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);
        System.out.println("✅ ReviewService: Review created successfully - ID: " + savedReview.getId());
        return convertToDTO(savedReview, currentUserId);
    }

    /**
     * US 11.3: Chỉnh sửa đánh giá
     */
    public ReviewDTO updateReview(Integer reviewId, ReviewRequestDTO requestDTO, Integer currentUserId) {
        if (currentUserId == null || currentUserId <= 0) {
            throw new ForbiddenException("Bạn phải đăng nhập để chỉnh sửa đánh giá");
        }

        // ✅ Validate rating is not null
        if (requestDTO.getRating() == null) {
            throw new BadRequestException("Đánh giá không được để trống");
        }
        if (requestDTO.getRating() < 1 || requestDTO.getRating() > 5) {
            throw new BadRequestException("Đánh giá phải từ 1 đến 5 sao");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Đánh giá không tồn tại"));

        if (review.getTenant().getId() != currentUserId) {
            throw new ForbiddenException("Bạn chỉ có thể chỉnh sửa đánh giá của chính mình");
        }

        // ✅ Only update if rating is provided and valid
        review.setRating(requestDTO.getRating());
        review.setComment(requestDTO.getComment() != null ? requestDTO.getComment() : "");
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        return convertToDTO(updatedReview, currentUserId);
    }

    /**
     * US 11.4: Xóa đánh giá
     */
    public void deleteReview(Integer reviewId, Integer currentUserId) {
        if (currentUserId == null || currentUserId <= 0) {
            throw new ForbiddenException("Bạn phải đăng nhập để xóa đánh giá");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Đánh giá không tồn tại"));

        if (review.getTenant().getId() != currentUserId) {
            throw new ForbiddenException("Bạn chỉ có thể xóa đánh giá của chính mình");
        }

        // ✅ Actually delete
        reviewRepository.deleteById(reviewId);
        System.out.println("✅ Review deleted: " + reviewId);
    }
    /**
     * Helper: Convert sang DTO
     */
    private ReviewDTO convertToDTO(Review review, Integer currentUserId) {
        // ✅ FIX: Dùng == thay vì .equals()
        boolean isOwner = currentUserId != null && currentUserId > 0 &&
                review.getTenant().getId() == currentUserId;

        return ReviewDTO.builder()
                .id(review.getId())
                .roomId(review.getRoom().getId())
                .tenantId(review.getTenant().getId())
                .tenantName(review.getTenant().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .canEdit(isOwner)
                .canDelete(isOwner)
                .build();
    }
}