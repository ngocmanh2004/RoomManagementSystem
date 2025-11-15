package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.model.Province; // IMPORT MỚI
import com.techroom.roommanagement.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provinces")
@CrossOrigin(origins = "http://localhost:4200")
public class ProvinceController {

    @Autowired
    private ProvinceService provinceService;

    /**
     * Lấy TẤT CẢ các tỉnh/thành phố (cho dropdown 1)
     */
    @GetMapping
    public ResponseEntity<List<Province>> getAllProvinces() {
        List<Province> provinces = provinceService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }


    // CÁC ENDPOINT CŨ CỦA BẠN (Vẫn giữ lại)
    /**
     * Lấy danh sách mã tỉnh có phòng trọ
     */
    @GetMapping("/with-rooms")
    public ResponseEntity<List<Integer>> getProvincesWithRooms() {
        List<Integer> provinceCodes = provinceService.getProvincesWithRooms();
        return ResponseEntity.ok(provinceCodes);
    }

    /**
     * Kiểm tra tỉnh có phòng trọ hay không
     */
    @GetMapping("/{code}/has-rooms")
    public ResponseEntity<Boolean> hasRooms(@PathVariable int code) {
        boolean hasRooms = provinceService.provinceHasRooms(code);
        return ResponseEntity.ok(hasRooms);
    }
}