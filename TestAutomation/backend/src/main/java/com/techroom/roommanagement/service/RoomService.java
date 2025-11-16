package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.model.Amenity;
import com.techroom.roommanagement.repository.AmenityRepository;
import com.techroom.roommanagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ SỬA IMPORT NÀY

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

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();

        // Force load tất cả lazy collections
        rooms.forEach(room -> {
            room.getImages().size();
            room.getAmenities().size();
        });

        return rooms;
    }

    @Transactional(readOnly = true)
    public Optional<Room> getRoomById(int id) {
        Optional<Room> room = roomRepository.findById(id);
        room.ifPresent(r -> {
            r.getImages().size();
            r.getAmenities().size();
        });
        return room;
    }

    @Transactional(readOnly = true)
    public List<Room> getRoomsByStatus(Room.RoomStatus status) {
        List<Room> rooms = roomRepository.findByStatus(status);
        rooms.forEach(room -> {
            room.getImages().size();
            room.getAmenities().size();
        });
        return rooms;
    }

    @Transactional(readOnly = true)
    public List<Room> searchRooms(String keyword) {
        List<Room> rooms = roomRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        rooms.forEach(room -> {
            room.getImages().size();
            room.getAmenities().size();
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
            room.getImages().size();
            room.getAmenities().size();
        });
        return rooms;
    }

    @Transactional
    public Room saveRoom(Room room) {
        if (room.getAmenities() != null) {
            Set<Amenity> managedAmenities = room.getAmenities().stream()
                    .map(a -> amenityRepository.findById(a.getId()).orElse(a))
                    .collect(Collectors.toSet());
            room.setAmenities(managedAmenities);
        }
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(int id, Room roomDetails) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với id: " + id));

        existingRoom.setName(roomDetails.getName());
        existingRoom.setPrice(roomDetails.getPrice());
        existingRoom.setArea(roomDetails.getArea());
        existingRoom.setStatus(roomDetails.getStatus());
        existingRoom.setDescription(roomDetails.getDescription());

        existingRoom.getAmenities().clear();
        if (roomDetails.getAmenities() != null) {
            roomDetails.getAmenities().forEach(amenity -> {
                Amenity managedAmenity = amenityRepository.findById(amenity.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy tiện ích: " + amenity.getId()));
                existingRoom.getAmenities().add(managedAmenity);
            });
        }

        return roomRepository.save(existingRoom);
    }

    @Transactional
    public Room updateRoomStatus(int id, Room.RoomStatus status) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với id: " + id));

        existingRoom.setStatus(status);
        return roomRepository.save(existingRoom);
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