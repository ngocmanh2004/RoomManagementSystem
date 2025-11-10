package com.techroom.roommanagement.dto;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import java.time.LocalDate;

@Data
@Getter
@Setter
public class RegisterRequest {
    // Thông tin Users
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private Integer role;

    // Thông tin Tenants
    private String address;
    private String cccd;
    private LocalDate dateOfBirth;
}