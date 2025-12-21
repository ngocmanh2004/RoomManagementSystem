package com.techroom.roommanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {
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
}
