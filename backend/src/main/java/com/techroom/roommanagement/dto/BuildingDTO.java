package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.Building;
import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.model.RoomImage;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class BuildingDTO {
    private int id;
    private String name;
    private String address;
    private String description;
    private String imageUrl;
    private Integer provinceCode;
    private Integer districtCode;
    private LocalDateTime createdAt;
    private LandlordInfo landlord;
    private Integer totalRooms;
    private Integer availableRooms;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private List<RoomBasicInfo> rooms;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LandlordInfo {
        private String fullName;
        private String phoneNumber;

        public LandlordInfo(String fullName, String phoneNumber) {
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RoomBasicInfo {
        private Integer id;
        private String status;
        private List<ImageInfo> images;

        public RoomBasicInfo(Integer id, String status, List<ImageInfo> images) {
            this.id = id;
            this.status = status;
            this.images = images;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ImageInfo {
        private String imageUrl;

        public ImageInfo(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public BuildingDTO(Building building) {
        this.id = building.getId();
        this.name = building.getName();
        this.address = building.getAddress();
        this.description = building.getDescription();
        this.imageUrl = building.getImageUrl();
        this.provinceCode = building.getProvinceCode();
        this.districtCode = building.getDistrictCode();
        this.createdAt = building.getCreatedAt();

        // Landlord info
        if (building.getLandlord() != null && building.getLandlord().getUser() != null) {
            this.landlord = new LandlordInfo(
                building.getLandlord().getUser().getFullName(),
                building.getLandlord().getUser().getPhone()
            );
        }

        // Count rooms
        if (building.getRooms() != null) {
            this.totalRooms = building.getRooms().size();
            this.availableRooms = (int) building.getRooms().stream()
                .filter(room -> room.getStatus() == Room.RoomStatus.AVAILABLE)
                .count();

            // Calculate min and max price
            this.minPrice = building.getRooms().stream()
                .filter(room -> room.getPrice() != null)
                .map(Room::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(null);
            
            this.maxPrice = building.getRooms().stream()
                .filter(room -> room.getPrice() != null)
                .map(Room::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(null);

            // Map rooms with images
            this.rooms = building.getRooms().stream()
                .map(room -> new RoomBasicInfo(
                    room.getId(),
                    room.getStatus() != null ? room.getStatus().name() : null,
                    room.getImages() != null ? room.getImages().stream()
                        .map(img -> new ImageInfo(img.getImageUrl()))
                        .collect(Collectors.toList())
                    : List.of()
                ))
                .collect(Collectors.toList());
        } else {
            this.totalRooms = 0;
            this.availableRooms = 0;
            this.minPrice = null;
            this.maxPrice = null;
            this.rooms = List.of();
        }
    }
}