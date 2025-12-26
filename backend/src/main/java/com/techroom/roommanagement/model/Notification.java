package com.techroom.roommanagement.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String message;
    private String type;
    @Column(name = "is_read")
    private Boolean isRead = false;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
