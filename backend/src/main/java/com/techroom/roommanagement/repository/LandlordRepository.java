package com.techroom.roommanagement.repository;
import com.techroom.roommanagement.model.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandlordRepository extends JpaRepository<Landlord, Integer> {
    boolean existsByUserId(int userId);
}