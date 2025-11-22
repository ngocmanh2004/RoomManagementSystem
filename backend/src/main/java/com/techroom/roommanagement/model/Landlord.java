package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "landlords")
@Data
public class Landlord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "business_license", length = 255)
    private String businessLicense;

    @Column(name = "province_code")
    private Integer provinceCode;

    @Column(name = "district_code")
    private Integer districtCode;

    @Column(length = 255)
    private String address;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approved = ApprovalStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "utility_mode")
    private UtilityMode utilityMode = UtilityMode.LANDLORD_INPUT;

    // Thêm các trường cho đăng ký
    @Column(length = 20)
    private String cccd;

    @Column(name = "front_image_path", length = 255)
    private String frontImagePath;

    @Column(name = "back_image_path", length = 255)
    private String backImagePath;

    @Column(name = "business_license_path", length = 255)
    private String businessLicensePath;

    @Column(name = "expected_room_count")
    private Integer expectedRoomCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Enum mapping với Database
    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum UtilityMode {
        LANDLORD_INPUT, TENANT_SUBMIT
    }
}