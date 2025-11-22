package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandlordRepository extends JpaRepository<Landlord, Integer> {
    @Query("SELECT l FROM Landlord l WHERE l.user.id = :userId")
    Optional<Landlord> findByUserId(@Param("userId") int userId);

    List<Landlord> findByApproved(Landlord.ApprovalStatus status);
    // Kiểm tra xem user đã là chủ trọ chưa (dùng cho logic xóa user)
    boolean existsByUserId(int userId);
}