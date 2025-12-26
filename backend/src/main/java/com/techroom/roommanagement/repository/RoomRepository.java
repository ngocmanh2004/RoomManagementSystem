package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    List<Room> findByStatus(Room.RoomStatus status);

    List<Room> findByBuildingId(Integer buildingId);

        // Lấy tất cả phòng thuộc về landlord
        @Query("SELECT r FROM Room r JOIN r.building b WHERE b.landlord.id = :landlordId")
        List<Room> findAllByLandlordId(@Param("landlordId") Integer landlordId);

    @Query("SELECT DISTINCT r FROM Room r " +
            "LEFT JOIN r.building b " +
            "LEFT JOIN r.amenities a " +
            "WHERE (:provinceCode IS NULL OR b.provinceCode = :provinceCode) " +
            "AND (:districtCode IS NULL OR b.districtCode = :districtCode) " +
            "AND (:minPrice IS NULL OR r.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR r.price <= :maxPrice) " +
            "AND (:minArea IS NULL OR r.area >= :minArea) " +
            "AND (:maxArea IS NULL OR r.area <= :maxArea) " +
            "AND (:amenities IS NULL OR a.id IN :amenities) " +
            "AND r.status = com.techroom.roommanagement.model.Room.RoomStatus.AVAILABLE")
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

    @Query("SELECT CASE WHEN r.status = com.techroom.roommanagement.model.Room.RoomStatus.AVAILABLE THEN true ELSE false END " +
            "FROM Room r WHERE r.id = :roomId")
    boolean isRoomAvailable(@Param("roomId") Integer roomId);

    List<Room> findByBuildingLandlordIdAndStatus(
            Integer landlordId,
            Room.RoomStatus status
    );
}