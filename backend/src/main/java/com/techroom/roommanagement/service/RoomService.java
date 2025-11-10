package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;


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

    // SỬA LẠI THAM SỐ
    public List<Room> filterRooms(Integer provinceCode, Integer districtCode,
                                  Double minPrice, Double maxPrice,
                                  String type, Integer minArea, Integer maxArea,
                                  List<Integer> amenities) {
        return roomRepository.filterRooms(provinceCode, districtCode, minPrice, maxPrice, type, minArea, maxArea, amenities);
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public void deleteRoom(int id) {
        roomRepository.deleteById(id);
    }

    // Bạn có thể xóa hàm này vì 'distinct areas' không còn dùng nữa
    // public List<String> getDistinctAreas() {
    //     return roomRepository.findDistinctAreas();
    // }
}