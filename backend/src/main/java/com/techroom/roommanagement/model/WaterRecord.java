package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_records", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"room_id", "month"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "room_id", nullable = false)
  private Integer roomId;

  @Column(name = "old_index", nullable = false)
  private Integer oldIndex;

  @Column(name = "new_index", nullable = false)
  private Integer newIndex;

  @Column(name = "unit_price", nullable = false)
  private BigDecimal unitPrice;

  @Column(name = "total_amount", nullable = false)
  private BigDecimal totalAmount;

  @Column(name = "month", nullable = false, length = 7)
  private String month;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private UtilityStatus status;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public enum UtilityStatus {
    PAID, UNPAID
  }
}
