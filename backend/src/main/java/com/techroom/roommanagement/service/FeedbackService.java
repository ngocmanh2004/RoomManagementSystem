package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.FeedbackCreateRequest;
import com.techroom.roommanagement.dto.FeedbackUpdateRequest;
import com.techroom.roommanagement.model.*;
import com.techroom.roommanagement.repository.ContractRepository;
import com.techroom.roommanagement.repository.FeedbackRepository;
import com.techroom.roommanagement.repository.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.techroom.roommanagement.dto.FeedbackProcessRequest;
import com.techroom.roommanagement.dto.TenantConfirmRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ContractRepository contractRepository;
    private final TenantRepository tenantRepository;
    private final NotificationService notificationService;

    /* ================= TENANT ================= */

    // Khách gửi phản hồi
    public Feedback create(Integer tenantUserId, FeedbackCreateRequest dto) {

        Tenant tenant = tenantRepository.findByUserId(tenantUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Không tìm thấy thông tin khách thuê"
                ));

        List<Contract> activeContracts =
                contractRepository.findByTenantIdAndStatus(
                        tenant.getId(), ContractStatus.ACTIVE
                );

        if (activeContracts.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bạn chưa có hợp đồng đang thuê"
            );
        }

        Contract contract = activeContracts.get(0);

        Feedback fb = new Feedback();
        fb.setSender(tenant.getUser());
        fb.setReceiver(contract.getRoom()
                .getBuilding()
                .getLandlord()
                .getUser());
        fb.setTenant(tenant);
        fb.setRoom(contract.getRoom());
        fb.setTitle(dto.getTitle());
        fb.setContent(dto.getContent());
        fb.setAttachmentUrl(dto.getAttachmentUrl());
        fb.setStatus(Feedback.Status.PENDING);

        Feedback saved = feedbackRepository.save(fb);

        // ✅ GỬI THÔNG BÁO CHO LANDLORD
        notificationService.createNotification(
                fb.getReceiver().getId(),
                "Phản hồi mới: " + fb.getTitle(),
                "Khách thuê " + tenant.getUser().getFullName() + " đã gửi phản hồi về phòng " + fb.getRoom().getName(),
                NotificationType.FEEDBACK_CREATED
        );

        return saved;
    }

    // Khách xác nhận hài lòng
    public Feedback tenantConfirm(
            Integer feedbackId,
            Integer tenantUserId,
            TenantConfirmRequest request
    ) {

        Feedback fb = getByIdAndTenantUser(feedbackId, tenantUserId);

        if (fb.getStatus() != Feedback.Status.RESOLVED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ xác nhận khi phản hồi đã được xử lý"
            );
        }

        fb.setStatus(Feedback.Status.TENANT_CONFIRMED);
        fb.setTenantSatisfied(request.getSatisfied());
        fb.setTenantFeedback(request.getTenantFeedback());

        return feedbackRepository.save(fb);
    }

    // Khách chưa hài lòng → mở lại
    public Feedback tenantReject(
            Integer feedbackId,
            Integer tenantUserId,
            String tenantFeedback
    ) {

        Feedback fb = getByIdAndTenantUser(feedbackId, tenantUserId);

        if (fb.getStatus() != Feedback.Status.RESOLVED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Phản hồi chưa ở trạng thái đã xử lý"
            );
        }

        fb.setStatus(Feedback.Status.TENANT_REJECTED);
        fb.setTenantSatisfied(false);
        fb.setTenantFeedback(tenantFeedback);
        fb.setResolvedAt(null);

        return feedbackRepository.save(fb);
    }

    /* ================= LANDLORD ================= */

    // Chủ trọ bắt đầu xử lý
    public Feedback startProcessing(Integer feedbackId, Integer landlordUserId) {

        Feedback fb = getByIdAndLandlord(feedbackId, landlordUserId);

        if (fb.getStatus() != Feedback.Status.PENDING
                && fb.getStatus() != Feedback.Status.TENANT_REJECTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ xử lý khi Chưa xử lý hoặc Khách chưa hài lòng"
            );
        }

        fb.setStatus(Feedback.Status.PROCESSING);
        Feedback saved = feedbackRepository.save(fb);

        // ✅ GỬI THÔNG BÁO CHO TENANT
        notificationService.createNotification(
                fb.getTenant().getUser().getId(),
                "Phản hồi đang được xử lý",
                "Phản hồi \"" + fb.getTitle() + "\" của bạn đang được chủ trọ xử lý.",
                NotificationType.FEEDBACK_PROCESSING
        );

        return saved;
    }

    // Chủ trọ hoàn tất xử lý
    public Feedback resolve(
            Integer feedbackId,
            Integer landlordUserId,
            String landlordNote
    ) {

        Feedback fb = getByIdAndLandlord(feedbackId, landlordUserId);

        if (fb.getStatus() != Feedback.Status.PROCESSING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ hoàn tất khi đang xử lý"
            );
        }

        fb.setStatus(Feedback.Status.RESOLVED);
        fb.setLandlordNote(landlordNote);
        fb.setResolvedAt(LocalDateTime.now());

        Feedback saved = feedbackRepository.save(fb);

        // ✅ GỬI THÔNG BÁO CHO TENANT
        notificationService.createNotification(
                fb.getTenant().getUser().getId(),
                "Phản hồi đã được xử lý",
                "Phản hồi \"" + fb.getTitle() + "\" đã được xử lý xong. Vui lòng xác nhận.",
                NotificationType.FEEDBACK_RESOLVED
        );

        return saved;
    }

    // Chủ trọ hủy phản hồi
    public Feedback cancel(Integer feedbackId, Integer landlordUserId) {

        Feedback fb = getByIdAndLandlord(feedbackId, landlordUserId);

        fb.setStatus(Feedback.Status.CANCELED);
        fb.setResolvedAt(LocalDateTime.now());

        Feedback saved = feedbackRepository.save(fb);

        // ✅ GỬI THÔNG BÁO CHO TENANT
        notificationService.createNotification(
                fb.getTenant().getUser().getId(),
                "Phản hồi đã bị hủy",
                "Phản hồi \"" + fb.getTitle() + "\" của bạn đã bị hủy bởi chủ trọ.",
                NotificationType.FEEDBACK_CANCELLED
        );

        return saved;
    }

    /* ================= DELETE ================= */

    public void delete(Integer feedbackId, Integer userId) {

        Feedback fb = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phản hồi"));

        if (!fb.getTenant().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa phản hồi này");
        }

        if (fb.getStatus() == Feedback.Status.RESOLVED
                || fb.getStatus() == Feedback.Status.TENANT_CONFIRMED) {
            throw new RuntimeException(
                    "Phản hồi đã được xử lý, không thể xóa"
            );
        }

        fb.setStatus(Feedback.Status.CANCELED);
        fb.setResolvedAt(LocalDateTime.now());

        feedbackRepository.save(fb);
    }

    /* ================= PRIVATE HELPERS ================= */

    private Feedback getByIdAndLandlord(
            Integer feedbackId,
            Integer landlordUserId
    ) {
        return feedbackRepository
                .findByIdAndReceiver_Id(feedbackId, landlordUserId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy phản hồi hoặc bạn không có quyền"
                ));
    }

    private Feedback getByIdAndTenantUser(
            Integer feedbackId,
            Integer tenantUserId
    ) {

        Tenant tenant = tenantRepository.findByUserId(tenantUserId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy tenant"
                ));

        return feedbackRepository
                .findByIdAndTenant_Id(feedbackId, tenant.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy phản hồi của bạn"
                ));
    }
    public Page<Feedback> getMyFeedbacks(
            Integer tenantUserId,
            Pageable pageable
    ) {
        Tenant tenant = tenantRepository.findByUserId(tenantUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tenant"));

        return feedbackRepository.findByTenant_Id(
                tenant.getId(),
                pageable
        );
    }
    public Page<Feedback> getForLandlord(
            Integer landlordUserId,
            Pageable pageable
    ) {
        return feedbackRepository.findByReceiver_Id(
                landlordUserId,
                pageable
        );
    }
    public Feedback process(
            Integer feedbackId,
            Integer landlordUserId,
            FeedbackProcessRequest request
    ) {

        Feedback fb = getByIdAndLandlord(feedbackId, landlordUserId);

        switch (request.getStatus()) {

            case PROCESSING -> {
                if (fb.getStatus() != Feedback.Status.PENDING
                        && fb.getStatus() != Feedback.Status.TENANT_REJECTED) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Không thể chuyển sang PROCESSING"
                    );
                }
                fb.setStatus(Feedback.Status.PROCESSING);

                // ✅ GỬI THÔNG BÁO CHO TENANT
                notificationService.createNotification(
                        fb.getTenant().getUser().getId(),
                        "Phản hồi đang được xử lý",
                        "Phản hồi \"" + fb.getTitle() + "\" của bạn đang được chủ trọ xử lý.",
                        NotificationType.FEEDBACK_PROCESSING
                );
            }

            case RESOLVED -> {
                if (fb.getStatus() != Feedback.Status.PROCESSING) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Chỉ RESOLVED khi đang PROCESSING"
                    );
                }
                fb.setStatus(Feedback.Status.RESOLVED);
                fb.setLandlordNote(request.getLandlordNote());
                fb.setResolvedAt(LocalDateTime.now());

                // ✅ GỬI THÔNG BÁO CHO TENANT
                notificationService.createNotification(
                        fb.getTenant().getUser().getId(),
                        "Phản hồi đã được xử lý",
                        "Phản hồi \"" + fb.getTitle() + "\" đã được xử lý xong. Vui lòng xác nhận.",
                        NotificationType.FEEDBACK_RESOLVED
                );
            }

            case CANCELED -> {
                fb.setStatus(Feedback.Status.CANCELED);
                fb.setResolvedAt(LocalDateTime.now());

                // ✅ GỬI THÔNG BÁO CHO TENANT
                notificationService.createNotification(
                        fb.getTenant().getUser().getId(),
                        "Phản hồi đã bị hủy",
                        "Phản hồi \"" + fb.getTitle() + "\" của bạn đã bị hủy bởi chủ trọ.",
                        NotificationType.FEEDBACK_CANCELLED
                );
            }

            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Trạng thái không hợp lệ"
            );
        }

        return feedbackRepository.save(fb);
    }
    public void update(Integer id, FeedbackUpdateRequest req) {
        Feedback fb = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        // Chỉ cho sửa khi chưa xử lý
        if (!fb.getStatus().equals(Feedback.Status.PENDING)) {
            throw new RuntimeException("Không thể chỉnh sửa phản hồi đã xử lý");
        }

        fb.setTitle(req.getTitle());
        fb.setContent(req.getContent());
        fb.setAttachmentUrl(req.getAttachmentUrl());

        feedbackRepository.save(fb);
    }
}