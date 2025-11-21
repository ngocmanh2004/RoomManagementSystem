package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.*;
import com.techroom.roommanagement.model.RefreshToken;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.security.JwtTokenProvider;
import com.techroom.roommanagement.service.RefreshTokenService;
import com.techroom.roommanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng ký thành công");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration error: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Xác thực user
            User user = userService.authenticate(request.getUsername(), request.getPassword());

            // Tạo UserDetails
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(getRoleName(user.getRole()))
                    .build();

            // ✅ Tạo access token
            String accessToken = jwtTokenProvider.generateAccessToken(userDetails);

            // ✅ Tạo refresh token với username
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            // Tạo user info
            UserInfo userInfo = new UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    getRoleName(user.getRole())
            );

            // Tạo response
            AuthResponse response = new AuthResponse(
                    accessToken,
                    refreshToken.getToken(),
                    userInfo
            );

            logger.info("User {} logged in successfully", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Login error: ", e);
            Map<String, String> error = new HashMap<>();

            if (e.getMessage().contains("bị khóa")) {
                error.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            error.put("message", "Tên đăng nhập hoặc mật khẩu không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshTokenStr = request.getRefreshToken();

            if (refreshTokenStr == null || refreshTokenStr.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Refresh token không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Tìm refresh token trong DB
            RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr);

            // Kiểm tra expiry
            refreshTokenService.verifyExpiration(refreshToken);

            // Lấy user từ userId
            User user = userService.findById(refreshToken.getUserId())
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));

            // Kiểm tra lại status user khi refresh
            if (user.getStatus() == User.Status.BANNED) {
                refreshTokenService.deleteByToken(refreshTokenStr);
                throw new RuntimeException("Tài khoản đã bị khóa");
            }

            // Tạo lại UserDetails để sinh access token mới
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(getRoleName(user.getRole()))
                    .build();

            // Tạo access token mới
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);

            // Rotate refresh token
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

            // Tạo response
            RefreshTokenResponse response = new RefreshTokenResponse(newAccessToken, newRefreshToken.getToken());

            logger.info("Token refreshed and rotated for user: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("Refresh token error: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            logger.error("Unexpected error during token refresh: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Có lỗi xảy ra, vui lòng đăng nhập lại");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        try {
            if (request.getRefreshToken() != null && !request.getRefreshToken().trim().isEmpty()) {
                refreshTokenService.deleteByToken(request.getRefreshToken());
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Đăng xuất thành công");

            logger.info("User logged out successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Logout error: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Đăng xuất thất bại");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Chuyển đổi role number sang role name
     */
    private String getRoleName(int role) {
        return switch (role) {
            case 0 -> "ADMIN";
            case 1 -> "LANDLORD";
            case 2 -> "TENANT";
            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };
    }
}