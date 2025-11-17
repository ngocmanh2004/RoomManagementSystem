package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.model.Room.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    List<Room> findByStatus(RoomStatus status);

    @Query("SELECT r FROM Room r WHERE " +
            "(:provinceCode IS NULL OR r.building.provinceCode = :provinceCode) AND " +
            "(:districtCode IS NULL OR r.building.districtCode = :districtCode) AND " +
            "(:minPrice IS NULL OR r.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR r.price <= :maxPrice) AND " +
            "(:type IS NULL OR LOWER(r.building.name) LIKE LOWER(CONCAT('%', :type, '%'))) AND " +
            "(:minArea IS NULL OR r.area >= :minArea) AND " +
            "(:maxArea IS NULL OR r.area <= :maxArea)")
    List<Room> filterRooms(
            @Param("provinceCode") Integer provinceCode,
            @Param("districtCode") Integer districtCode,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("type") String type,
            @Param("minArea") Integer minArea,
            @Param("maxArea") Integer maxArea,
            @Param("amenities") List<Integer> amenities
    );
}