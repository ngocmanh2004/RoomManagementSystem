package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "electricity_records", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"room_id", "month"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectricityRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "room_id", nullable = false)
  private Integer roomId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "old_index", nullable = false)
  private Integer oldIndex;

  @Column(name = "new_index", nullable = false)
  private Integer newIndex;

  @Column(name = "unit_price", nullable = false)
  private Double unitPrice;

  @Column(name = "total_amount", nullable = false)
  private Double totalAmount;

  @Column(name = "meter_photo_url", length = 255)
  private String meterPhotoUrl;

  @Column(nullable = false)
  private String month;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UtilityStatus status = UtilityStatus.UNPAID;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UtilitySource source = UtilitySource.SYSTEM;


  public enum UtilityStatus {
    PAID("Đã thanh toán"),
    UNPAID("Chưa thanh toán");

    private final String displayName;

    UtilityStatus(String displayName) {
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }
  }

  public enum UtilitySource {
    LANDLORD("Chủ trọ nhập"),
    TENANT("Khách thuê gửi"),
    SYSTEM("Hệ thống");

    private final String displayName;

    UtilitySource(String displayName) {
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }
  }
}
