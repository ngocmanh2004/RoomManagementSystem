package com.techroom.roommanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TerminateContractRequest {

    @NotNull(message = "Loại thanh lý không được để trống")
    private TerminationType terminationType;

    private String reason; // Lý do chi tiết (optional)

    public enum TerminationType {
        EXPIRED,      // Hết hạn đúng hạn
        CANCELLED     // Chấm dứt sớm
    }
}