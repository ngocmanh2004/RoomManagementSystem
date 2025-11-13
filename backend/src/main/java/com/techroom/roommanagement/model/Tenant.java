package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "tenants")
@Data
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //@Column(name = "user_id")
    //private int userId;

    @Column(unique = true, length = 20)
    private String cccd;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // ✅ THÊM 2 TRƯỜNG MỚI (từ DB)
    @Column(name = "province_code")
    private Integer provinceCode;

    @Column(name = "district_code")
    private Integer districtCode;

    @Column(length = 255)
    private String address;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}