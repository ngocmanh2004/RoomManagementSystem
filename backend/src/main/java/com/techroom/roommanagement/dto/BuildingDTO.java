package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.Building;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BuildingDTO {
    private int id;
    private String name;
    private String address;
    private String description;
    private Integer provinceCode;
    private Integer districtCode;
    private LocalDateTime createdAt;

    public BuildingDTO(Building building) {
        this.id = building.getId();
        this.name = building.getName();
        this.address = building.getAddress();
        this.description = building.getDescription();
        this.provinceCode = building.getProvinceCode();
        this.districtCode = building.getDistrictCode();
        this.createdAt = building.getCreatedAt();
    }
}