package com.techroom.roommanagement.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("File rỗng");
        }

        // demo: trả về URL giả
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String url = "http://localhost:8081/uploads/" + fileName;

        return Map.of("url", url);
    }
}

