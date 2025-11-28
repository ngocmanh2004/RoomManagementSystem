package com.techroom.roommanagement.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectContractDTO {

    @NotNull(message = "Room ID không được để trống")
    private Integer roomId;

    @NotNull(message = "Tenant ID không được để trống")
    private Integer tenantId;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String fullName;

    @NotBlank(message = "CCCD không được để trống")
    @Size(max = 20, message = "CCCD không được quá 20 ký tự")
    private String cccd;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được quá 255 ký tự")
    private String address;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Tiền cọc không được để trống")
    @DecimalMin(value = "0.0", message = "Tiền cọc phải lớn hơn hoặc bằng 0")
    private BigDecimal deposit;

    private String notes;
}