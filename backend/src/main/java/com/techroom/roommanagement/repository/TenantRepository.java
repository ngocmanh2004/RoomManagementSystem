package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    @Query("SELECT t FROM Tenant t WHERE t.user.id = :userId")
    Optional<Tenant> findByUserId(@Param("userId") int userId);
    Optional<Tenant> findByCccd(String cccd);

    /**
     * Tìm Tenant từ User ID
     */
    Optional<Tenant> findByUserId(Integer userId);
}

