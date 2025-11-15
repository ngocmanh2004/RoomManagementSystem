package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.model.Amenity;
import com.techroom.roommanagement.repository.AmenityRepository;
import com.techroom.roommanagement.repository.RoomRepository;
// import com.techroom.roommanagement.repository.ContractRepository; // Sẽ cần khi bạn có file này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; // <-- THÊM IMPORT NÀY

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    // @Autowired
    // private ContractRepository contractRepository; // <-- Sẽ cần cho US 1.3 (cách nâng cao)

    // ===========================================
    // CÁC HÀM CŨ (GIỮ NGUYÊN)
    // ===========================================
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(int id) {
        return roomRepository.findById(id);
    }

    public List<Room> getRoomsByStatus(Room.RoomStatus status) {
        return roomRepository.findByStatus(status);
    }

    public List<Room> searchRooms(String keyword) {
        return roomRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    public List<Room> filterRooms(Integer provinceCode, Integer districtCode,
                                  Double minPrice, Double maxPrice,
                                  String type, Integer minArea, Integer maxArea,
                                  List<Integer> amenities) {
        return roomRepository.filterRooms(provinceCode, districtCode, minPrice, maxPrice, type, minArea, maxArea, amenities);
    }

    // ===========================================
    // CÁC HÀM MỚI VÀ CẬP NHẬT CHO SPRINT 1
    // ===========================================

    @Transactional // <-- CẬP NHẬT (Thêm @Transactional)
    public Room saveRoom(Room room) {
        // Dùng cho US 1.1 (Thêm mới)
        if (room.getAmenities() != null) {
            Set<Amenity> managedAmenities = room.getAmenities().stream()
                    .map(a -> amenityRepository.findById(a.getId()).orElse(a))
                    .collect(Collectors.toSet());
            room.setAmenities(managedAmenities);
        }
        return roomRepository.save(room);
    }

    @Transactional // <-- THÊM MỚI
    public Room updateRoom(int id, Room roomDetails) {
        // Dùng cho US 1.2 (Chỉnh sửa)
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với id: " + id));

        existingRoom.setName(roomDetails.getName());
        existingRoom.setPrice(roomDetails.getPrice());
        existingRoom.setArea(roomDetails.getArea());
        existingRoom.setStatus(roomDetails.getStatus());
        existingRoom.setDescription(roomDetails.getDescription());

        existingRoom.getAmenities().clear(); // Xóa các tiện ích cũ
        if (roomDetails.getAmenities() != null) {
            // Thêm các tiện ích mới
            roomDetails.getAmenities().forEach(amenity -> {
                Amenity managedAmenity = amenityRepository.findById(amenity.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy tiện ích: " + amenity.getId()));
                existingRoom.getAmenities().add(managedAmenity);
            });
        }

        return roomRepository.save(existingRoom);
    }

    @Transactional // <-- THÊM MỚI
    public Room updateRoomStatus(int id, Room.RoomStatus status) {
        // Dùng cho US 1.4 (Cập nhật trạng thái)
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với id: " + id));

        existingRoom.setStatus(status);
        return roomRepository.save(existingRoom);
    }

    @Transactional // <-- CẬP NHẬT (Thêm @Transactional và logic)
    public void deleteRoom(int id) {
        // Dùng cho US 1.3 (Xóa)
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với id: " + id));

        // US 1.3: Kiểm tra điều kiện: nếu phòng đang có người thuê → không cho xóa.
        if (existingRoom.getStatus() == Room.RoomStatus.OCCUPIED) {
            throw new IllegalStateException("Không thể xóa phòng đang có người thuê.");
        }

        // (Nếu bạn có ContractRepository, logic kiểm tra ở đây sẽ tốt hơn)

        roomRepository.deleteById(id);
    }
}