package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.model.Amenity;
import com.techroom.roommanagement.repository.AmenityRepository;
import com.techroom.roommanagement.service.AmenityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@CrossOrigin(origins = "http://localhost:4200")
public class AmenityController {

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private AmenityService amenityService;

    @GetMapping
    public List<Amenity> getAllAmenities() {
        return amenityService.getAllAmenities();
    }

    @GetMapping("/room/{roomId}")
    public List<Amenity> getAmenitiesByRoom(@PathVariable int roomId) {
        return amenityRepository.findByRoomId(roomId);
    }

    @PostMapping
    public ResponseEntity<Amenity> createAmenity(@RequestBody Amenity amenity) {
        try {
            Amenity newAmenity = amenityService.createNewAmenity(amenity);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAmenity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}