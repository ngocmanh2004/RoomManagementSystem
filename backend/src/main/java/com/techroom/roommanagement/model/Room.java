package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import com.techroom.roommanagement.model.Building;

@Entity
@Table(name = "rooms")
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "building_id")
    @JsonIgnoreProperties({"rooms"})
    private Building building;



    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(precision = 5, scale = 2)
    private BigDecimal area;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('AVAILABLE','OCCUPIED','REPAIRING') DEFAULT 'AVAILABLE'")
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Liên kết 1-n với bảng room_images
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("room")
    private List<RoomImage> images;

    public enum RoomStatus {
        AVAILABLE,
        OCCUPIED,
        REPAIRING
    }
}
