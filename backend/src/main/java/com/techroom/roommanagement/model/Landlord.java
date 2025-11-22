package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "landlords")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Landlord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 20)
    private String cccd;

    @Column(length = 255)
    private String address;

    @Column(name = "expected_room_count")
    private Integer expectedRoomCount;

    @Column(name = "province_code")
    private Integer provinceCode;

    @Column(name = "district_code")
    private Integer districtCode;

    @Column(name = "front_image_path", length = 255)
    private String frontImagePath;

    @Column(name = "back_image_path", length = 255)
    private String backImagePath;

    @Column(name = "business_license_path", length = 255)
    private String businessLicensePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approved = ApprovalStatus.APPROVED;

    @Enumerated(EnumType.STRING)
    @Column(name = "utility_mode", nullable = false)
    private UtilityMode utilityMode = UtilityMode.LANDLORD_INPUT;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public enum ApprovalStatus {
        APPROVED("Đã phê duyệt");

        private final String displayName;

        ApprovalStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum UtilityMode {
        LANDLORD_INPUT("Chủ trọ nhập"),
        TENANT_SUBMIT("Khách thuê gửi");

        private final String displayName;

        UtilityMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}