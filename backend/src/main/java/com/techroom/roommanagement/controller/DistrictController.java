package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.model.District;
import com.techroom.roommanagement.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/districts")
@CrossOrigin(origins = "http://localhost:4200")
public class DistrictController {

    @Autowired
    private DistrictService districtService;

    /**
     * API lấy danh sách Quận/Huyện theo Tỉnh/Thành
     * Ví dụ: GET /api/districts/by-province/1
     */
    @GetMapping("/by-province/{provinceCode}")
    public ResponseEntity<List<District>> getDistrictsByProvince(@PathVariable Integer provinceCode) {
        List<District> districts = districtService.getDistrictsByProvince(provinceCode);
        return ResponseEntity.ok(districts);
    }
}