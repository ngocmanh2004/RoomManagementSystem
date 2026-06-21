package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.model.Building;
import com.techroom.roommanagement.repository.BuildingRepository;
import com.techroom.roommanagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/buildings")
@CrossOrigin(origins = "http://localhost:4200")
public class BuildingImageController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private BuildingRepository buildingRepository;

    /**
     * Upload ảnh đại diện cho building
     * Endpoint: POST /api/buildings/{buildingId}/image
     */
    @PostMapping("/{buildingId}/image")
    public ResponseEntity<Map<String, String>> uploadBuildingImage(
            @PathVariable Integer buildingId,
            @RequestParam("file") MultipartFile file) {

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dãy trọ với id: " + buildingId));

        // Xóa ảnh cũ nếu có
        if (building.getImageUrl() != null && !building.getImageUrl().isEmpty()) {
            fileStorageService.delete(building.getImageUrl());
        }

        // Lưu ảnh mới vào images/buildingId/main.{ext}
        String imagePath = fileStorageService.saveBuildingImage(file, buildingId);

        // Cập nhật đường dẫn ảnh vào DB
        building.setImageUrl(imagePath);
        buildingRepository.save(building);

        return ResponseEntity.ok(Map.of(
                "message", "Upload ảnh thành công",
                "imageUrl", imagePath
        ));
    }

    /**
     * Xóa ảnh đại diện của building
     * Endpoint: DELETE /api/buildings/{buildingId}/image
     */
    @DeleteMapping("/{buildingId}/image")
    public ResponseEntity<Map<String, String>> deleteBuildingImage(@PathVariable Integer buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dãy trọ với id: " + buildingId));

        if (building.getImageUrl() == null || building.getImageUrl().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Dãy trọ chưa có ảnh"));
        }

        // Xóa file khỏi ổ đĩa
        fileStorageService.delete(building.getImageUrl());

        // Xóa đường dẫn trong DB
        building.setImageUrl(null);
        buildingRepository.save(building);

        return ResponseEntity.ok(Map.of("message", "Xóa ảnh thành công"));
    }
}
