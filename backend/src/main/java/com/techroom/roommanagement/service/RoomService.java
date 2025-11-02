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
        return roomRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Room> filterByPrice(double min, double max) {
        return roomRepository.findByPriceBetween(min, max);
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public void deleteRoom(int id) {
        roomRepository.deleteById(id);
    }
}
