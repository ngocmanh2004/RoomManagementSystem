package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR u.username LIKE %:keyword% OR u.fullName LIKE %:keyword% OR u.email LIKE %:keyword%) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:status IS NULL OR u.status = :status)")
    Page<User> searchUsers(@Param("keyword") String keyword,
                           @Param("role") Integer role,
                           @Param("status") User.Status status,
                           Pageable pageable);
    List<User> findAllByRole(int role);

    // Soft delete helper
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    Page<User> findAllActive(Pageable pageable);
}