package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.repository.RoomRepository;
import com.techroom.roommanagement.repository.RoomImageRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;

    public RoomController(RoomRepository roomRepository, RoomImageRepository roomImageRepository) {
        this.roomRepository = roomRepository;
        this.roomImageRepository = roomImageRepository;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        for (Room room : rooms) {
            room.setImages(roomImageRepository.findByRoomId(room.getId()));
        }
        return rooms;
    }

    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable int id) {
        Room room = roomRepository.findById(id).orElseThrow();
        room.setImages(roomImageRepository.findByRoomId(id));
        return room;
    }
}
