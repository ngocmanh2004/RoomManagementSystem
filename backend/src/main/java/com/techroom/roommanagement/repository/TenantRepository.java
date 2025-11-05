package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByUserId(Long userId);
    Optional<Tenant> findByCccd(String cccd);
}