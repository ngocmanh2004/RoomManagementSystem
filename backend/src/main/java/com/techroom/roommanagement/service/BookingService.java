package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.BookingDTO;
import com.techroom.roommanagement.dto.DirectContractDTO;
import com.techroom.roommanagement.dto.ExtendContractRequest;
import com.techroom.roommanagement.dto.TerminateContractRequest;
import com.techroom.roommanagement.exception.UnauthorizedException;
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


import java.math.BigDecimal;
import java.time.LocalDate;
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

        if (tenantUserId == null || tenantUserId <= 0) {
            System.out.println("REJECT: Invalid tenant userId");
            throw new BadRequestException("Tenant ID khong hop le");
        }

        if (bookingDTO.getRoomId() == null || bookingDTO.getRoomId() <= 0) {
            System.out.println("REJECT: Invalid roomId");
            throw new BadRequestException("Room ID khong hop le");
        }

        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(() -> {
                    System.out.println("REJECT: Room not found");
                    return new NotFoundException("Phong khong ton tai");
                });

        System.out.println("Room found: " + room.getName());
        System.out.println("Room status: " + room.getStatus());

        if (!Room.RoomStatus.AVAILABLE.equals(room.getStatus())) {
            System.out.println("REJECT: Room not available");
            throw new BadRequestException("Phong nay khong kha dung");
        }

        Tenant tenant = tenantRepository.findByUserId(tenantUserId)
                .orElseThrow(() -> {
                    System.out.println("REJECT: Tenant record not found");
                    return new NotFoundException("Thong tin nguoi thue khong ton tai");
                });

        System.out.println(" Tenant found: ID=" + tenant.getId());

        Contract contract = Contract.builder()
                .room(room)
                .tenant(tenant)
                .fullName(bookingDTO.getFullName())
                .cccd(bookingDTO.getCccd())
                .phone(bookingDTO.getPhone())
                .address(bookingDTO.getAddress())
                .startDate(bookingDTO.getStartDate())
                .endDate(bookingDTO.getEndDate())
                .deposit(bookingDTO.getDeposit() != null ? bookingDTO.getDeposit() : BigDecimal.ZERO)
                .monthlyRent(room.getPrice())
                .notes(bookingDTO.getNotes() != null ? bookingDTO.getNotes() : "")
                .status(ContractStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Contract savedContract = contractRepository.save(contract);

        if (savedContract.getContractCode() == null) {
            savedContract.setContractCode("HD" + String.format("%07d", savedContract.getId()));
            savedContract = contractRepository.save(savedContract);
        }

        System.out.println("Contract created - ID: " + savedContract.getId());
        System.out.println("Contract code: " + savedContract.getContractCode());

        // Gửi thông báo...
        notificationService.createNotification(
                tenantUserId,
                "Yeu cau thue phong",
                "Yeu cau thue phong \"" + room.getName() + "\" da duoc gui thanh cong.",
                NotificationType.BOOKING_CREATED
        );

        if (room.getBuilding() != null && room.getBuilding().getLandlord() != null) {
            User landlordUser = room.getBuilding().getLandlord().getUser();
            if (landlordUser != null && landlordUser.getId() != null) {
                notificationService.createNotification(
                        landlordUser.getId(),
                        "Yeu cau thue phong moi",
                        "Co yeu cau thue phong \"" + room.getName() + "\" tu " + tenant.getUser().getFullName(),
                        NotificationType.BOOKING_CREATED
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
        System.out.println("========== GET LANDLORD CONTRACTS ==========");
        System.out.println("Landlord ID: " + landlordId);

        Page<Contract> contracts = contractRepository.findByLandlordId(landlordId, pageable);

        System.out.println("Total contracts found: " + contracts.getTotalElements());
        System.out.println("========================================");

        return contracts;
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

        contract.setStatus(ContractStatus.ACTIVE);
        contract.setUpdatedAt(LocalDateTime.now());
        Contract savedContract = contractRepository.save(contract);

        Room room = contract.getRoom();
        room.setStatus(Room.RoomStatus.OCCUPIED);
        roomRepository.save(room);
        System.out.println("Room status updated to OCCUPIED - roomId: " + room.getId());


        notificationService.createNotification(
                contract.getTenant().getUser().getId(),
                "Hợp đồng được duyệt",
                "Yêu cầu thuê phòng \"" + room.getName() + "\" của bạn đã được chấp nhận.",
                NotificationType.CONTRACT_APPROVED
        );

        System.out.println("Contract approved - ID: " + contractId + ", Status: ACTIVE");
        return savedContract;
    }

    public Contract rejectContract(Integer contractId, String rejectionReason, Integer landlordId) {
        Contract contract = getLandlordContractById(contractId, landlordId);

        if (!ContractStatus.PENDING.equals(contract.getStatus())) {
            throw new BadRequestException("Chi co the tu choi hop dong o trang thai PENDING");
        }

        contract.setStatus(ContractStatus.REJECTED);
        contract.setRejectionReason(rejectionReason);
        contract.setUpdatedAt(LocalDateTime.now());
        Contract savedContract = contractRepository.save(contract);

        // Gửi thông báo
        notificationService.createNotification(
                contract.getTenant().getUser().getId(),
                "Hợp đồng bị từ chối",
                "Yêu cầu thuê phòng \"" + contract.getRoom().getName() + "\" bị từ chối. Lý do: " + rejectionReason,
                NotificationType.CONTRACT_REJECTED
        );

        System.out.println("Contract rejected - ID: " + contractId);
        return savedContract;
    }

    public Contract terminateContract(
            Integer contractId,
            TerminateContractRequest request,
            Integer landlordId
    ) {
        System.out.println("========== TERMINATE CONTRACT START ==========");
        System.out.println("Contract ID: " + contractId);
        System.out.println("Landlord ID: " + landlordId);
        System.out.println("Termination Type: " + request.getTerminationType());

        // Validate: Kiểm tra hợp đồng có tồn tại và thuộc về landlord này không
        Contract contract = contractRepository.findByIdAndLandlordId(contractId, landlordId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay hop dong hoac ban khong co quyen truy cap"));

        // Validate: Chỉ thanh lý được hợp đồng đang ACTIVE
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new BadRequestException(
                    "Chi co the thanh ly hop dong dang hoat dong. " +
                            "Trang thai hien tai: " + contract.getStatus()
            );
        }

        // Cập nhật trạng thái hợp đồng dựa trên loại thanh lý
        ContractStatus newStatus;
        String terminationMessage;

        if (request.getTerminationType() == TerminateContractRequest.TerminationType.EXPIRED) {
            newStatus = ContractStatus.EXPIRED;
            terminationMessage = "Hop dong da het han dung thoi han";
        } else { // CANCELLED
            newStatus = ContractStatus.CANCELLED;
            terminationMessage = "Hop dong da cham dut som";
        }

        contract.setStatus(newStatus);
        contract.setUpdatedAt(LocalDateTime.now());

        // Lưu lý do thanh lý (nếu có)
        if (request.getReason() != null && !request.getReason().trim().isEmpty()) {
            contract.setRejectionReason(terminationMessage + ": " + request.getReason());
        } else {
            contract.setRejectionReason(terminationMessage);
        }

        // QUAN TRỌNG: Cập nhật trạng thái phòng về AVAILABLE
        Room room = contract.getRoom();
        if (room == null) {
            throw new NotFoundException("Khong tim thay phong lien ket voi hop dong nay");
        }

        room.setStatus(Room.RoomStatus.AVAILABLE);
        roomRepository.save(room);

        // Lưu hợp đồng
        Contract savedContract = contractRepository.save(contract);

        // Gửi thông báo cho tenant
        notificationService.createNotification(
                contract.getTenant().getUser().getId(),
                "Hop dong da thanh ly",
                "Hop dong thue phong \"" + room.getName() + "\" da duoc thanh ly. " + terminationMessage,
                NotificationType.CONTRACT_CANCELLED
        );

        // Log để debug
        System.out.println(" Thanh lý hợp đồng #" + contractId +
                " - Loại: " + request.getTerminationType() +
                " - Trạng thái mới: " + newStatus +
                " - Phòng #" + room.getId() + " đã chuyển về AVAILABLE");
        System.out.println("========== TERMINATE CONTRACT END ==========");

        return savedContract;
    }

    // Thêm method này vào BookingService.java

    public Contract createDirectContract(DirectContractDTO dto, Integer landlordId) {
        System.out.println("========== CREATE DIRECT CONTRACT START ==========");
        System.out.println("Landlord ID: " + landlordId);
        System.out.println("Room ID: " + dto.getRoomId());
        System.out.println("Tenant ID: " + dto.getTenantId());

        // Validate room
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new NotFoundException("Phòng không tồn tại"));

        // Kiểm tra room có thuộc landlord này không
        if (room.getBuilding() == null ||
                room.getBuilding().getLandlord() == null ||
                !room.getBuilding().getLandlord().getId().equals(landlordId)) {
            throw new BadRequestException("Bạn không có quyền tạo hợp đồng cho phòng này");
        }

        // Kiểm tra phòng phải đang AVAILABLE
        if (!Room.RoomStatus.AVAILABLE.equals(room.getStatus())) {
            throw new BadRequestException(
                    "Phòng này không khả dụng. Trạng thái hiện tại: " + room.getStatus()
            );
        }

        // Validate tenant
        Tenant tenant = tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new NotFoundException("Khách thuê không tồn tại"));

        // Kiểm tra tenant có hợp đồng ACTIVE nào không
        boolean hasActiveContract = contractRepository.existsByTenantIdAndStatus(
                tenant.getId(),
                ContractStatus.ACTIVE
        );

        if (hasActiveContract) {
            throw new BadRequestException(
                    "Khách thuê này đã có hợp đồng đang hoạt động. " +
                            "Vui lòng chọn khách thuê khác hoặc chấm dứt hợp đồng cũ trước."
            );
        }

        // Tạo hợp đồng với status = ACTIVE (bỏ qua PENDING)
        Contract contract = Contract.builder()
                .room(room)
                .tenant(tenant)
                .fullName(dto.getFullName())
                .cccd(dto.getCccd())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .deposit(dto.getDeposit())
                .monthlyRent(room.getPrice())
                .notes(dto.getNotes() != null ? dto.getNotes() : "")
                .status(ContractStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Contract savedContract = contractRepository.save(contract);

        // Generate contract code
        if (savedContract.getContractCode() == null) {
            savedContract.setContractCode("HD" + String.format("%07d", savedContract.getId()));
            savedContract = contractRepository.save(savedContract);
        }

        // Cập nhật trạng thái phòng sang OCCUPIED
        room.setStatus(Room.RoomStatus.OCCUPIED);
        roomRepository.save(room);
        System.out.println(" Room status updated to OCCUPIED - roomId: " + room.getId());

        // 9. Gửi thông báo cho tenant
        notificationService.createNotification(
                tenant.getUser().getId(),
                "Hợp đồng thuê phòng mới",
                "Bạn đã được tạo hợp đồng thuê phòng \"" + room.getName() +
                        "\". Hợp đồng có hiệu lực từ " + dto.getStartDate(),
                NotificationType.CONTRACT_CREATED
        );

        System.out.println(" Direct contract created - ID: " + savedContract.getId());
        System.out.println("========== CREATE DIRECT CONTRACT END ==========");

        return savedContract;
    }

    // Thêm method này vào BookingService.java

    /**
     * Gia hạn hợp đồng (chỉ cho ACTIVE contracts)
     */
    public Contract extendContract(Integer contractId, ExtendContractRequest request, Integer landlordId) {
        System.out.println("========== EXTEND CONTRACT ==========");
        System.out.println("Contract ID: " + contractId);
        System.out.println("New End Date: " + request.getNewEndDate());
        System.out.println("Landlord ID: " + landlordId);

        // Tìm hợp đồng
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Hợp đồng không tồn tại"));

        // Kiểm tra quyền sở hữu
        Room room = contract.getRoom();
        if (!room.getBuilding().getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Bạn không có quyền gia hạn hợp đồng này");
        }

        // Kiểm tra trạng thái - CHỈ ACTIVE mới được gia hạn
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new BadRequestException("Chỉ có thể gia hạn hợp đồng đang ACTIVE. Trạng thái hiện tại: " + contract.getStatus());
        }

        // Validate ngày kết thúc mới
        LocalDate newEndDate = request.getNewEndDate();
        LocalDate today = LocalDate.now();

        if (newEndDate.isBefore(today)) {
            throw new BadRequestException("Ngày kết thúc mới phải lớn hơn hoặc bằng ngày hiện tại");
        }

        // Nếu có ngày kết thúc cũ, kiểm tra ngày mới phải lớn hơn
        if (contract.getEndDate() != null && newEndDate.isBefore(contract.getEndDate())) {
            throw new BadRequestException("Ngày kết thúc mới phải lớn hơn ngày kết thúc cũ ("
                    + contract.getEndDate() + ")");
        }

        // Cập nhật thông tin
        contract.setEndDate(newEndDate);

        // Cập nhật notes nếu có
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            String currentNotes = contract.getNotes() != null ? contract.getNotes() : "";
            String extensionNote = "\n[Gia hạn " + LocalDate.now() + "]: " + request.getNotes();
            contract.setNotes(currentNotes + extensionNote);
        }

        contract.setUpdatedAt(LocalDateTime.now());

        Contract savedContract = contractRepository.save(contract);

        System.out.println("Contract extended successfully. New end date: " + newEndDate);

        return savedContract;
    }
}