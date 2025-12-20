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
public class InvoiceCreateRequest {
    private Integer contractId;
    private String month; // YYYY-MM
    private BigDecimal electricity;
    private BigDecimal water;
    private BigDecimal extraCost;
    private String notes;
}
