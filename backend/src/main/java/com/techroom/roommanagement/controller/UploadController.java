/*package com.techroom.roommanagement.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class UploadController {

    @PostMapping
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("File rỗng");
        }

        // demo: trả về URL giả
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String url = "http://localhost:8081/images/" + fileName;

        return Map.of("url", url);
    }
}*/
package com.techroom.roommanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    // Thư mục lưu ảnh thực tế
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/images/";

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File rỗng");
        }

        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file KHÔNG TRÙNG
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Lưu file thật
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL public phù hợp với MvcConfig
        String fileUrl = "/images/" + fileName;

        System.out.println("Saved image: " + filePath.toAbsolutePath());

        return ResponseEntity.ok().body(Map.of("url", fileUrl));
    }
}

