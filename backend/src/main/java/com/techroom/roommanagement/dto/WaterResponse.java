package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.WaterRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterResponse {
  private Long id;
  private Integer roomId;
  private String roomName;
  private String tenantName;
  private Integer oldIndex;
  private Integer newIndex;
  private Integer usage;      // Số khối tiêu thụ
  private BigDecimal unitPrice;
  private BigDecimal totalAmount;
  private String month;
  private WaterRecord.UtilityStatus status; // Trả về Enum hoặc String đều được
}
