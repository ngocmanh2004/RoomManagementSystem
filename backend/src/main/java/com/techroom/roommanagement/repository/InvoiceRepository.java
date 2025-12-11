package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    // Lấy hóa đơn theo hợp đồng và tháng
    @Query("SELECT i FROM Invoice i WHERE i.contract.id = :contractId AND i.month = :month")
    Optional<Invoice> findByContractIdAndMonth(@Param("contractId") Integer contractId, @Param("month") String month);

    // Lấy tất cả hóa đơn theo hợp đồng
    @Query("SELECT i FROM Invoice i WHERE i.contract.id = :contractId ORDER BY i.month DESC")
    List<Invoice> findByContractId(@Param("contractId") Integer contractId);

    // Lấy hóa đơn theo phòng và tháng
    @Query("SELECT i FROM Invoice i WHERE i.contract.room.id = :roomId AND i.month = :month")
    Optional<Invoice> findByRoomIdAndMonth(@Param("roomId") Integer roomId, @Param("month") String month);

    // Lấy tất cả hóa đơn theo phòng
    @Query("SELECT i FROM Invoice i WHERE i.contract.room.id = :roomId ORDER BY i.month DESC")
    List<Invoice> findByRoomId(@Param("roomId") Integer roomId);

    // Lấy hóa đơn theo người thuê
    @Query("SELECT i FROM Invoice i WHERE i.contract.tenant.id = :tenantId ORDER BY i.month DESC")
    List<Invoice> findByTenantId(@Param("tenantId") Integer tenantId);

    // Lấy hóa đơn theo chủ trọ
    @Query("SELECT i FROM Invoice i WHERE i.contract.room.building.landlord.id = :landlordId ORDER BY i.month DESC")
    List<Invoice> findByLandlordId(@Param("landlordId") Integer landlordId);

    // Lấy hóa đơn theo trạng thái
    @Query("SELECT i FROM Invoice i WHERE i.status = :status ORDER BY i.month DESC")
    List<Invoice> findByStatus(@Param("status") Invoice.InvoiceStatus status);

    // Lấy tất cả hóa đơn theo trạng thái và chủ trọ
    @Query("SELECT i FROM Invoice i WHERE i.contract.room.building.landlord.id = :landlordId AND i.status = :status ORDER BY i.month DESC")
    List<Invoice> findByLandlordIdAndStatus(@Param("landlordId") Integer landlordId, @Param("status") Invoice.InvoiceStatus status);

    // Lấy tất cả hóa đơn theo tháng
    @Query("SELECT i FROM Invoice i WHERE i.month = :month ORDER BY i.contract.room.name")
    List<Invoice> findByMonth(@Param("month") String month);
}
