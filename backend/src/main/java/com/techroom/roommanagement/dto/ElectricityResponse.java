package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.ElectricityRecord;

public class ElectricityResponse {

  private Integer id;
  private Integer roomId;
  private Integer oldIndex;
  private Integer newIndex;
  private Integer usage;
  private Double unitPrice;
  private Double totalAmount;
  private String month;
  private ElectricityRecord.UtilityStatus status;
  private ElectricityRecord.UtilitySource source;

  // Getters and setters
  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }

  public Integer getRoomId() { return roomId; }
  public void setRoomId(Integer roomId) { this.roomId = roomId; }

  public Integer getOldIndex() { return oldIndex; }
  public void setOldIndex(Integer oldIndex) { this.oldIndex = oldIndex; }

  public Integer getNewIndex() { return newIndex; }
  public void setNewIndex(Integer newIndex) { this.newIndex = newIndex; }

  public Integer getUsage() { return usage; }
  public void setUsage(Integer usage) { this.usage = usage; }

  public Double getUnitPrice() { return unitPrice; }
  public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

  public Double getTotalAmount() { return totalAmount; }
  public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

  public String getMonth() { return month; }
  public void setMonth(String month) { this.month = month; }

  public ElectricityRecord.UtilityStatus getStatus() { return status; }
  public void setStatus(ElectricityRecord.UtilityStatus status) { this.status = status; }

  public ElectricityRecord.UtilitySource getSource() { return source; }
  public void setSource(ElectricityRecord.UtilitySource source) { this.source = source; }
}
