
package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.Building;
import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.repository.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    @Transactional(readOnly = true)
    public List<Building> getAllBuildings() {
        List<Building> buildings = buildingRepository.findAll();
        
        // Force load landlord, rooms và images
        buildings.forEach(building -> {
            if (building.getLandlord() != null && building.getLandlord().getUser() != null) {
                building.getLandlord().getUser().getFullName(); // Force load
            }
            if (building.getRooms() != null) {
                building.getRooms().forEach(room -> {
                    if (room.getImages() != null) {
                        room.getImages().size(); // Force load images
                    }
                });
            }
        });
        
        return buildings;
    }

    @Transactional(readOnly = true)
    public Optional<Building> getBuildingById(int id) {
        Optional<Building> building = buildingRepository.findById(id);
        
        // Force load landlord, rooms và images
        building.ifPresent(b -> {
            if (b.getLandlord() != null && b.getLandlord().getUser() != null) {
                b.getLandlord().getUser().getFullName();
            }
            if (b.getRooms() != null) {
                b.getRooms().forEach(room -> {
                    if (room.getImages() != null) {
                        room.getImages().size();
                    }
                });
            }
        });
        
        return building;
    }

    @Transactional(readOnly = true)
    public List<Room> getRoomsByBuildingId(int buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building not found"));

        List<Room> rooms = building.getRooms();

        // Force load images và amenities trong transaction
        rooms.forEach(room -> {
            room.getImages().size();
            room.getAmenities().size();
        });

        return rooms;
    }
    // Lấy danh sách building theo landlordId
    @Transactional(readOnly = true)
    public List<Building> getBuildingsByLandlord(Integer landlordId) {
        return buildingRepository.findByLandlordId(landlordId);
    }
}