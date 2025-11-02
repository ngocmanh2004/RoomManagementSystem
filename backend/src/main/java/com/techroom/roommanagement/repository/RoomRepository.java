package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByStatus(Room.RoomStatus status);

    List<Room> findByNameContainingIgnoreCase(String keyword);

    List<Room> findByPriceBetween(double min, double max);
}
