package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.Landlord;
import com.techroom.roommanagement.model.LandlordRequest;
import com.techroom.roommanagement.repository.LandlordRepository;
import com.techroom.roommanagement.repository.LandlordRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LandlordService {

    private final LandlordRepository landlordRepository;
    private final LandlordRequestRepository landlordRequestRepository;

    public List<Landlord> getAllLandlords() {
        return landlordRepository.findAll();
    }

    public Optional<Landlord> getLandlordById(int id) {
        return landlordRepository.findById(id);
    }

    public Optional<Landlord> getLandlordByUserId(int userId) {
        return landlordRepository.findByUserId(userId);
    }

    public boolean isLandlord(int userId) {
        return landlordRepository.existsByUserId(userId);
    }

    public List<Landlord> getApprovedLandlords() {
        return landlordRepository.findByApproved(Landlord.ApprovalStatus.APPROVED);
    }

    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();

        // Thống kê từ bảng landlords (những người đã được approve)
        long totalLandlords = landlordRepository.count();
        long approvedLandlords = landlordRepository.countByApproved(Landlord.ApprovalStatus.APPROVED);

        // Thống kê từ bảng landlord_requests
        long pendingRequests = landlordRequestRepository.countByStatus(LandlordRequest.Status.PENDING);
        long approvedRequests = landlordRequestRepository.countByStatus(LandlordRequest.Status.APPROVED);
        long rejectedRequests = landlordRequestRepository.countByStatus(LandlordRequest.Status.REJECTED);
        long totalRequests = landlordRequestRepository.count();

        stats.put("totalLandlords", totalLandlords);
        stats.put("approvedLandlords", approvedLandlords);
        stats.put("pendingRequests", pendingRequests);
        stats.put("approvedRequests", approvedRequests);
        stats.put("rejectedRequests", rejectedRequests);
        stats.put("totalRequests", totalRequests);

        return stats;
    }
}