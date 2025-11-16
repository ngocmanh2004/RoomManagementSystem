package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Thêm import này
import org.springframework.stereotype.Repository;

import java.util.List; // <-- Thêm import này

@Repository
public interface BuildingRepository extends JpaRepository<Building, Integer> {

    // THÊM 2 DÒNG NÀY VÀO
    @Query("SELECT DISTINCT b.provinceCode FROM Building b WHERE b.provinceCode IS NOT NULL")
    List<Integer> findDistinctProvinceCodes();

    boolean existsByProvinceCode(Integer provinceCode);

    // ... (Các hàm khác của bạn nếu có) ...
}