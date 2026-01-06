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

    @Transactional
    public InvoiceDTO createInvoice(InvoiceCreateRequest request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new RuntimeException("Hợp đồng không tồn tại"));

        invoiceRepository.findByContractIdAndMonth(request.getContractId(), request.getMonth())
                .ifPresent(inv -> {
                    throw new RuntimeException("Hóa đơn cho tháng này đã tồn tại");
                });

        BigDecimal roomRent = contract.getMonthlyRent() != null ? contract.getMonthlyRent() : BigDecimal.ZERO;

        BigDecimal totalAmount = roomRent
                .add(request.getElectricity() != null ? request.getElectricity() : BigDecimal.ZERO)
                .add(request.getWater() != null ? request.getWater() : BigDecimal.ZERO)
                .add(request.getExtraCost() != null ? request.getExtraCost() : BigDecimal.ZERO);

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

    @Transactional
    public InvoiceDTO updateInvoice(Integer invoiceId, InvoiceCreateRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        invoice.setElectricity(request.getElectricity() != null ? request.getElectricity() : BigDecimal.ZERO);
        invoice.setWater(request.getWater() != null ? request.getWater() : BigDecimal.ZERO);
        invoice.setExtraCost(request.getExtraCost() != null ? request.getExtraCost() : BigDecimal.ZERO);
        invoice.setNotes(request.getNotes());

        BigDecimal totalAmount = invoice.getRoomRent()
                .add(invoice.getElectricity())
                .add(invoice.getWater())
                .add(invoice.getExtraCost());
        invoice.setTotalAmount(totalAmount);

        invoice.setUpdatedAt(LocalDateTime.now());

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return InvoiceDTO.fromEntity(updatedInvoice);
    }

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

    public InvoiceDTO getInvoiceById(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        return InvoiceDTO.fromEntity(invoice);
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getInvoicesByContractId(Integer contractId) {
        return invoiceRepository.findByContractId(contractId)
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getInvoicesByRoomId(Integer roomId) {
        return invoiceRepository.findByRoomId(roomId)
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getInvoicesByTenantId(Integer tenantId) {
        return invoiceRepository.findByTenantId(tenantId)
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getInvoicesByLandlordId(Integer landlordId) {
        return invoiceRepository.findByLandlordId(landlordId)
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getInvoicesByMonth(String month) {
        return invoiceRepository.findByMonth(month)
                .stream()
                .map(InvoiceDTO::fromEntity)
                .collect(Collectors.toList());
    }

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

    @Transactional
    public void deleteInvoice(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        invoiceRepository.delete(invoice);
    }
}
