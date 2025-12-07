package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.ExtraCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtraCostRepository extends JpaRepository<ExtraCost, Long> {

  // Query tìm kiếm linh động (Nếu tham số null thì bỏ qua điều kiện đó)
  @Query("SELECT e FROM ExtraCost e WHERE " +
    "(:month IS NULL OR e.month = :month) AND " +
    "(:roomId IS NULL OR e.roomId = :roomId) AND " +
    "(:type IS NULL OR e.type = :type) AND " +
    "(:status IS NULL OR e.status = :status) " +
    "ORDER BY e.month DESC, e.createdAt DESC")
  List<ExtraCost> findWithFilters(
    String month,
    Integer roomId,
    ExtraCost.CostType type,
    ExtraCost.ExtraCostStatus status
  );

  // Kiểm tra trùng mã code (để sinh mã unique)
  boolean existsByCode(String code);
}
