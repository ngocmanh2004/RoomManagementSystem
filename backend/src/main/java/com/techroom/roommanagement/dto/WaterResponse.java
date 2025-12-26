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
  private String name;
  private String fullName;
  private Integer oldIndex;
  private Integer newIndex;
  private Integer usage;
  private BigDecimal unitPrice;
  private BigDecimal totalAmount;
  private String month;
  private WaterRecord.UtilityStatus status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getRoomId() {
    return roomId;
  }

  public void setRoomId(Integer roomId) {
    this.roomId = roomId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Integer getOldIndex() {
    return oldIndex;
  }

  public void setOldIndex(Integer oldIndex) {
    this.oldIndex = oldIndex;
  }

  public Integer getNewIndex() {
    return newIndex;
  }

  public void setNewIndex(Integer newIndex) {
    this.newIndex = newIndex;
  }

  public Integer getUsage() {
    return usage;
  }

  public void setUsage(Integer usage) {
    this.usage = usage;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getMonth() {
    return month;
  }

  public void setMonth(String month) {
    this.month = month;
  }

  public WaterRecord.UtilityStatus getStatus() {
    return status;
  }

  public void setStatus(WaterRecord.UtilityStatus status) {
    this.status = status;
  }
}
