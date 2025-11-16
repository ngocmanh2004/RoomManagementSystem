package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_images")
@Getter
@Setter
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnore
    private Room room;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     *  Xử lý cả 2 trường hợp:
     * 1. Nếu imageUrl chỉ là tên file: "room1.jpg" → "/images/1/room1.jpg"
     * 2. Nếu imageUrl đã là full path: "/images/rooms/room1.jpg" → Lấy filename và rebuild
     */
    @JsonProperty("imageUrl")
    public String getFullImageUrl() {
        if (room == null || imageUrl == null) {
            return imageUrl;
        }

        // Lấy tên file (phần sau dấu / cuối cùng)
        String filename = imageUrl;
        if (imageUrl.contains("/")) {
            filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        }

        // Build lại URL đúng
        return "/images/" + room.getId() + "/" + filename;
    }

    /**
     * Setter để lưu chỉ tên file vào DB
     */
    public void setImageUrl(String imageUrl) {
        if (imageUrl != null && imageUrl.contains("/")) {
            // Chỉ lấy tên file
            this.imageUrl = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        } else {
            this.imageUrl = imageUrl;
        }
    }
}