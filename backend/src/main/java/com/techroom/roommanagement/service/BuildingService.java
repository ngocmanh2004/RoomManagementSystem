
package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.Building;
import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.repository.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
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