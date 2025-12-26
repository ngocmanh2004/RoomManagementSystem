package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.WaterRequest;
import com.techroom.roommanagement.dto.WaterResponse;
import com.techroom.roommanagement.model.WaterRecord;
import com.techroom.roommanagement.service.WaterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilities/water")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class WaterController {

  private final WaterService service;

  // GET all với filter (Tháng, Trạng thái)
  @GetMapping
  public List<WaterResponse> getAll(
    @RequestParam(required = false) String month,
    @RequestParam(required = false) WaterRecord.UtilityStatus status
  ) {
    return service.getAll(month, status);
  }

  // GET by ID
  // ⚠️ Lưu ý: Sử dụng Long thay vì Integer để khớp với Service/Entity mới
  @GetMapping("/{id}")
  public WaterResponse getById(@PathVariable Long id) {
    return service.getById(id);
  }

  // CREATE
  @PostMapping
  public WaterResponse create(@Valid @RequestBody WaterRequest request) {
    return service.create(request);
  }

  // UPDATE
  @PutMapping("/{id}")
  public WaterResponse update(
    @PathVariable Long id,
    @Valid @RequestBody WaterRequest request
  ) {
    return service.update(id, request);
  }

  // DELETE
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  // MARK PAID
  @PutMapping("/{id}/mark-paid")
  public void markPaid(@PathVariable Long id) {
    service.markPaid(id);
  }
}
