package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.Province; // IMPORT MỚI
import com.techroom.roommanagement.repository.BuildingRepository;
import com.techroom.roommanagement.repository.ProvinceRepository; // IMPORT MỚI
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceService {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ProvinceRepository provinceRepository; // THÊM MỚI

    /**
     * Lấy TẤT CẢ Tỉnh/Thành phố để hiển thị
     */
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }

    // CÁC HÀM CŨ CỦA BẠN (Vẫn giữ lại)
    /**
     * Lấy danh sách mã tỉnh có ít nhất 1 phòng trọ
     */
    public List<Integer> getProvincesWithRooms() {
        return buildingRepository.findDistinctProvinceCodes();
    }

    /**
     * Kiểm tra tỉnh có phòng trọ hay không
     */
    public boolean provinceHasRooms(int provinceCode) {
        return buildingRepository.existsByProvinceCode(provinceCode);
    }
}