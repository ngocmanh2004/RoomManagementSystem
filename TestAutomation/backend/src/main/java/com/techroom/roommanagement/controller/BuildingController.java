package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.BuildingDTO;
import com.techroom.roommanagement.dto.RoomDTO;
import com.techroom.roommanagement.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@CrossOrigin(origins = "http://localhost:4200")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @GetMapping
    public List<BuildingDTO> getAllBuildings() {
        return buildingService.getAllBuildings().stream()
                .map(BuildingDTO::new)
                .toList();
    }
    @GetMapping("/{buildingId}/rooms")
    public List<RoomDTO> getRoomsByBuilding(@PathVariable int buildingId) {
        return buildingService.getRoomsByBuildingId(buildingId).stream()
                .map(RoomDTO::new)
                .toList();
    }
}