package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "landlord_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandlordRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 20, nullable = false)
    private String cccd;

    @Column(length = 255, nullable = false)
    private String address;

    @Column(name = "expected_room_count", nullable = false)
    private Integer expectedRoomCount;

    @Column(name = "province_code")
    private Integer provinceCode;

    @Column(name = "district_code")
    private Integer districtCode;

    @Column(name = "front_image_path", length = 255, nullable = false)
    private String frontImagePath;

    @Column(name = "back_image_path", length = 255, nullable = false)
    private String backImagePath;

    @Column(name = "business_license_path", length = 255, nullable = false)
    private String businessLicensePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public enum Status {
        PENDING("Chờ duyệt"),
        APPROVED("Đã duyệt"),
        REJECTED("Từ chối");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}