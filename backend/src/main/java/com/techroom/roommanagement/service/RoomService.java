
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
    // Lấy tất cả phòng thuộc landlord
    @Transactional(readOnly = true)
    public List<Room> getRoomsByLandlord(Integer landlordId) {
        List<Room> rooms = roomRepository.findAllByLandlordId(landlordId);
        rooms.forEach(room -> {
            if (room.getImages() != null)
                room.getImages().size();
            if (room.getAmenities() != null)
                room.getAmenities().size();
        });
        return rooms;
    }

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    // Lấy phòng theo landlordId (dùng cho API public dashboard landlord)
    @Transactional(readOnly = true)
    public List<Room> getRoomsByLandlordId(Integer landlordId) {
        return getRoomsByLandlord(landlordId);
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();

        rooms.forEach(room -> {
            if (room.getImages() != null)
                room.getImages().size();
            if (room.getAmenities() != null)
                room.getAmenities().size();
        });

        return rooms;
    }

    @Transactional(readOnly = true)
    public Optional<Room> getRoomById(int id) {
        Optional<Room> room = roomRepository.findById(id);
        room.ifPresent(r -> {
            if (r.getImages() != null)
                r.getImages().size();
            if (r.getAmenities() != null)
                r.getAmenities().size();
        });
        return room;
    }

    @Transactional(readOnly = true)
    public List<Room> getRoomsByStatus(Room.RoomStatus status) {
        List<Room> rooms = roomRepository.findByStatus(status);
        rooms.forEach(room -> {
            if (room.getImages() != null)
                room.getImages().size();
            if (room.getAmenities() != null)
                room.getAmenities().size();
        });
        return rooms;
    }

    @Transactional(readOnly = true)
    public List<Room> searchRooms(String keyword) {
        List<Room> rooms = roomRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        rooms.forEach(room -> {
            if (room.getImages() != null)
                room.getImages().size();
            if (room.getAmenities() != null)
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
            if (room.getImages() != null)
                room.getImages().size();
            if (room.getAmenities() != null)
                room.getAmenities().size();
        });
        return rooms;
    }

    @Transactional
    public Room saveRoom(Room room) {
        if (room.getBuilding() != null && room.getBuilding().getId() != null && room.getBuilding().getId() > 0) {
            Building managedBuilding = buildingRepository.findById(room.getBuilding().getId())
                    .orElseThrow(() -> new RuntimeException("Khong tim thay toa nha voi id: " + room.getBuilding().getId()));
            room.setBuilding(managedBuilding);
        } else if (room.getBuilding() != null && (room.getBuilding().getId() == null || room.getBuilding().getId() == 0)) {
            // Nếu không có id, tạo mới building
            Building newBuilding = new Building();
            newBuilding.setName(room.getBuilding().getName());
            newBuilding.setAddress(room.getBuilding().getAddress());
            newBuilding.setDescription(room.getBuilding().getDescription());
            newBuilding.setProvinceCode(room.getBuilding().getProvinceCode());
            newBuilding.setDistrictCode(room.getBuilding().getDistrictCode());
            // Gán landlord cho building mới
            if (room.getBuilding().getLandlord() != null) {
                newBuilding.setLandlord(room.getBuilding().getLandlord());
            } else {
                throw new RuntimeException("Thiếu landlord khi tạo building mới");
            }
            Building savedBuilding = buildingRepository.save(newBuilding);
            room.setBuilding(savedBuilding);
        }

        if (room.getAmenities() != null && !room.getAmenities().isEmpty()) {
            Set<Amenity> managedAmenities = room.getAmenities().stream()
                    .map(a -> amenityRepository.findById(a.getId())
                            .orElseThrow(() -> new RuntimeException("Khong tim thay tien nghi voi id: " + a.getId())))
                    .collect(Collectors.toSet());
            room.setAmenities(managedAmenities);
        }

        Room savedRoom = roomRepository.save(room);

        if (savedRoom.getImages() != null)
            savedRoom.getImages().size();
        if (savedRoom.getAmenities() != null)
            savedRoom.getAmenities().size();

        return savedRoom;
    }

    @Transactional
    public Room updateRoom(int id, Room roomDetails) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay phong voi id: " + id));

        existingRoom.setName(roomDetails.getName());
        existingRoom.setPrice(roomDetails.getPrice());
        existingRoom.setArea(roomDetails.getArea());
        existingRoom.setStatus(roomDetails.getStatus());
        existingRoom.setDescription(roomDetails.getDescription());

        if (roomDetails.getBuilding() != null && roomDetails.getBuilding().getId() != null && roomDetails.getBuilding().getId() > 0) {
            Building building = buildingRepository.findById(roomDetails.getBuilding().getId())
                    .orElseThrow(() -> new RuntimeException("Khong tim thay toa nha voi id: " + roomDetails.getBuilding().getId()));
            existingRoom.setBuilding(building);
        }

        existingRoom.getAmenities().clear();
        if (roomDetails.getAmenities() != null && !roomDetails.getAmenities().isEmpty()) {
            roomDetails.getAmenities().forEach(amenity -> {
                Amenity managedAmenity = amenityRepository.findById(amenity.getId())
                        .orElseThrow(() -> new RuntimeException("Khong tim thay tien nghi: " + amenity.getId()));
                existingRoom.getAmenities().add(managedAmenity);
            });
        }

        Room savedRoom = roomRepository.save(existingRoom);

        if (savedRoom.getImages() != null)
            savedRoom.getImages().size();
        if (savedRoom.getAmenities() != null)
            savedRoom.getAmenities().size();

        return savedRoom;
    }

    @Transactional
    public Room updateRoomStatus(int id, Room.RoomStatus status) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay phong voi id: " + id));

        existingRoom.setStatus(status);
        Room savedRoom = roomRepository.save(existingRoom);

        if (savedRoom.getImages() != null)
            savedRoom.getImages().size();
        if (savedRoom.getAmenities() != null)
            savedRoom.getAmenities().size();

        return savedRoom;
    }

    @Transactional
    public void deleteRoom(int id) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay phong voi id: " + id));

        if (existingRoom.getStatus() == Room.RoomStatus.OCCUPIED) {
            throw new IllegalStateException("Khong the xoa phong dang co nguoi thue.");
        }

        roomRepository.deleteById(id);
    }
}