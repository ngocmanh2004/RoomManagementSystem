package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.Amenity;
import com.techroom.roommanagement.repository.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmenityService {

    @Autowired
    private AmenityRepository amenityRepository;

    public List<Amenity> getAllAmenities() {
        return amenityRepository.findAll();
    }

    public Amenity createNewAmenity(Amenity amenity) {
        // (Có thể thêm logic kiểm tra trùng tên ở đây)
        if (amenity.getName() == null || amenity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tiện ích không được để trống");
        }
        return amenityRepository.save(amenity);
    }
}