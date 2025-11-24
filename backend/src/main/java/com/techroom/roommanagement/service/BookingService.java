package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.BookingDTO;
import com.techroom.roommanagement.model.*;
import com.techroom.roommanagement.repository.ContractRepository;
import com.techroom.roommanagement.repository.RoomRepository;
import com.techroom.roommanagement.repository.TenantRepository;
import com.techroom.roommanagement.exception.BadRequestException;
import com.techroom.roommanagement.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class BookingService {
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public Contract createBooking(BookingDTO bookingDTO, Integer tenantUserId) {
    System.out.println("========== CREATE BOOKING START ==========");
    System.out.println("User ID: " + tenantUserId);
    System.out.println("Room ID: " + bookingDTO.getRoomId());
    System.out.println("Full Name: " + bookingDTO.getFullName());
    System.out.println("CCCD: " + bookingDTO.getCccd());
    System.out.println("Phone: " + bookingDTO.getPhone());
    System.out.println("Address: " + bookingDTO.getAddress());
    
    if (tenantUserId == null || tenantUserId <= 0) {
        System.out.println("❌ REJECT: Invalid tenant userId");
        throw new BadRequestException("Tenant ID khong hop le");
    }
    
    if (bookingDTO.getRoomId() == null || bookingDTO.getRoomId() <= 0) {
        System.out.println("❌ REJECT: Invalid roomId");
        throw new BadRequestException("Room ID khong hop le");
    }
    
    Room room = roomRepository.findById(bookingDTO.getRoomId())
        .orElseThrow(() -> {
            System.out.println("❌ REJECT: Room not found");
            return new NotFoundException("Phong khong ton tai");
        });
    
    System.out.println("✅ Room found: " + room.getName());
    System.out.println("✅ Room status: " + room.getStatus());
    
    if (!Room.RoomStatus.AVAILABLE.equals(room.getStatus())) {
        System.out.println("❌ REJECT: Room not available");
        throw new BadRequestException("Phong nay khong kha dung. Trang thai: " + room.getStatus().getDisplayName());
    }
    
    Tenant tenant = tenantRepository.findByUserId(tenantUserId)
        .orElseThrow(() -> {
            System.out.println("❌ REJECT: Tenant record not found for userId: " + tenantUserId);
            return new NotFoundException("Thong tin nguoi thue khong ton tai. Vui long cap nhat thong tin ca nhan.");
        });
    
    System.out.println("✅ Tenant found: ID=" + tenant.getId());
    
    Contract contract = Contract.builder()
        .room(room)
        .tenant(tenant)
        .fullName(bookingDTO.getFullName())           // ✅ THÊM
        .cccd(bookingDTO.getCccd())                   // ✅ THÊM
        .phone(bookingDTO.getPhone())                 // ✅ THÊM
        .address(bookingDTO.getAddress())             // ✅ THÊM
        .startDate(bookingDTO.getStartDate())
        .endDate(bookingDTO.getEndDate())
        .deposit(bookingDTO.getDeposit() != null ? bookingDTO.getDeposit() : 0.0)
        .notes(bookingDTO.getNotes() != null ? bookingDTO.getNotes() : "")
        .status(ContractStatus.PENDING)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
    
    Contract savedContract = contractRepository.save(contract);
    System.out.println("✅ Contract created successfully - ID: " + savedContract.getId());
    
    notificationService.sendNotification(
        tenantUserId,
        "Yeu cau thue phong",
        "Yeu cau thue phong \"" + room.getName() + "\" da duoc gui thanh cong.",
        "BOOKING_CREATED"
    );
    
    if (room.getBuilding() != null && room.getBuilding().getLandlord() != null) {
        User landlordUser = room.getBuilding().getLandlord().getUser();
        if (landlordUser != null && landlordUser.getId() != null) {
            notificationService.sendNotification(
                landlordUser.getId(),
                "Yeu cau thue phong moi",
                "Co yeu cau thue phong \"" + room.getName() + "\" tu " + tenant.getUser().getFullName(),
                "BOOKING_REQUEST"
            );
        }
    }
    
    System.out.println("========== CREATE BOOKING END ==========");
    return savedContract;
}
    
    public Page<Contract> getTenantContracts(Integer tenantId, Pageable pageable) {
        return contractRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, pageable);
    }
    
    public Contract getTenantContractById(Integer contractId, Integer tenantId) {
        return contractRepository.findByIdAndTenantId(contractId, tenantId)
            .orElseThrow(() -> new NotFoundException("Hop dong khong ton tai"));
    }
    
    public Page<Contract> getLandlordContracts(Integer landlordId, Pageable pageable) {
        return contractRepository.findByLandlordId(landlordId, pageable);
    }
    
    public Page<Contract> getLandlordContractsByStatus(Integer landlordId, String status, Pageable pageable) {
        try {
            ContractStatus contractStatus = ContractStatus.valueOf(status.toUpperCase());
            return contractRepository.findByLandlordIdAndStatus(landlordId, contractStatus, pageable);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trang thai hop dong khong hop le: " + status);
        }
    }
    
    public Contract getContractById(Integer contractId) {
        return contractRepository.findById(contractId)
            .orElseThrow(() -> new NotFoundException("Hop dong khong ton tai"));
    }
    
    public Contract getLandlordContractById(Integer contractId, Integer landlordId) {
        return contractRepository.findByIdAndLandlordId(contractId, landlordId)
            .orElseThrow(() -> new NotFoundException("Hop dong khong ton tai"));
    }
    
    public Contract approveContract(Integer contractId, Integer landlordId) {
        Contract contract = getLandlordContractById(contractId, landlordId);
        
        if (!ContractStatus.PENDING.equals(contract.getStatus())) {
            throw new BadRequestException("Chi co the duyet hop dong o trang thai PENDING");
        }
        
        contract.setStatus(ContractStatus.APPROVED);
        Contract savedContract = contractRepository.save(contract);
        
        Room room = contract.getRoom();
        room.setStatus(Room.RoomStatus.OCCUPIED);
        roomRepository.save(room);
        System.out.println("Room status updated to OCCUPIED - roomId: " + room.getId());
        
        notificationService.sendNotification(
            contract.getTenant().getUser().getId(),
            "Hop dong duoc duyet",
            "Hop dong thue phong \"" + room.getName() + "\" da duoc chap nhan.",
            "CONTRACT_APPROVED"
        );
        
        System.out.println("Contract approved - ID: " + contractId);
        return savedContract;
    }
    
    public Contract rejectContract(Integer contractId, String rejectionReason, Integer landlordId) {
        Contract contract = getLandlordContractById(contractId, landlordId);
        
        if (!ContractStatus.PENDING.equals(contract.getStatus())) {
            throw new BadRequestException("Chi co the tu choi hop dong o trang thai PENDING");
        }
        
        contract.setStatus(ContractStatus.REJECTED);
        contract.setRejectionReason(rejectionReason);
        Contract savedContract = contractRepository.save(contract);
        
        notificationService.sendNotification(
            contract.getTenant().getUser().getId(),
            "Hop dong bi tu choi",
            "Hop dong thue phong bi tu choi. Ly do: " + rejectionReason,
            "CONTRACT_REJECTED"
        );
        
        System.out.println("Contract rejected - ID: " + contractId);
        return savedContract;
    }
}