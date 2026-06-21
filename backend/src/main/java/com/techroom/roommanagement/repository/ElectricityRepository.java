package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.ElectricityRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ElectricityRepository extends JpaRepository<ElectricityRecord, Integer> {

  List<ElectricityRecord> findByMonth(String month);

  List<ElectricityRecord> findByStatus(ElectricityRecord.UtilityStatus status);

  List<ElectricityRecord> findByMonthAndStatus(String month, ElectricityRecord.UtilityStatus status);

  Optional<ElectricityRecord> findByRoomIdAndMonth(Integer roomId, String month);
}
