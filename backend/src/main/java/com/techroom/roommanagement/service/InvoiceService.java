package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.InvoiceCreateRequest;
import com.techroom.roommanagement.dto.InvoiceDTO;
import com.techroom.roommanagement.model.Contract;
import com.techroom.roommanagement.model.Invoice;
import com.techroom.roommanagement.model.Invoice.InvoiceStatus;
import com.techroom.roommanagement.repository.ContractRepository;
import com.techroom.roommanagement.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;

    // ==================== CREATE & UPDATE ====================

    /**
     * Tạo hóa đơn mới
     */
    @Transactional
    public InvoiceDTO createInvoice(InvoiceCreateRequest request) {
        // Kiểm tra hợp đồng tồn tại
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new RuntimeException("Hợp đồng không tồn tại"));

        // Kiểm tra hóa đơn đã tồn tại cho tháng này
        invoiceRepository.findByContractIdAndMonth(request.getContractId(), request.getMonth())
                .ifPresent(inv -> {
                    throw new RuntimeException("Hóa đơn cho tháng này đã tồn tại");
                });

        // Lấy tiền phòng từ hợp đồng
        BigDecimal roomRent = contract.getMonthlyRent() != null ? contract.getMonthlyRent() : BigDecimal.ZERO;

        // Tính tổng
        BigDecimal totalAmount = roomRent
                .add(request.getElectricity() != null ? request.getElectricity() : BigDecimal.ZERO)
                .add(request.getWater() != null ? request.getWater() : BigDecimal.ZERO)
                .add(request.getExtraCost() != null ? request.getExtraCost() : BigDecimal.ZERO);

        // Tạo hóa đơn
        Invoice invoice = Invoice.builder()
                .contract(contract)
                .month(request.getMonth())
                .roomRent(roomRent)
                .electricity(request.getElectricity() != null ? request.getElectricity() : BigDecimal.ZERO)
                .water(request.getWater() != null ? request.getWater() : BigDecimal.ZERO)
                .extraCost(request.getExtraCost() != null ? request.getExtraCost() : BigDecimal.ZERO)
                .totalAmount(totalAmount)
                .status(InvoiceStatus.UNPAID)
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return InvoiceDTO.fromEntity(savedInvoice);
    }

    /**
     * Cập nhật hóa đơn
     */
    @Transactional
    public InvoiceDTO updateInvoice(Integer invoiceId, InvoiceCreateRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        // Cập nhật các mục
        invoice.setElectricity(request.getElectricity() != null ? request.getElectricity() : BigDecimal.ZERO);
        invoice.setWater(request.getWater() != null ? request.getWater() : BigDecimal.ZERO);
        invoice.setExtraCost(request.getExtraCost() != null ? request.getExtraCost() : BigDecimal.ZERO);
        invoice.setNotes(request.getNotes());

        // Tính lại tổng
        BigDecimal totalAmount = invoice.getRoomRent()
                .add(invoice.getElectricity())
                .add(invoice.getWater())
                .add(invoice.getExtraCost());
        invoice.setTotalAmount(totalAmount);

        invoice.setUpdatedAt(LocalDateTime.now());

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return InvoiceDTO.fromEntity(updatedInvoice);
    }

    /**
     * Cập nhật trạng thái hóa đơn
     */
    @Transactional
    public InvoiceDTO updateInvoiceStatus(Integer invoiceId, String status) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        try {
            invoice.setStatus(InvoiceStatus.valueOf(status));
            invoice.setUpdatedAt(LocalDateTime.now());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return InvoiceDTO.fromEntity(updatedInvoice);
    }

    // ==================== READ ====================

    /**
     * Lấy hóa đơn theo ID
     */
    public InvoiceDTO getInvoiceById(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        return InvoiceDTO.fromEntity(invoice);
    }

    /**
     * Lấy tất cả hóa đơn
     */
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy hóa đơn theo hợp đồng
     */
    public List<InvoiceDTO> getInvoicesByContractId(Integer contractId) {
        return invoiceRepository.findByContractId(contractId)
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy hóa đơn theo phòng
     */
    public List<InvoiceDTO> getInvoicesByRoomId(Integer roomId) {
        return invoiceRepository.findByRoomId(roomId)
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy hóa đơn theo người thuê
     */
    public List<InvoiceDTO> getInvoicesByTenantId(Integer tenantId) {
        return invoiceRepository.findByTenantId(tenantId)
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy hóa đơn theo chủ trọ
     */
    public List<InvoiceDTO> getInvoicesByLandlordId(Integer landlordId) {
        return invoiceRepository.findByLandlordId(landlordId)
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy hóa đơn theo tháng
     */
    public List<InvoiceDTO> getInvoicesByMonth(String month) {
        return invoiceRepository.findByMonth(month)
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lấy hóa đơn theo trạng thái
     */
    public List<InvoiceDTO> getInvoicesByStatus(String status) {
        try {
            InvoiceStatus invoiceStatus = InvoiceStatus.valueOf(status);
            return invoiceRepository.findByStatus(invoiceStatus)
                    .stream()
                    .map(InvoiceDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }
    }

    /**
     * Lấy hóa đơn theo chủ trọ và trạng thái
     */
    public List<InvoiceDTO> getInvoicesByLandlordIdAndStatus(Integer landlordId, String status) {
        try {
            InvoiceStatus invoiceStatus = InvoiceStatus.valueOf(status);
            return invoiceRepository.findByLandlordIdAndStatus(landlordId, invoiceStatus)
                    .stream()
                    .map(InvoiceDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }
    }

    // ==================== DELETE ====================

    /**
     * Xóa hóa đơn
     */
    @Transactional
    public void deleteInvoice(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        invoiceRepository.delete(invoice);
    }
}
