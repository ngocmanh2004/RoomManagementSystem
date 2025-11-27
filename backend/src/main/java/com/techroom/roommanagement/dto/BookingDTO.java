package com.techroom.roommanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    
    @NotNull(message = "Room ID là bắt buộc")
    @Positive(message = "Room ID phải lớn hơn 0")
    private Integer roomId;
    
    @NotNull(message = "Ngày bắt đầu là bắt buộc")
    @FutureOrPresent(message = "Ngày bắt đầu không được ở quá khứ")
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @NotNull(message = "Tiền đặt cọc là bắt buộc")
    @Min(value = 0, message = "Tiền đặt cọc không được âm")
    private BigDecimal deposit;
    
    private String notes;
    
    @NotBlank(message = "Họ tên là bắt buộc")
    @Size(min = 3, max = 100, message = "Họ tên phải từ 3-100 ký tự")
    private String fullName;
    
    @NotBlank(message = "CCCD là bắt buộc")
    @Pattern(regexp = "^\\d{9,12}$", message = "CCCD phải là 9-12 chữ số")
    private String cccd;
    
    @NotBlank(message = "Số điện thoại là bắt buộc")
    @Pattern(regexp = "^0\\d{9}$", message = "SĐT phải là 10 chữ số bắt đầu bằng 0")
    private String phone;
    
    @NotBlank(message = "Địa chỉ là bắt buộc")
    @Size(min = 5, max = 255, message = "Địa chỉ phải từ 5-255 ký tự")
    private String address;
}
