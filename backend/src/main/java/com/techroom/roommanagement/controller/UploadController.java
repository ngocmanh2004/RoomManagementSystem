package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.FeedbackUpdateRequest;
import com.techroom.roommanagement.service.FeedbackService;
import com.techroom.roommanagement.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin
public class UploadController {

    private final FileStorageService fileStorageService;
    private final FeedbackService feedbackService;

    @PostMapping
    public Map<String, String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("roomId") Integer roomId
    ) {
        if (file.isEmpty()) {
            throw new RuntimeException("File rỗng");
        }

        // ✅ LƯU FILE THẬT VÀO images/
        String relativePath = fileStorageService.save(file, roomId);

        // ✅ TRẢ URL ĐÚNG
        String url = "http://localhost:8081" + relativePath;

        return Map.of("url", url);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(
            @PathVariable Integer id,
            @RequestBody FeedbackUpdateRequest request
    ) {
        feedbackService.update(id, request);
        return ResponseEntity.ok().build();
    }
}
