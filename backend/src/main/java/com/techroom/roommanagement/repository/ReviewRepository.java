package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findByRoomIdOrderByCreatedAtDesc(Integer roomId, Pageable pageable);

    Optional<Review> findByRoomIdAndTenantId(Integer roomId, Integer tenantId);

    Page<Review> findByTenantId(Integer tenantId, Pageable pageable);

    long countByRoomId(Integer roomId);
}