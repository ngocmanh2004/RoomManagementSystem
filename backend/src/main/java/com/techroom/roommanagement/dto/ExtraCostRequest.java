package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.ExtraCost;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExtraCostRequest {
  @NotNull(message = "Vui lòng chọn phòng")
  private Integer roomId;

  @NotNull(message = "Vui lòng chọn loại chi phí")
  private ExtraCost.CostType type;

  @NotNull(message = "Số tiền không được để trống")
  @Min(value = 0, message = "Số tiền phải >= 0")
  private BigDecimal amount;

  @NotNull(message = "Tháng không được để trống")
  @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Định dạng tháng phải là YYYY-MM")
  private String month;

  private String description;
}
