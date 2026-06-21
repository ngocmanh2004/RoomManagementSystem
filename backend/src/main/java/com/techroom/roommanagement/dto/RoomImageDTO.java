package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.RoomImage;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class RoomImageDTO {
    private int id;
    private String imageUrl;
    private LocalDateTime createdAt;

    public RoomImageDTO(RoomImage image) {
        this.id = image.getId();
        // Chỉ lấy URL gốc, không gọi getFullImageUrl()
        this.imageUrl = image.getImageUrl();
        this.createdAt = image.getCreatedAt();
    }
}