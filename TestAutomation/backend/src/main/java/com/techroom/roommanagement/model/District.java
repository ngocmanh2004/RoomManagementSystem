package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "districts")
@Getter
@Setter
public class District {

    @Id
    private Integer code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "division_type")
    private String divisionType;

    // Map trực tiếp với cột province_code trong DB
    @Column(name = "province_code", nullable = false)
    private Integer provinceCode;
}