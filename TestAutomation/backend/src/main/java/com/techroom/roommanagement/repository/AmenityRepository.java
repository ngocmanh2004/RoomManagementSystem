package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer> {

    @Query(value = """
        SELECT a.* FROM amenities a
        JOIN room_amenities ra ON a.id = ra.amenity_id
        WHERE ra.room_id = :roomId
    """, nativeQuery = true)
    List<Amenity> findByRoomId(@Param("roomId") int roomId);
}
