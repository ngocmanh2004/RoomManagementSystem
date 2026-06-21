package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LandlordRepository extends JpaRepository<Landlord, Integer> {

    Optional<Landlord> findByUserId(int userId);

    boolean existsByUserId(int userId);
    List<Landlord> findByApproved(Landlord.ApprovalStatus approved);
    long countByApproved(Landlord.ApprovalStatus approved);
}