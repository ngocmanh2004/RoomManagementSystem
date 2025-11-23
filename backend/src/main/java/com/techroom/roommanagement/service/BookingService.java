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
        System.out.println("Creating booking - userId: " + tenantUserId + ", roomId: " + bookingDTO.getRoomId());
        
        if (tenantUserId == null || tenantUserId <= 0) {
            throw new BadRequestException("Tenant ID khong hop le");
        }
        
        if (bookingDTO.getRoomId() == null || bookingDTO.getRoomId() <= 0) {
            throw new BadRequestException("Room ID khong hop le");
        }
        
        if (bookingDTO.getStartDate() == null) {
            throw new BadRequestException("Ngay bat dau la bat buoc");
        }
        
        if (bookingDTO.getEndDate() != null && bookingDTO.getStartDate().isAfter(bookingDTO.getEndDate())) {
            throw new BadRequestException("Ngay ket thuc phai sau ngay bat dau");
        }
        
        Room room = roomRepository.findById(bookingDTO.getRoomId())
            .orElseThrow(() -> new NotFoundException("Phong khong ton tai"));
        
        System.out.println("Room status: " + room.getStatus());
        
        if (!Room.RoomStatus.AVAILABLE.equals(room.getStatus())) {
            throw new BadRequestException("Phong nay khong kha dung. Trang thai: " + room.getStatus().getDisplayName());
        }
        
        if (contractRepository.existsByRoomIdAndStatus(bookingDTO.getRoomId(), ContractStatus.PENDING)) {
            throw new BadRequestException("Phong nay da co yeu cau dat thue chua xu ly");
        }
        
        if (contractRepository.existsByRoomIdAndStatus(bookingDTO.getRoomId(), ContractStatus.ACTIVE)) {
            throw new BadRequestException("Phong nay da duoc thue");
        }
        
        Tenant tenant = tenantRepository.findByUserId(tenantUserId)
            .orElseThrow(() -> new NotFoundException("Thong tin nguoi thue khong ton tai"));
        
        Contract contract = Contract.builder()
            .room(room)
            .tenant(tenant)
            .startDate(bookingDTO.getStartDate())
            .endDate(bookingDTO.getEndDate())
            .deposit(bookingDTO.getDeposit() != null ? bookingDTO.getDeposit() : 0.0)
            .notes(bookingDTO.getNotes() != null ? bookingDTO.getNotes() : "")
            .status(ContractStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        Contract savedContract = contractRepository.save(contract);
        System.out.println("Contract created - ID: " + savedContract.getId());
        
        notificationService.sendNotification(
            tenantUserId,
            "Yeu cau thue phong",
            "Yeu cau thue phong \"" + room.getName() + "\" da duoc gui.",
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
                System.out.println("Notification sent to landlord - ID: " + landlordUser.getId());
            }
        }
        
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