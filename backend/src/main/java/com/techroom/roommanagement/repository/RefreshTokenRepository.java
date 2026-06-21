package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Integer userId);
    void deleteByToken(String token);

    @Transactional
    void deleteByUserId(int userId);
}