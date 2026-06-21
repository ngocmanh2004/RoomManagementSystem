package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.ExtraCost;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtraCostRepository extends JpaRepository<ExtraCost, Long> {

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

  boolean existsByCode(String code);

  boolean existsByRoomIdAndMonthAndType(
    Integer roomId,
    String month,
    ExtraCost.CostType type
  );

  Optional<Object> findByRoomIdAndMonth(@NotNull Integer roomId, @NotNull(message = "Tháng không được để trống") @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Định dạng tháng phải là YYYY-MM") String month);
}
