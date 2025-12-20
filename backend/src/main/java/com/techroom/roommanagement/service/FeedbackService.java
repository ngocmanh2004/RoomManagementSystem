package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.CreateFeedbackDTO;
import com.techroom.roommanagement.dto.SendNotificationRequest;
import com.techroom.roommanagement.model.*;
import com.techroom.roommanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ContractRepository contractRepository;
    private final TenantRepository tenantRepository;

    // Khách gửi phản hồi
    public Feedback create(Integer tenantUserId, CreateFeedbackDTO dto) {
        Tenant tenant = tenantRepository.findByUserId(tenantUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin khách thuê"
                ));

        Contract contract = contractRepository
                .findByTenantIdAndStatus(tenant.getId(), ContractStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Bạn chưa có hợp đồng đang thuê"
                ));
        Feedback f = new Feedback();
        f.setSender(tenant.getUser());
        f.setReceiver(contract.getRoom().getBuilding().getLandlord().getUser());
        f.setTenant(tenant);
        f.setRoom(contract.getRoom());
        f.setTitle(dto.getTitle());
        f.setContent(dto.getContent());
        f.setAttachmentUrl(dto.getAttachmentUrl());
        f.setStatus(Feedback.Status.PENDING);

        return feedbackRepository.save(f);
    }

    // Chủ trọ bắt đầu xử lý
    public Feedback startProcessing(Integer id, Integer landlordUserId) {
        Feedback f = getByIdAndLandlord(id, landlordUserId);
        // THÊM: Cho phép xử lý lại khi trạng thái là PENDING HOẶC TENANT_REJECTED
        if (f.getStatus() != Feedback.Status.PENDING && f.getStatus() != Feedback.Status.TENANT_REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ xử lý được khi Chưa xử lý hoặc Khách chưa hài lòng");
        }
        f.setStatus(Feedback.Status.PROCESSING);
        return feedbackRepository.save(f);
    }

    // Chủ trọ hoàn tất xử lý
    public Feedback resolve(Integer id, Integer landlordUserId, String note) {
        Feedback f = getByIdAndLandlord(id, landlordUserId);
        if (f.getStatus() != Feedback.Status.PROCESSING) throw new RuntimeException("Chỉ hoàn tất khi Đang xử lý");
        f.setStatus(Feedback.Status.RESOLVED);
        f.setLandlordNote(note);
        f.setResolvedAt(LocalDateTime.now());
        return feedbackRepository.save(f);
    }

    // Khách xác nhận hài lòng → kết thúc
    public Feedback tenantConfirm(Integer id, Integer tenantUserId) {
        Feedback f = getByIdAndTenantUser(id, tenantUserId);
        if (f.getStatus() != Feedback.Status.RESOLVED) throw new RuntimeException("Chỉ xác nhận khi đã xử lý");
        f.setStatus(Feedback.Status.TENANT_CONFIRMED);
        f.setTenantSatisfied(true);
        return feedbackRepository.save(f);
    }

    // Khách chưa hài lòng → mở lại
    public Feedback tenantReject(Integer id, Integer tenantUserId, String feedback) {
        Feedback f = getByIdAndTenantUser(id, tenantUserId);
        if (f.getStatus() != Feedback.Status.RESOLVED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Phản hồi này chưa ở trạng thái đã xử lý"
            );
        }
        f.setStatus(Feedback.Status.TENANT_REJECTED);
        f.setTenantFeedback(feedback);
        f.setTenantSatisfied(false);
        f.setResolvedAt(null);
        return feedbackRepository.save(f);
    }

    // Hủy phản hồi
    @Transactional
    public Feedback cancel(Integer id, Integer userId) {

        Feedback fb = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phản hồi"));

        if (fb.getRoom() == null || fb.getRoom().getLandlord() == null) {
            throw new RuntimeException("Phản hồi chưa gán chủ trọ");
        }

        Integer landlordUserId = fb.getRoom()
                .getLandlord()
                .getUser()
                .getId();

        if (landlordUserId == null || !landlordUserId.equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy phản hồi này");
        }

        fb.setStatus(Feedback.Status.CANCELED);
        return feedbackRepository.save(fb);
    }

    public Feedback getByIdAndLandlord(Integer id, Integer landlordUserId) {
        return feedbackRepository.findByIdAndRoom_Landlord_User_Id(id, landlordUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoặc bạn không có quyền"));
    }

    private Feedback getByIdAndTenantUser(Integer id, Integer tenantUserId) {
        Tenant tenant = tenantRepository.findByUserId(tenantUserId).orElseThrow();
        return feedbackRepository.findByIdAndTenantId(id, tenant.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phản hồi của bạn"));
    }
    @Transactional
    public void delete(Integer feedbackId, Integer userId) {

        Feedback fb = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phản hồi"));

        // ✅ Chỉ cho tenant tạo phản hồi được xóa
        if (!fb.getTenant().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa phản hồi này");
        }

        // ❌ Không cho xóa khi đã xử lý xong
        if (fb.getStatus() == Feedback.Status.RESOLVED
                || fb.getStatus() == Feedback.Status.TENANT_CONFIRMED) {
            throw new RuntimeException("Phản hồi đã được xử lý, không thể xóa");
        }

        fb.setStatus(Feedback.Status.CANCELED);
        fb.setResolvedAt(LocalDateTime.now());
        feedbackRepository.save(fb);

    }

}
