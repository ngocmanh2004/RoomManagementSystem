package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.ExtraCostRequest;
import com.techroom.roommanagement.dto.ExtraCostResponse;
import com.techroom.roommanagement.model.ExtraCost;
import com.techroom.roommanagement.service.ExtraCostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilities/extra-costs")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class ExtraCostController {

  private final ExtraCostService service;

  // ==========================================
  // 1. GET ALL (Hỗ trợ bộ lọc đa năng)
  // URL: /api/utilities/extra-costs?month=2025-12&roomId=1&type=INTERNET
  // ==========================================
  @GetMapping
  public ResponseEntity<List<ExtraCostResponse>> getAll(
    @RequestParam(required = false) String month,
    @RequestParam(required = false) Integer roomId, // ⚠️ Đã sửa thành Integer để khớp với Room.id
    @RequestParam(required = false) ExtraCost.CostType type,
    @RequestParam(required = false) ExtraCost.ExtraCostStatus status
  ) {
    return ResponseEntity.ok(service.getAll(month, roomId, type, status));
  }

  // ==========================================
  // 2. GET BY ID (ID của chi phí vẫn là Long)
  // ==========================================
  @GetMapping("/{id}")
  public ResponseEntity<ExtraCostResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(service.getById(id));
  }

  // ==========================================
  // 3. CREATE (Thêm chi phí mới)
  // ==========================================
  @PostMapping
  public ResponseEntity<ExtraCostResponse> create(@Valid @RequestBody ExtraCostRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
  }

  // ==========================================
  // 4. UPDATE (Sửa chi phí)
  // ==========================================
  @PutMapping("/{id}")
  public ResponseEntity<ExtraCostResponse> update(
    @PathVariable Long id,
    @Valid @RequestBody ExtraCostRequest request
  ) {
    return ResponseEntity.ok(service.update(id, request));
  }

  // ==========================================
  // 5. DELETE (Xóa chi phí)
  // ==========================================
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  // ==========================================
  // 6. MARK PAID (Xác nhận thanh toán)
  // URL: /api/utilities/extra-costs/1/mark-paid
  // ==========================================
  @PutMapping("/{id}/mark-paid")
  public ResponseEntity<Void> markPaid(@PathVariable Long id) {
    service.markPaid(id);
    return ResponseEntity.ok().build();
  }
}
