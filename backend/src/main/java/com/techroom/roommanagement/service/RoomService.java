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

    public List<Room> filterRooms(String area, Double minPrice, Double maxPrice,
                                  String type, Integer minArea, Integer maxArea,
                                  List<Integer> amenities) {
        return roomRepository.filterRooms(area, minPrice, maxPrice, type, minArea, maxArea, amenities);
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public void deleteRoom(int id) {
        roomRepository.deleteById(id);
    }

    public List<String> getDistinctAreas() {
        return roomRepository.findDistinctAreas();
    }

}
