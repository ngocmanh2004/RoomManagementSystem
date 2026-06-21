package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.WaterRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaterRepository extends JpaRepository<WaterRecord, Long> {

  List<WaterRecord> findByMonth(String month);

  List<WaterRecord> findByStatus(WaterRecord.UtilityStatus status);

  List<WaterRecord> findByMonthAndStatus(String month, WaterRecord.UtilityStatus status);

  Optional<WaterRecord> findByRoomIdAndMonth(Integer roomId, String month);
}
