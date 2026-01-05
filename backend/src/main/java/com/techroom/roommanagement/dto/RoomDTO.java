package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.Room;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class RoomDTO {
    private int id;
    private String name;
    private BigDecimal price;
    private BigDecimal area;
    private String status;
    private String description;
    private Integer buildingId;
    private String buildingName;
    private String buildingAddress;
    private List<RoomImageDTO> images;
    private Set<AmenityDTO> amenities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.name = room.getName();
        this.price = room.getPrice();
        this.area = room.getArea();
        this.status = room.getStatus() != null ? room.getStatus().name() : null;
        this.description = room.getDescription();

        // Building info
        if (room.getBuilding() != null) {
            this.buildingId = room.getBuilding().getId();
            this.buildingName = room.getBuilding().getName();
            this.buildingAddress = room.getBuilding().getAddress();
        }

        // Convert images - không thêm prefix, để frontend xử lý
        this.images = room.getImages() != null
                ? room.getImages().stream()
                .map(img -> new RoomImageDTO(img))
                .toList()
                : List.of();

        // Convert amenities
        this.amenities = room.getAmenities() != null
                ? room.getAmenities().stream()
                .map(AmenityDTO::new)
                .collect(Collectors.toSet())
                : Set.of();

        this.createdAt = room.getCreatedAt();
        this.updatedAt = room.getUpdatedAt();
    }
}