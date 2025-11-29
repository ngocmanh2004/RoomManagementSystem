package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Contract;
import com.techroom.roommanagement.model.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {

    // ==================== TENANT QUERIES ====================

    /**
     * US 8.5: Lấy danh sách hợp đồng của tenant
     */
    Page<Contract> findByTenantIdOrderByCreatedAtDesc(Integer tenantId, Pageable pageable);

    Optional<Contract> findByRoomIdAndTenantIdAndStatus(Integer roomId, Integer tenantId, ContractStatus status);

    Optional<Contract> findByIdAndTenantId(Integer id, Integer tenantId);

    // ==================== LANDLORD QUERIES ====================

    /**
     * US 8.6: Lấy danh sách hợp đồng của landlord
     */
    @Query("SELECT c FROM Contract c " +
            "JOIN c.room r " +
            "JOIN r.building b " +
            "WHERE b.landlord.id = :landlordId " +
            "ORDER BY c.createdAt DESC")
    Page<Contract> findByLandlordId(@Param("landlordId") Integer landlordId, Pageable pageable);

    @Query("SELECT c FROM Contract c " +
            "JOIN c.room r " +
            "JOIN r.building b " +
            "WHERE b.landlord.id = :landlordId " +
            "AND c.status = :status " +
            "ORDER BY c.createdAt DESC")
    Page<Contract> findByLandlordIdAndStatus(
            @Param("landlordId") Integer landlordId,
            @Param("status") ContractStatus status,
            Pageable pageable
    );

    @Query("SELECT c FROM Contract c " +
            "JOIN c.room r " +
            "JOIN r.building b " +
            "WHERE c.id = :contractId " +
            "AND b.landlord.id = :landlordId")
    Optional<Contract> findByIdAndLandlordId(
            @Param("contractId") Integer contractId,
            @Param("landlordId") Integer landlordId
    );

    // ==================== ROOM STATUS CHECKS ====================

    /**
     * US 8.4: Check if room has pending/active contract
     */
    boolean existsByRoomIdAndStatus(Integer roomId, ContractStatus status);

    List<Contract> findByRoomId(Integer roomId);

    // ==================== REVIEW SERVICE QUERIES ====================

    /**
     * US 11.2: Check nếu user từng thuê phòng (cho phép review)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contract c " +
            "WHERE c.tenant.user.id = :userId AND c.room.id = :roomId AND c.status IN :statuses")
    boolean existsByTenantIdAndRoomIdAndStatusIn(
            @Param("userId") Integer userId,
            @Param("roomId") Integer roomId,
            @Param("statuses") List<ContractStatus> statuses  //CHANGE: String -> ContractStatus
    );

    // ==================== USER SERVICE QUERIES ====================

    /**
     * US Admin: Check nếu user có contract trước khi xóa
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contract c " +
            "WHERE c.tenant.user.id = :userId")
    boolean existsByTenantUserId(@Param("userId") Integer userId);

    @Query("SELECT c FROM Contract c WHERE c.tenant.id = :tenantId " +
            "AND c.status IN :statuses ORDER BY c.createdAt DESC")
    Optional<Contract> findFirstByTenantIdAndStatusInOrderByCreatedAtDesc(
            @Param("tenantId") Integer tenantId,
            @Param("statuses") List<ContractStatus> statuses
    );

    boolean existsByTenantIdAndStatus(Integer tenantId, ContractStatus status);
}