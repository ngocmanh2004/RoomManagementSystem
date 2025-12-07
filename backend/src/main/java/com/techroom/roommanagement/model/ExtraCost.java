package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "extra_costs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraCost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "room_id", nullable = false)
  private Integer roomId;

  @Column(name = "code", nullable = false, unique = true)
  private String code; // Mã chi phí (VD: EXP-123456)

  @Enumerated(EnumType.STRING)
  // 👇 Thêm thuộc tính length = 50 vào đây
  @Column(name = "type", nullable = false, length = 50)
  private CostType type;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "month", nullable = false, length = 7) // YYYY-MM
  private String month;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ExtraCostStatus status;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // --- ENUMS ---
  public enum CostType {
    INTERNET, GARBAGE, MAINTENANCE, OTHERS
  }

  public enum ExtraCostStatus {
    PAID, UNPAID
  }
}
