package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "landlords")
@Data
public class Landlord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "business_license")
    private String businessLicense;

    @Column(name = "province_code")
    private Integer provinceCode;

    @Column(name = "district_code")
    private Integer districtCode;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING'")
    private ApprovalStatus approved = ApprovalStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "utility_mode", columnDefinition = "ENUM('LANDLORD_INPUT','TENANT_SUBMIT') DEFAULT 'LANDLORD_INPUT'")
    private UtilityMode utilityMode = UtilityMode.LANDLORD_INPUT;

    // Enum mapping với Database
    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum UtilityMode {
        LANDLORD_INPUT, TENANT_SUBMIT
    }
}