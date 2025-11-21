package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {
    
    boolean existsByTenantUserId(int userId);
    
    boolean existsByTenantIdAndRoomIdAndStatusIn(Integer tenantId, Integer roomId, List<String> statuses);
}