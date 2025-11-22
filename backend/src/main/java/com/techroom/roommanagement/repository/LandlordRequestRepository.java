package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.LandlordRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LandlordRequestRepository extends JpaRepository<LandlordRequest, Integer> {

    List<LandlordRequest> findByStatus(LandlordRequest.Status status);

    List<LandlordRequest> findAllByOrderByCreatedAtDesc();

    @Query("SELECT r FROM LandlordRequest r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Optional<LandlordRequest> findLatestByUserId(@Param("userId") int userId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM LandlordRequest r " +
            "WHERE r.user.id = :userId AND r.status = :status")
    boolean existsByUserIdAndStatus(@Param("userId") int userId,
                                    @Param("status") LandlordRequest.Status status);

    @Query("SELECT COUNT(r) FROM LandlordRequest r WHERE r.status = :status")
    long countByStatus(@Param("status") LandlordRequest.Status status);
}