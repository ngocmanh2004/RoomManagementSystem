package com.techroom.roommanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RejectRequestDTO {
    @NotBlank(message = "Lý do từ chối không được để trống")
    private String reason;
}
