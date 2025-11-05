package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Integer userId);
    void deleteByToken(String token);
}