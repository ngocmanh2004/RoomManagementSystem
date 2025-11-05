package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.RefreshToken;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.RefreshTokenRepository;
import com.techroom.roommanagement.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Tạo refresh token mới cho user
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Xóa refresh token cũ của user (nếu có) - 1 user chỉ có 1 refresh token
        refreshTokenRepository.deleteByUserId(user.getId());

        // Tạo refresh token mới
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(jwtTokenProvider.generateRefreshToken());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7)); // 7 ngày

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Tìm refresh token theo token string
     */
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    /**
     * Kiểm tra refresh token đã hết hạn chưa
     */
    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please login again");
        }
    }

    /**
     * Xóa refresh token của user (dùng khi logout)
     */
    @Transactional
    public void deleteByUserId(Integer userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * Xóa refresh token theo token string
     */
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}