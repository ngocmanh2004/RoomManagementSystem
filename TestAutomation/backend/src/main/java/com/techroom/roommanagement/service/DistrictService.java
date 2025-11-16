package com.techroom.roommanagement.service;

import com.techroom.roommanagement.model.District;
import com.techroom.roommanagement.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistrictService {

    @Autowired
    private DistrictRepository districtRepository;

    public List<District> getDistrictsByProvince(Integer provinceCode) {
        return districtRepository.findByProvinceCode(provinceCode);
    }
}