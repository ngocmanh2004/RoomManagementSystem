package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.model.RefreshToken;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.security.JwtTokenProvider;
import com.techroom.roommanagement.service.RefreshTokenService;
import com.techroom.roommanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.techroom.roommanagement.dto.AuthResponse;
import com.techroom.roommanagement.dto.LoginRequest;
import com.techroom.roommanagement.dto.RefreshTokenRequest;
import com.techroom.roommanagement.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.authenticate(request.getUsername(), request.getPassword());

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(getRoleName(user.getRole()))
                    .build();

            // Tạo access token
            String accessToken = jwtTokenProvider.generateAccessToken(userDetails);

            // Tạo refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            // Tạo thông tin user
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole(),
                    getRoleName(user.getRole())
            );

            return ResponseEntity.ok(new AuthResponse(
                    accessToken,
                    refreshToken.getToken(),
                    "Bearer",
                    userInfo
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String requestRefreshToken = request.getRefreshToken();

            // Validate refresh token
            if (!jwtTokenProvider.validateRefreshToken(requestRefreshToken)) {
                return ResponseEntity.badRequest().body("Invalid refresh token");
            }

            // Tìm refresh token trong DB
            RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);

            // Kiểm tra expiry
            refreshTokenService.verifyExpiration(refreshToken);

            // Lấy user và tạo access token mới
            User user = refreshToken.getUser();
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(getRoleName(user.getRole()))
                    .build();

            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);

            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole(),
                    getRoleName(user.getRole())
            );

            return ResponseEntity.ok(new AuthResponse(
                    newAccessToken,
                    requestRefreshToken, // Giữ nguyên refresh token
                    "Bearer",
                    userInfo
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
            refreshTokenService.deleteByUserId(refreshToken.getUserId());
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String getRoleName(int role) {
        return switch (role) {
            case 0 -> "ADMIN";
            case 1 -> "LANDLORD";
            case 2 -> "TENANT";
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };
    }
}