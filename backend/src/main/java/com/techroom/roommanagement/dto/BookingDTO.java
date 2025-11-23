package com.techroom.roommanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Integer roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double deposit;
    private String notes;
    
    // Thông tin người thuê (auto-fill từ profile, cho phép chỉnh sửa)
    private String fullName;
    private String cccd;
    private String phone;
    private String address;
}
