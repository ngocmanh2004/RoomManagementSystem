package com.techroom.roommanagement.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequest {
    // Thông tin Users
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private Integer role; // 1=ADMIN, 2=LANDLORD, 3=TENANT


    private String address;
    private String cccd;
    private LocalDate dateOfBirth;
    private Integer provinceCode;
    private Integer districtCode;
}