package com.techroom.roommanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {

    // File sẽ được lưu vào thư mục 'images' cùng cấp với 'src'
    private final Path rootLocation = Paths.get("images");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    /**
     * Lưu ảnh building vào images/buildingId/main.{ext}
     * @param file File tải lên
     * @param buildingId ID của dãy trọ
     * @return Đường dẫn tương đối (ví dụ: /images/1/main.jpg)
     */
    public String saveBuildingImage(MultipartFile file, Integer buildingId) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Tạo thư mục: images/buildingId
            Path buildingDirectory = this.rootLocation.resolve(String.valueOf(buildingId));
            Files.createDirectories(buildingDirectory);

            // Lấy extension
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Đặt tên file là main.{ext}
            String filename = "main" + extension;
            Path destinationFile = buildingDirectory.resolve(filename).normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Trả về đường dẫn: /images/buildingId/main.jpg
            return "/images/" + buildingId + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store building image.", e);
        }
    }

    /**
     * Lưu ảnh room vào images/buildingId/roomId/
     * @param file File tải lên
     * @param buildingId ID của dãy trọ
     * @param roomId ID của phòng
     * @param filename Tên file (main.jpg, detail1.jpg, ...)
     * @return Đường dẫn tương đối (ví dụ: /images/1/2/main.jpg)
     */
    public String saveRoomImage(MultipartFile file, Integer buildingId, Integer roomId, String filename) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Tạo thư mục: images/buildingId/roomId
            Path buildingDirectory = this.rootLocation.resolve(String.valueOf(buildingId));
            Path roomDirectory = buildingDirectory.resolve(String.valueOf(roomId));
            Files.createDirectories(roomDirectory);

            // Lấy extension nếu filename chưa có
            if (!filename.contains(".")) {
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                filename = filename + extension;
            }

            Path destinationFile = roomDirectory.resolve(filename).normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Trả về đường dẫn: /images/buildingId/roomId/filename
            return "/images/" + buildingId + "/" + roomId + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store room image.", e);
        }
    }

    /**
     * Lưu file và trả về đường dẫn tương đối (để lưu vào DB)
     * Phương thức này được giữ lại để backward compatible
     * @param file File tải lên
     * @param buildingId ID của dãy trọ
     * @param roomId ID của phòng để tạo thư mục con
     * @return Đường dẫn tương đối (ví dụ: /images/1/2/abc.jpg)
     */
    public String save(MultipartFile file, Integer buildingId, Integer roomId) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Tạo thư mục theo cấu trúc: images/buildingId/roomId
            Path buildingDirectory = this.rootLocation.resolve(String.valueOf(buildingId));
            Path roomDirectory = buildingDirectory.resolve(String.valueOf(roomId));
            Files.createDirectories(roomDirectory);

            // Tạo tên file duy nhất
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path destinationFile = roomDirectory.resolve(uniqueFilename).normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Trả về đường dẫn: /images/buildingId/roomId/filename
            return "/images/" + buildingId + "/" + roomId + "/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    /**
     * Xóa file khỏi ổ đĩa
     * @param filePath Đường dẫn tương đối từ DB (ví dụ: /images/1/abc.jpg)
     */
    public void delete(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        try {
            // Chuyển đường dẫn (ví dụ: /images/1/abc.jpg) -> (images/1/abc.jpg)
            Path file = this.rootLocation.resolve(filePath.substring(1)).normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + filePath);
            e.fillInStackTrace();
        }
    }
}