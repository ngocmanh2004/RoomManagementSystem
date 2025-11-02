package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    // Ví dụ: /api/rooms/search?keyword=Quy%20Nhơn
    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam String keyword) {
        List<Room> rooms = roomService.searchRooms(keyword);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Room>> filterRooms(
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minArea,
            @RequestParam(required = false) Integer maxArea,
            @RequestParam(required = false) List<Integer> amenities
    ) {
        List<Room> rooms = roomService.filterRooms(area, minPrice, maxPrice, type, minArea, maxArea, amenities);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable int id) {
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
