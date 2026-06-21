package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    @Query("SELECT t FROM Tenant t WHERE t.cccd = :cccd")
    Optional<Tenant> findByCccd(@Param("cccd") String cccd);

    /**
     * Tìm Tenant từ User ID
     */
    Optional<Tenant> findByUserId(Integer userId);

    /*@Query("SELECT t FROM Tenant t WHERE NOT EXISTS (" +
            "SELECT c FROM Contract c WHERE c.tenant.id = t.id AND c.status = 'ACTIVE')")
    List<Tenant> findTenantsWithoutActiveContract();*/
    // Lấy tất cả tenant KHÔNG CÓ hợp đồng ACTIVE (bao gồm tenant mới chưa có hợp đồng)
    @Query("SELECT t FROM Tenant t WHERE NOT EXISTS (" +
            "SELECT c FROM Contract c WHERE c.tenant.id = t.id AND c.status = 'ACTIVE')")
    List<Tenant> findAvailableTenantsByLandlord(@Param("landlordId") Integer landlordId);


    // Lấy tất cả tenant thuộc landlord (qua hợp đồng và phòng)
    @Query("SELECT DISTINCT t FROM Tenant t " +
           "JOIN Contract c ON c.tenant.id = t.id " +
           "JOIN Room r ON c.room.id = r.id " +
           "JOIN Building b ON r.building.id = b.id " +
           "WHERE b.landlord.id = :landlordId")
    List<Tenant> findAllByLandlordId(@Param("landlordId") Integer landlordId);
}

