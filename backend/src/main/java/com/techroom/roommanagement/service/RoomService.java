package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.model.Building;
import com.techroom.roommanagement.model.Amenity;
import com.techroom.roommanagement.repository.AmenityRepository;
import com.techroom.roommanagement.repository.RoomRepository;
import com.techroom.roommanagement.repository.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();

        // Force load tất cả lazy collections
        rooms.forEach(room -> {
            if (room.getImages() != null) room.getImages().size();
            if (room.getAmenities() != null) room.getAmenities().size();
        });

        return rooms;
    }

    @Transactional(readOnly = true)
    public Optional<Room> getRoomById(int id) {
        Optional<Room> room = roomRepository.findById(id);
        room.ifPresent(r -> {
            if (r.getImages() != null) r.getImages().size();
            if (r.getAmenities() != null) r.getAmenities().size();
        });
        return room;
    }

    @Transactional(readOnly = true)
    public List<Room> getRoomsByStatus(Room.RoomStatus status) {
        List<Room> rooms = roomRepository.findByStatus(status);
        rooms.forEach(room -> {
            if (room.getImages() != null) room.getImages().size();
            if (room.getAmenities() != null) room.getAmenities().size();
        });
        return rooms;
    }

    @Transactional(readOnly = true)
    public List<Room> searchRooms(String keyword) {
        List<Room> rooms = roomRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        rooms.forEach(room -> {
            if (room.getImages() != null) room.getImages().size();
            if (room.getAmenities() != null) room.getAmenities().size();
        });
        return rooms;
    }

    @Transactional(readOnly = true)
    public List<Room> filterRooms(Integer provinceCode, Integer districtCode,
                                  Double minPrice, Double maxPrice,
                                  String type, Integer minArea, Integer maxArea,
                                  List<Integer> amenities) {
        List<Room> rooms = roomRepository.filterRooms(provinceCode, districtCode, minPrice, maxPrice, type, minArea, maxArea, amenities);
        rooms.forEach(room -> {
            if (room.getImages() != null) room.getImages().size();
            if (room.getAmenities() != null) room.getAmenities().size();
        });
        return rooms;
    }

    @Transactional
    public Room saveRoom(Room room) {
        // Xử lý Building
        if (room.getBuilding() != null && room.getBuilding().getId() != 0) {
            Building managedBuilding = buildingRepository.findById(room.getBuilding().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tòa nhà với id: " + room.getBuilding().getId()));
            room.setBuilding(managedBuilding);
        }

        // Xử lý Amenities
        if (room.getAmenities() != null && !room.getAmenities().isEmpty()) {
            Set<Amenity> managedAmenities = room.getAmenities().stream()
                    .map(a -> amenityRepository.findById(a.getId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy tiện ích với id: " + a.getId())))
                    .collect(Collectors.toSet());
            room.setAmenities(managedAmenities);
        }

        Room savedRoom = roomRepository.save(room);

        // Force load để tránh lỗi lazy loading khi return
        if (savedRoom.getImages() != null) savedRoom.getImages().size();
        if (savedRoom.getAmenities() != null) savedRoom.getAmenities().size();

        return savedRoom;
    }

    @Transactional
    public Room updateRoom(int id, Room roomDetails) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với id: " + id));

        // Cập nhật thông tin cơ bản
        existingRoom.setName(roomDetails.getName());
        existingRoom.setPrice(roomDetails.getPrice());
        existingRoom.setArea(roomDetails.getArea());
        existingRoom.setStatus(roomDetails.getStatus());
        existingRoom.setDescription(roomDetails.getDescription());

        // Cập nhật Building
        if (roomDetails.getBuilding() != null && roomDetails.getBuilding().getId() != 0) {
            Building building = buildingRepository.findById(roomDetails.getBuilding().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tòa nhà với id: " + roomDetails.getBuilding().getId()));
            existingRoom.setBuilding(building);
        }

        // Cập nhật Amenities
        existingRoom.getAmenities().clear();
        if (roomDetails.getAmenities() != null && !roomDetails.getAmenities().isEmpty()) {
            roomDetails.getAmenities().forEach(amenity -> {
                Amenity managedAmenity = amenityRepository.findById(amenity.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy tiện ích: " + amenity.getId()));
                existingRoom.getAmenities().add(managedAmenity);
            });
        }

        Room savedRoom = roomRepository.save(existingRoom);

        // Force load để tránh lỗi lazy loading
        if (savedRoom.getImages() != null) savedRoom.getImages().size();
        if (savedRoom.getAmenities() != null) savedRoom.getAmenities().size();

        return savedRoom;
    }

    @Transactional
    public Room updateRoomStatus(int id, Room.RoomStatus status) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với id: " + id));

        existingRoom.setStatus(status);
        Room savedRoom = roomRepository.save(existingRoom);

        // Force load
        if (savedRoom.getImages() != null) savedRoom.getImages().size();
        if (savedRoom.getAmenities() != null) savedRoom.getAmenities().size();

        return savedRoom;
    }

    @Transactional
    public void deleteRoom(int id) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với id: " + id));

        if (existingRoom.getStatus() == Room.RoomStatus.OCCUPIED) {
            throw new IllegalStateException("Không thể xóa phòng đang có người thuê.");
        }

        roomRepository.deleteById(id);
    }
}