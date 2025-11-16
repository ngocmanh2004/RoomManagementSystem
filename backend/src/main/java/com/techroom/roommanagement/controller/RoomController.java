package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.RoomDTO;
import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus; // <-- THÊM IMPORT NÀY
import java.util.Map; // <-- THÊM IMPORT NÀY

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public List<RoomDTO> getAllRooms() {
        return roomService.getAllRooms().stream()
                .map(RoomDTO::new)
                .toList();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam String keyword) {
        List<Room> rooms = roomService.searchRooms(keyword);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Room>> filterRooms(
            @RequestParam(required = false) Integer provinceCode,
            @RequestParam(required = false) Integer districtCode,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minArea,
            @RequestParam(required = false) Integer maxArea,
            @RequestParam(required = false) List<Integer> amenities
    ) {
        List<Room> rooms = roomService.filterRooms(provinceCode, districtCode, minPrice, maxPrice, type, minArea, maxArea, amenities);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable int id) {
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint này có thể xóa (nhưng vẫn giữ theo yêu cầu của bạn)
    // @GetMapping("/areas")
    // public ResponseEntity<List<String>> getAllAreas() {
    //     List<String> areas = roomService.getDistinctAreas();
    //     return ResponseEntity.ok(areas);
    // }

    // ===========================================
    // THÊM CÁC ENDPOINT MỚI CHO SPRINT 1
    // ===========================================

    /**
     * US 1.1: Thêm phòng trọ
     */
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        if (room.getStatus() == null) {
            room.setStatus(Room.RoomStatus.AVAILABLE);
        }
        Room newRoom = roomService.saveRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRoom);
    }

    /**
     * US 1.2: Chỉnh sửa phòng trọ
     */
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable int id, @RequestBody Room roomDetails) {
        try {
            Room updatedRoom = roomService.updateRoom(id, roomDetails);
            return ResponseEntity.ok(updatedRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * US 1.4: Cập nhật trạng thái phòng
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateRoomStatus(@PathVariable int id, @RequestBody Map<String, String> statusUpdate) {
        try {
            String statusString = statusUpdate.get("status");
            Room.RoomStatus status = Room.RoomStatus.valueOf(statusString.toUpperCase());
            Room updatedRoom = roomService.updateRoomStatus(id, status);
            return ResponseEntity.ok(updatedRoom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Trạng thái không hợp lệ"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * US 1.3: Xóa phòng trọ
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable int id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) { // Lỗi 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) { // Lỗi 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }
}