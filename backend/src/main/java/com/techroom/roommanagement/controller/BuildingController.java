package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.BuildingDTO;
import com.techroom.roommanagement.dto.PageResponseDTO;
import com.techroom.roommanagement.dto.RoomDTO;
import com.techroom.roommanagement.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@CrossOrigin(origins = "http://localhost:4200")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping
    public PageResponseDTO<BuildingDTO> getAllBuildings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BuildingDTO> buildingPage = buildingService.getAllBuildings(pageable)
                .map(BuildingDTO::new);
        
        return new PageResponseDTO<>(
                buildingPage.getContent(),
                buildingPage.getNumber(),
                buildingPage.getSize(),
                buildingPage.getTotalElements(),
                buildingPage.getTotalPages(),
                buildingPage.isLast()
        );
    }

    @GetMapping("/search")
    public List<BuildingDTO> searchBuildings(
            @RequestParam(required = false) Integer provinceCode,
            @RequestParam(required = false) Integer districtCode,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minAcreage,
            @RequestParam(required = false) BigDecimal maxAcreage
    ) {
        return buildingService.searchBuildings(
                provinceCode, districtCode, minPrice, maxPrice, minAcreage, maxAcreage
        ).stream()
                .map(BuildingDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public BuildingDTO getBuildingById(@PathVariable int id) {
        return buildingService.getBuildingById(id)
                .map(BuildingDTO::new)
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + id));
    }

    @GetMapping("/{buildingId}/rooms")
    public PageResponseDTO<RoomDTO> getRoomsByBuilding(
            @PathVariable int buildingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomDTO> roomPage = buildingService.getRoomsByBuildingId(buildingId, pageable)
                .map(RoomDTO::new);
        
        return new PageResponseDTO<>(
                roomPage.getContent(),
                roomPage.getNumber(),
                roomPage.getSize(),
                roomPage.getTotalElements(),
                roomPage.getTotalPages(),
                roomPage.isLast()
        );
    }
    
    // API lấy danh sách building theo landlordId (dùng cho FE dashboard chủ trọ)
    @GetMapping("/by-landlord/{landlordId}")
    public List<BuildingDTO> getBuildingsByLandlord(@PathVariable Integer landlordId) {
        return buildingService.getBuildingsByLandlord(landlordId).stream()
                .map(BuildingDTO::new)
                .toList();
    }
}