package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.model.RoomImage;
import com.techroom.roommanagement.repository.RoomImageRepository;
import com.techroom.roommanagement.repository.RoomRepository;
import com.techroom.roommanagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomImageController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private RoomImageRepository roomImageRepository;

    @Autowired
    private RoomRepository roomRepository; // Cần để liên kết ảnh với phòng

    /**
     * Tải lên một hoặc nhiều ảnh cho một phòng
     */
    @PostMapping("/room/{roomId}")
    public ResponseEntity<List<RoomImage>> uploadImages(
            @PathVariable int roomId,
            @RequestParam("files") MultipartFile[] files) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với id: " + roomId));

        List<RoomImage> savedImages = new ArrayList<>();
    
        for (MultipartFile file : files) {
            // 1. Lưu file vào ổ đĩa
            String relativePath = fileStorageService.save(file, roomId);

            // 2. Lưu đường dẫn vào CSDL
            RoomImage roomImage = new RoomImage();
            roomImage.setRoom(room);
            roomImage.setImageUrl(relativePath); // Lưu đường dẫn (ví dụ: /uploads/1/abc.jpg)
            roomImage.setCreatedAt(LocalDateTime.now());

            savedImages.add(roomImageRepository.save(roomImage));
        }

        return ResponseEntity.ok(savedImages);
    }

    /**
     * Xóa một ảnh
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable int imageId) {
        RoomImage image = roomImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh với id: " + imageId));

        // 1. Xóa file khỏi ổ đĩa
        fileStorageService.delete(image.getImageUrl());

        // 2. Xóa record khỏi CSDL
        roomImageRepository.delete(image);

        return ResponseEntity.noContent().build();
    }
}