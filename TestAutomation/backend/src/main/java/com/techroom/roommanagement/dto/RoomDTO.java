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
    private List<RoomImageDTO> images;
    private Set<AmenityDTO> amenities;
    private LocalDateTime createdAt;

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
        }

        // Convert images và build full URL ngay tại đây
        this.images = room.getImages() != null
                ? room.getImages().stream()
                .map(img -> {
                    RoomImageDTO dto = new RoomImageDTO(img);
                    // Build full URL
                    String filename = dto.getImageUrl();
                    if (filename != null && !filename.startsWith("/")) {
                        dto.setImageUrl("/images/" + room.getId() + "/" + filename);
                    }
                    return dto;
                })
                .toList()
                : List.of();

        // Convert amenities
        this.amenities = room.getAmenities() != null
                ? room.getAmenities().stream()
                .map(AmenityDTO::new)
                .collect(Collectors.toSet())
                : Set.of();

        this.createdAt = room.getCreatedAt();
    }
}