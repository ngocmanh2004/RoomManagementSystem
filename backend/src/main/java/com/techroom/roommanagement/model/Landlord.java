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

    private Integer provinceCode;
    private Integer districtCode;
    private String address;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approved = ApprovalStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "utility_mode")
    private UtilityMode utilityMode = UtilityMode.LANDLORD_INPUT;

    public enum ApprovalStatus { PENDING, APPROVED, REJECTED }
    public enum UtilityMode { LANDLORD_INPUT, TENANT_SUBMIT }
}