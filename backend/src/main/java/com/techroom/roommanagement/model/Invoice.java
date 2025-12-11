package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"contract_id", "month"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(name = "month", nullable = false, length = 7)
    private String month; // YYYY-MM

    @Column(name = "room_rent", nullable = false, precision = 12, scale = 2)
    private BigDecimal roomRent; // Tiền phòng

    @Column(name = "electricity", nullable = false, precision = 12, scale = 2)
    private BigDecimal electricity; // Tiền điện

    @Column(name = "water", nullable = false, precision = 12, scale = 2)
    private BigDecimal water; // Tiền nước

    @Column(name = "extra_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal extraCost; // Chi phí khác (internet, dịch vụ, v.v.)

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount; // Tổng cộng

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ENUM ====================
    public enum InvoiceStatus {
        UNPAID("Chưa thanh toán"),
        PAID("Đã thanh toán"),
        OVERDUE("Quá hạn");

        private final String displayName;

        InvoiceStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // ==================== TRANSIENT FIELDS ====================
    @Transient
    public String getRoomName() {
        return contract != null && contract.getRoom() != null
                ? contract.getRoom().getName() : "";
    }

    @Transient
    public String getTenantName() {
        return contract != null && contract.getTenant() != null
                ? contract.getTenant().getUser().getFullName() : "";
    }

    @Transient
    public String getBuildingName() {
        return contract != null && contract.getRoom() != null
                && contract.getRoom().getBuilding() != null
                ? contract.getRoom().getBuilding().getName() : "";
    }

    @Transient
    public Integer getRoomId() {
        return contract != null && contract.getRoom() != null
                ? contract.getRoom().getId() : null;
    }
}
