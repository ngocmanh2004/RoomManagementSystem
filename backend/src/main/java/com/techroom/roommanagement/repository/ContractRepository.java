package com.techroom.roommanagement.repository;
import com.techroom.roommanagement.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Integer> {
    boolean existsByTenantUserId(int userId);
}