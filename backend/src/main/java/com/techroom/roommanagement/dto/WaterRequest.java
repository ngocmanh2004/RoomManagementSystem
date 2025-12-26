package com.techroom.roommanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterRequest {

  @NotNull
  private Integer roomId;

  @NotNull(message = "Chỉ số cũ không được để trống")
  @Min(value = 0, message = "Chỉ số cũ phải >= 0")
  private Integer oldIndex;

  @NotNull(message = "Chỉ số mới không được để trống")
  @Min(value = 0, message = "Chỉ số mới phải >= 0")
  private Integer newIndex;

  @NotNull(message = "Đơn giá không được để trống")
  @Min(value = 0, message = "Đơn giá phải >= 0")
  private BigDecimal unitPrice;

  @NotNull(message = "Tháng không được để trống")
  @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Định dạng tháng phải là YYYY-MM")
  private String month;
}
