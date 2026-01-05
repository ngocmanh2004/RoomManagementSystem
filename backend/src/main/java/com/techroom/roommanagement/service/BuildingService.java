package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.BuildingDTO;
import com.techroom.roommanagement.model.Building;
import com.techroom.roommanagement.model.Landlord;
import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.model.User;
import com.techroom.roommanagement.repository.BuildingRepository;
import com.techroom.roommanagement.repository.LandlordRepository;
import com.techroom.roommanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LandlordRepository landlordRepository;

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
    public Page<Building> getAllBuildings(Pageable pageable) {
        Page<Building> buildingPage = buildingRepository.findAll(pageable);
        
        // Force load landlord, rooms và images
        buildingPage.forEach(building -> {
            if (building.getLandlord() != null && building.getLandlord().getUser() != null) {
                building.getLandlord().getUser().getFullName();
            }
            if (building.getRooms() != null) {
                building.getRooms().forEach(room -> {
                    if (room.getImages() != null) {
                        room.getImages().size();
                    }
                });
            }
        });
        
        return buildingPage;
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

    @Transactional(readOnly = true)
    public Page<Room> getRoomsByBuildingId(int buildingId, Pageable pageable) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building not found"));

        List<Room> allRooms = building.getRooms();

        // Force load images và amenities
        allRooms.forEach(room -> {
            room.getImages().size();
            room.getAmenities().size();
        });

        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allRooms.size());
        
        List<Room> pageContent = allRooms.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(
            pageContent,
            pageable,
            allRooms.size()
        );
    }
    
    // Lấy danh sách building theo landlordId
    @Transactional(readOnly = true)
    public List<Building> getBuildingsByLandlord(Integer landlordId) {
        return buildingRepository.findByLandlordId(landlordId);
    }

    // CRUD Building
    @Transactional
    public Building createBuilding(BuildingDTO dto, String username) {
        // Tìm user từ username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với username: " + username));
        
        // Tìm landlord từ user
        Landlord landlord = landlordRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("User này không phải là chủ trọ"));
        
        Building building = new Building();
        building.setName(dto.getName());
        building.setAddress(dto.getAddress());
        building.setDescription(dto.getDescription());
        building.setProvinceCode(dto.getProvinceCode());
        building.setDistrictCode(dto.getDistrictCode());
        building.setLandlord(landlord);
        
        return buildingRepository.save(building);
    }

    @Transactional
    public Building updateBuilding(Integer id, BuildingDTO dto) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dãy trọ với id: " + id));
        
        building.setName(dto.getName());
        building.setAddress(dto.getAddress());
        building.setDescription(dto.getDescription());
        building.setProvinceCode(dto.getProvinceCode());
        building.setDistrictCode(dto.getDistrictCode());
        
        return buildingRepository.save(building);
    }

    @Transactional
    public void deleteBuilding(Integer id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dãy trọ với id: " + id));
        
        // Kiểm tra xem có phòng nào đang thuê không
        if (building.getRooms() != null && building.getRooms().stream().anyMatch(r -> r.getStatus() == Room.RoomStatus.OCCUPIED)) {
            throw new IllegalStateException("Không thể xóa dãy trọ có phòng đang được thuê");
        }
        
        buildingRepository.deleteById(id);
    }

    // Tìm kiếm và lọc building
    @Transactional(readOnly = true)
    public List<Building> searchBuildings(
            Integer provinceCode, 
            Integer districtCode,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minAcreage,
            BigDecimal maxAcreage
    ) {
        List<Building> buildings = buildingRepository.findAll();
        
        // Force load landlord, rooms và images
        buildings.forEach(building -> {
            if (building.getLandlord() != null && building.getLandlord().getUser() != null) {
                building.getLandlord().getUser().getFullName();
            }
            if (building.getRooms() != null) {
                building.getRooms().forEach(room -> {
                    if (room.getImages() != null) {
                        room.getImages().size();
                    }
                });
            }
        });
        
        // Lọc theo các tiêu chí
        return buildings.stream()
            .filter(building -> {
                // Lọc theo tỉnh
                if (provinceCode != null && !provinceCode.equals(building.getProvinceCode())) {
                    return false;
                }
                
                // Lọc theo quận/huyện
                if (districtCode != null && !districtCode.equals(building.getDistrictCode())) {
                    return false;
                }
                
                // Lọc theo giá và diện tích (kiểm tra ít nhất 1 phòng thỏa mãn)
                if (building.getRooms() != null && !building.getRooms().isEmpty()) {
                    boolean hasMatchingRoom = building.getRooms().stream()
                        .anyMatch(room -> {
                            // Kiểm tra giá
                            if (minPrice != null && (room.getPrice() == null || room.getPrice().compareTo(minPrice) < 0)) {
                                return false;
                            }
                            if (maxPrice != null && (room.getPrice() == null || room.getPrice().compareTo(maxPrice) > 0)) {
                                return false;
                            }
                            
                            // Kiểm tra diện tích
                            if (minAcreage != null && (room.getArea() == null || room.getArea().compareTo(minAcreage) < 0)) {
                                return false;
                            }
                            if (maxAcreage != null && (room.getArea() == null || room.getArea().compareTo(maxAcreage) > 0)) {
                                return false;
                            }
                            
                            return true;
                        });
                    
                    return hasMatchingRoom;
                }
                
                // Nếu không có phòng và có filter giá/diện tích thì loại bỏ
                return minPrice == null && maxPrice == null && minAcreage == null && maxAcreage == null;
            })
            .toList();
    }
}