package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandlordRepository extends JpaRepository<Landlord, Integer> {
    // Kiểm tra xem user đã là chủ trọ chưa (dùng cho logic xóa user)
    boolean existsByUserId(int userId);
}