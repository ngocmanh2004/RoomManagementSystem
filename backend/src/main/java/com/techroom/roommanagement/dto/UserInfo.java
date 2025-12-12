package com.techroom.roommanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private String role; // "ADMIN", "LANDLORD", "TENANT"

    // Thêm trường landlord (có thể null nếu không phải landlord)
    private LandlordInfo landlord;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LandlordInfo {
        private Integer id;
        private String cccd;
        private String address;
        private Integer expectedRoomCount;
        private String frontImagePath;
        private String backImagePath;
        private String businessLicensePath;
        private String approved;
        private String utilityMode;
        private String createdAt;
    }
}