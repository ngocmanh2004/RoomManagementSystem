package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    private Integer id;
    private Integer contractId;
    private String contractCode;
    private Integer roomId;
    private String roomName;
    private String tenantName;
    private String month;
    private BigDecimal roomRent;
    private BigDecimal electricity;
    private BigDecimal water;
    private BigDecimal extraCost;
    private BigDecimal totalAmount;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InvoiceDTO fromEntity(Invoice invoice) {
        return InvoiceDTO.builder()
                .id(invoice.getId())
                .contractId(invoice.getContract().getId())
                .contractCode(invoice.getContract().getContractCode())
                .roomId(invoice.getRoomId())
                .roomName(invoice.getRoomName())
                .tenantName(invoice.getTenantName())
                .month(invoice.getMonth())
                .roomRent(invoice.getRoomRent())
                .electricity(invoice.getElectricity())
                .water(invoice.getWater())
                .extraCost(invoice.getExtraCost())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus().name())
                .notes(invoice.getNotes())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }
}
