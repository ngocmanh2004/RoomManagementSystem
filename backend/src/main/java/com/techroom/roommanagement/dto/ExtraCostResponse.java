package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.ExtraCost;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExtraCostResponse {
  private Long id;
  private Integer roomId;
  private String roomName;   // Lấy từ bảng Room
  private String tenantName; // Lấy từ bảng Room
  private String code;
  private ExtraCost.CostType type;
  private String typeName;   // Tên hiển thị (Internet, Rác...)
  private BigDecimal amount;
  private String month;
  private String description;
  private ExtraCost.ExtraCostStatus status;
  private String createdAt;
}
