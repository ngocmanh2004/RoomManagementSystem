package com.techroom.roommanagement.service;

import com.techroom.roommanagement.exception.*;
import com.techroom.roommanagement.model.*;
import com.techroom.roommanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LandlordRequestService {

    private final LandlordRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final LandlordRepository landlordRepository;

    @Value("${upload.path:/uploads}")
    private String uploadPath;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/jpg", "application/pdf"
    );

    public List<LandlordRequest> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<LandlordRequest> getPendingRequests() {
        return requestRepository.findByStatus(LandlordRequest.Status.PENDING);
    }

    public List<LandlordRequest> getApprovedRequests() {
        return requestRepository.findByStatus(LandlordRequest.Status.APPROVED);
    }

    public List<LandlordRequest> getRejectedRequests() {
        return requestRepository.findByStatus(LandlordRequest.Status.REJECTED);
    }

    public Optional<LandlordRequest> getById(int id) {
        return requestRepository.findById(id);
    }

    public Optional<LandlordRequest> getRequestByUserId(int userId) {
        return requestRepository.findLatestByUserId(userId);
    }

    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("PENDING", requestRepository.countByStatus(LandlordRequest.Status.PENDING));
        stats.put("APPROVED", requestRepository.countByStatus(LandlordRequest.Status.APPROVED));
        stats.put("REJECTED", requestRepository.countByStatus(LandlordRequest.Status.REJECTED));
        stats.put("TOTAL", requestRepository.count());
        return stats;
    }

    @Transactional
    public LandlordRequest createRequest(
            int userId,
            String cccd,
            String address,
            Integer expectedRoomCount,
            Integer provinceCode,
            Integer districtCode,
            MultipartFile frontImage,
            MultipartFile backImage,
            MultipartFile businessLicense
    ) {
        try {
            // 1. Validate user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            // 2. Check if already landlord
            if (landlordRepository.existsByUserId(userId)) {
                throw new DuplicateResourceException("Bạn đã là chủ trọ");
            }

            // 3. Check existing pending request
            if (requestRepository.existsByUserIdAndStatus(userId, LandlordRequest.Status.PENDING)) {
                throw new DuplicateResourceException("Bạn đã có yêu cầu đang chờ duyệt");
            }

            // 4. Validate input
            validateRegistrationInput(cccd, expectedRoomCount);

            // 5. Save files
            String frontImagePath = saveFile(frontImage, "front_");
            String backImagePath = saveFile(backImage, "back_");
            String businessLicensePath = saveFile(businessLicense, "business_");

            // 6. Create request
            LandlordRequest request = LandlordRequest.builder()
                    .user(user)
                    .cccd(cccd)
                    .address(address)
                    .expectedRoomCount(expectedRoomCount)
                    .provinceCode(provinceCode)
                    .districtCode(districtCode)
                    .frontImagePath(frontImagePath)
                    .backImagePath(backImagePath)
                    .businessLicensePath(businessLicensePath)
                    .status(LandlordRequest.Status.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            LandlordRequest saved = requestRepository.save(request);
            log.info("Created landlord request {} for user {}", saved.getId(), userId);

            return saved;

        } catch (IOException e) {
            log.error("File upload error for user {}", userId, e);
            throw new BusinessException("Có lỗi xảy ra khi tải file: " + e.getMessage());
        }
    }

    @Transactional
    public void approveRequest(int requestId) {
        // 1. Find request
        LandlordRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("LandlordRequest", "id", requestId));

        // 2. Validate status
        if (request.getStatus() != LandlordRequest.Status.PENDING) {
            throw new BusinessException("Yêu cầu không ở trạng thái chờ duyệt");
        }

        User user = request.getUser();

        // 3. Double check no existing landlord
        if (landlordRepository.existsByUserId(user.getId())) {
            throw new DuplicateResourceException("User này đã là landlord");
        }

        // 4. Update request status
        request.setStatus(LandlordRequest.Status.APPROVED);
        request.setProcessedAt(LocalDateTime.now());
        requestRepository.save(request);

        // 5. Create Landlord record
        Landlord landlord = Landlord.builder()
                .user(user)
                .cccd(request.getCccd())
                .address(request.getAddress())
                .expectedRoomCount(request.getExpectedRoomCount())
                .provinceCode(request.getProvinceCode())
                .districtCode(request.getDistrictCode())
                .frontImagePath(request.getFrontImagePath())
                .backImagePath(request.getBackImagePath())
                .businessLicensePath(request.getBusinessLicensePath())
                .approved(Landlord.ApprovalStatus.APPROVED)
                .utilityMode(Landlord.UtilityMode.LANDLORD_INPUT)
                .createdAt(LocalDateTime.now())
                .build();

        landlordRepository.save(landlord);

        // 6. Update user role to LANDLORD
        user.setRole(1);
        userRepository.save(user);

        log.info("Approved landlord request {} for user {}", requestId, user.getId());
    }

    @Transactional
    public void rejectRequest(int requestId, String reason) {
        // 1. Validate reason
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("Vui lòng nhập lý do từ chối");
        }

        // 2. Find request
        LandlordRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("LandlordRequest", "id", requestId));

        // 3. Validate status
        if (request.getStatus() != LandlordRequest.Status.PENDING) {
            throw new BusinessException("Chỉ có thể từ chối yêu cầu đang chờ duyệt");
        }

        // 4. Update status
        request.setStatus(LandlordRequest.Status.REJECTED);
        request.setRejectionReason(reason);
        request.setProcessedAt(LocalDateTime.now());
        requestRepository.save(request);

        log.info("Rejected landlord request {} for user {}: {}",
                requestId, request.getUser().getId(), reason);
    }

    @Transactional
    public void cancelRequest(int userId) {
        LandlordRequest request = requestRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("LandlordRequest", "userId", userId));

        if (request.getStatus() != LandlordRequest.Status.PENDING) {
            throw new BusinessException("Chỉ có thể hủy yêu cầu đang chờ duyệt");
        }

        // Delete files
        deleteFilesSafely(request);

        // Delete request
        requestRepository.delete(request);
        log.info("Cancelled landlord request {} for user {}", request.getId(), userId);
    }

    private void validateRegistrationInput(String cccd, Integer expectedRoomCount) {
        if (cccd == null || !cccd.matches("\\d{9,12}")) {
            throw new BusinessException("Số CCCD không hợp lệ (phải là 9-12 chữ số)");
        }

        if (expectedRoomCount == null || expectedRoomCount < 1) {
            throw new BusinessException("Số phòng dự kiến phải lớn hơn 0");
        }
    }

    private String saveFile(MultipartFile file, String prefix) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File không được để trống");
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("File không được lớn hơn 5MB");
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("Chỉ chấp nhận file JPG, PNG hoặc PDF");
        }

        // Generate safe filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("Tên file không hợp lệ");
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        String filename = prefix + UUID.randomUUID() + extension;

        // Create upload directory if not exists
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Save file
        Path targetPath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        log.debug("Saved file: {}", filename);
        return filename;
    }

    private void deleteFilesSafely(LandlordRequest request) {
        deleteFileIfExists(request.getFrontImagePath());
        deleteFileIfExists(request.getBackImagePath());
        deleteFileIfExists(request.getBusinessLicensePath());
    }

    private void deleteFileIfExists(String filename) {
        if (filename == null || filename.isEmpty()) {
            return;
        }

        try {
            Path filePath = Paths.get(uploadPath).resolve(filename);
            Files.deleteIfExists(filePath);
            log.debug("Deleted file: {}", filename);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", filename, e);
        }
    }
}