package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {

    // Đây là phương thức quan trọng để lấy Quận/Huyện theo Tỉnh/Thành
    List<District> findByProvinceCode(Integer provinceCode);
}