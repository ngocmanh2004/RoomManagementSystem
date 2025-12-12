package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.ExtraCostRequest;
import com.techroom.roommanagement.dto.ExtraCostResponse;
import com.techroom.roommanagement.model.ExtraCost;
import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.repository.ExtraCostRepository;
import com.techroom.roommanagement.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExtraCostService {

  private final ExtraCostRepository repository;
  private final RoomRepository roomRepository;

  // ==========================================
  // 1. GET ALL (Bộ lọc đa năng)
  // ==========================================
  public List<ExtraCostResponse> getAll(
    String month,
    Integer roomId, // ⚠️ Sửa thành Integer để khớp với Room.id
    ExtraCost.CostType type,
    ExtraCost.ExtraCostStatus status
  ) {
    // Xử lý chuỗi rỗng và số 0 từ frontend
    if (month != null && month.isEmpty()) month = null;
    if (roomId != null && roomId == 0) roomId = null;

    List<ExtraCost> records = repository.findWithFilters(month, roomId, type, status);
    return records.stream().map(this::toResponse).collect(Collectors.toList());
  }

  // ==========================================
  // 2. GET BY ID (ID của ExtraCost vẫn là Long)
  // ==========================================
  public ExtraCostResponse getById(Long id) {
    ExtraCost record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chi phí này"));
    return toResponse(record);
  }

  // ==========================================
  // 3. CREATE
  // ==========================================
  public ExtraCostResponse create(ExtraCostRequest request) {
    validateRequest(request);

    // Tự động sinh mã chi phí (VD: EXP-A1B2C3D4)
    String generatedCode = "EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    ExtraCost record = ExtraCost.builder()
      .roomId(request.getRoomId()) // Integer
      .code(generatedCode)
      .type(request.getType())
      .amount(request.getAmount())
      .month(request.getMonth())
      .description(request.getDescription())
      .status(ExtraCost.ExtraCostStatus.UNPAID) // Mặc định chưa thanh toán
      .build();

    return toResponse(repository.save(record));
  }

  // ==========================================
  // 4. UPDATE
  // ==========================================
  public ExtraCostResponse update(Long id, ExtraCostRequest request) {
    validateRequest(request);

    ExtraCost record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chi phí này"));

    // Cập nhật thông tin
    record.setRoomId(request.getRoomId());
    record.setType(request.getType());
    record.setAmount(request.getAmount());
    record.setMonth(request.getMonth());
    record.setDescription(request.getDescription());

    return toResponse(repository.save(record));
  }

  // ==========================================
  // 5. DELETE
  // ==========================================
  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chi phí này");
    }
    repository.deleteById(id);
  }

  // ==========================================
  // 6. MARK PAID (Đánh dấu đã thanh toán)
  // ==========================================
  public void markPaid(Long id) {
    ExtraCost record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chi phí này"));
    record.setStatus(ExtraCost.ExtraCostStatus.PAID);
    repository.save(record);
  }

  // =============================
  // HELPERS
  // =============================

  // Map Entity -> Response DTO
  private ExtraCostResponse toResponse(ExtraCost record) {
    // Mặc định
    String roomName = "Phòng " + record.getRoomId();
    String tenantName = "---";

    // Lấy tên phòng từ RoomRepository
    if (record.getRoomId() != null) {
      // RoomRepository.findById nhận Integer
      Room room = roomRepository.findById(record.getRoomId()).orElse(null);
      if (room != null) {
        roomName = room.getName();
        // ⚠️ BỎ tenantName vì Room model không có trường này
        // Nếu muốn có tên khách, sau này cần join bảng Contract
      }
    }

    return ExtraCostResponse.builder()
      .id(record.getId())
      .roomId(record.getRoomId()) // Integer
      .roomName(roomName)
      .tenantName(tenantName)
      .code(record.getCode())
      .type(record.getType())
      .typeName(getFriendlyTypeName(record.getType()))
      .amount(record.getAmount())
      .month(record.getMonth())
      .description(record.getDescription())
      .status(record.getStatus())
      .createdAt(record.getCreatedAt() != null ? record.getCreatedAt().toString() : null)
      .build();
  }

  // Validate logic nghiệp vụ
  private void validateRequest(ExtraCostRequest request) {
    if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số tiền không được âm");
    }
  }

  // Convert Enum sang tên hiển thị (nếu cần dùng ở BE)
  private String getFriendlyTypeName(ExtraCost.CostType type) {
    switch (type) {
      case INTERNET: return "Internet / Wifi";
      case GARBAGE: return "Rác thải";
      case MAINTENANCE: return "Bảo trì";
      case OTHERS: return "Khác";
      default: return type.name();
    }
  }
}
