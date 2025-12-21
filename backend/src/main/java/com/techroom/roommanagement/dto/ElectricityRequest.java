package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.ElectricityRecord;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ElectricityRequest {

  @NotNull
  private Integer roomId;

  @NotNull
  @Min(0)
  private Integer oldIndex;

  @NotNull
  @Min(0)
  private Integer newIndex;

  @NotNull
  @Min(0)
  private Double unitPrice;

  @NotNull
  @Pattern(regexp = "\\d{4}-\\d{2}", message = "Month must be in YYYY-MM format")
  private String month;

  private ElectricityRecord.UtilitySource source = ElectricityRecord.UtilitySource.SYSTEM;

  // Getters and setters
  public Integer getRoomId() { return roomId; }
  public void setRoomId(Integer roomId) { this.roomId = roomId; }

  public Integer getOldIndex() { return oldIndex; }
  public void setOldIndex(Integer oldIndex) { this.oldIndex = oldIndex; }

  public Integer getNewIndex() { return newIndex; }
  public void setNewIndex(Integer newIndex) { this.newIndex = newIndex; }

  public Double getUnitPrice() { return unitPrice; }
  public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

  public String getMonth() { return month; }
  public void setMonth(String month) { this.month = month; }

  public ElectricityRecord.UtilitySource getSource() { return source; }
  public void setSource(ElectricityRecord.UtilitySource source) { this.source = source; }
}
