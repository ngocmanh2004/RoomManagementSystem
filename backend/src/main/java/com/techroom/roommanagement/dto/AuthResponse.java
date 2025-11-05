package com.techroom.roommanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private UserInfo user;

    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private int id;
        private String username;
        private String fullName;
        private String email;
        private int role;
        private String roleName;
    }
}