package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.model.Amenity;
import com.techroom.roommanagement.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AmenityController {

    private final AmenityRepository amenityRepository;

    @GetMapping
    public List<Amenity> getAllAmenities() {
        return amenityRepository.findAll();
    }

    @GetMapping("/room/{roomId}")
    public List<Amenity> getAmenitiesByRoom(@PathVariable int roomId) {
        return amenityRepository.findByRoomId(roomId);
    }
}
