package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.Amenity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmenityDTO {
    private int id;
    private String name;
    //private String icon;

    public AmenityDTO(Amenity amenity) {
        this.id = amenity.getId();
        this.name = amenity.getName();
    }
}