package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "contract_code", unique = true, length = 20)
    private String contractCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "cccd", nullable = false, length = 20)
    private String cccd;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "deposit", nullable = false, precision = 12, scale = 2)
    private BigDecimal deposit;  // ✅ ĐỔI: Double -> BigDecimal

    @Column(name = "monthly_rent", precision = 12, scale = 2)
    private BigDecimal monthlyRent;  // ✅ ĐỔI: Double -> BigDecimal

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== TRANSIENT FIELDS ====================

    @Transient
    public String getRoomName() {
        return room != null ? room.getName() : "";
    }

    @Transient
    public String getBuildingName() {
        return room != null && room.getBuilding() != null
                ? room.getBuilding().getName() : "";
    }

    @Transient
    public String getLandlordName() {
        if (room != null && room.getBuilding() != null
                && room.getBuilding().getLandlord() != null
                && room.getBuilding().getLandlord().getUser() != null) {
            return room.getBuilding().getLandlord().getUser().getFullName();
        }
        return "";
    }

    @Transient
    public String getLandlordPhone() {
        if (room != null && room.getBuilding() != null
                && room.getBuilding().getLandlord() != null
                && room.getBuilding().getLandlord().getUser() != null) {
            return room.getBuilding().getLandlord().getUser().getPhone();
        }
        return "";
    }

    @Transient
    public BigDecimal getMonthlyRentCalculated() {
        // Ưu tiên monthlyRent từ DB, nếu null thì lấy từ room
        if (monthlyRent != null && monthlyRent.compareTo(BigDecimal.ZERO) > 0) {
            return monthlyRent;
        }
        return room != null && room.getPrice() != null
                ? room.getPrice()
                : BigDecimal.ZERO;
    }

    @Transient
    public BigDecimal getTotalInitialCost() {
        BigDecimal rent = getMonthlyRentCalculated();
        BigDecimal dep = deposit != null ? deposit : BigDecimal.ZERO;
        return rent.add(dep);
    }

    @Transient
    public Integer getDurationMonths() {
        if (startDate != null && endDate != null) {
            return (int) ChronoUnit.MONTHS.between(startDate, endDate);
        }
        return 0;
    }

    @Transient
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "";
    }

    @Transient
    public List<String> getTerms() {
        return Arrays.asList(
                "Thanh toán tiền thuê trước ngày 5 hàng tháng",
                "Không được nuôi thú cưng trong phòng",
                "Giữ gìn vệ sinh chung và riêng",
                "Không được chuyển nhượng hợp đồng cho người khác",
                "Báo trước 30 ngày khi muốn chấm dứt hợp đồng",
                "Chủ trách nhiệm về tài sản trong phòng",
                "Tuân thủ quy định chung của tòa nhà",
                "Không được sử dụng phòng vào mục đích kinh doanh"
        );
    }

    @Transient
    public String getContractCodeGenerated() {
        if (contractCode != null && !contractCode.isEmpty()) {
            return contractCode;
        }
        return id != null ? "HD" + String.format("%07d", id) : "";
    }

    // ==================== LIFECYCLE CALLBACKS ====================

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // Auto-set monthly_rent từ room price nếu chưa có
        if (this.monthlyRent == null && this.room != null) {
            this.monthlyRent = this.room.getPrice();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}