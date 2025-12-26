package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.ExtraCostRequest;
import com.techroom.roommanagement.dto.ExtraCostResponse;
import com.techroom.roommanagement.model.ElectricityRecord;
import com.techroom.roommanagement.model.ExtraCost;
import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.repository.ContractRepository;
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
  private final ContractRepository contractRepository;

  public List<ExtraCostResponse> getAll(
    String month,
    Integer roomId,
    ExtraCost.CostType type,
    ExtraCost.ExtraCostStatus status
  ) {
    if (month != null && month.isEmpty()) month = null;
    if (roomId != null && roomId == 0) roomId = null;

    List<ExtraCost> records = repository.findWithFilters(month, roomId, type, status);
    return records.stream().map(this::toResponse).collect(Collectors.toList());
  }

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

    if (repository.existsByRoomIdAndMonthAndType(
      request.getRoomId(),
      request.getMonth(),
      request.getType()
    )) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Loại chi phí này đã tồn tại trong tháng cho phòng này"
      );
    }

    String roomName = roomRepository.findById(request.getRoomId())
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Room not found"
      ))
      .getName();

    String generatedCode =
      "EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    ExtraCost record = ExtraCost.builder()
      .roomId(request.getRoomId())
      .name(roomName)
      .code(generatedCode)
      .type(request.getType())
      .amount(request.getAmount())
      .month(request.getMonth())
      .description(request.getDescription())
      .status(ExtraCost.ExtraCostStatus.UNPAID)
      .build();

    return toResponse(repository.save(record));
  }

  // ==========================================
  // 4. UPDATE
  // ==========================================
  public ExtraCostResponse update(Long id, ExtraCostRequest request) {
    validateRequest(request);

    ExtraCost record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Không tìm thấy chi phí này"
      ));

    boolean exists = repository.existsByRoomIdAndMonthAndType(
      request.getRoomId(),
      request.getMonth(),
      request.getType()
    );

    if (exists &&
      (!record.getRoomId().equals(request.getRoomId())
        || !record.getMonth().equals(request.getMonth())
        || record.getType() != request.getType())
    ) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Loại chi phí này đã tồn tại trong tháng cho phòng này"
      );
    }

    record.setRoomId(request.getRoomId());
    record.setType(request.getType());
    record.setAmount(request.getAmount());
    record.setMonth(request.getMonth());
    record.setDescription(request.getDescription());

    return toResponse(repository.save(record));
  }


  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chi phí này");
    }
    repository.deleteById(id);
  }

  public void markPaid(Long id) {
    ExtraCost record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chi phí này"));
    record.setStatus(ExtraCost.ExtraCostStatus.PAID);
    repository.save(record);
  }

  // =============================
  // HELPERS
  // =============================
  private ExtraCostResponse toResponse(ExtraCost record) {
    ExtraCostResponse res = new ExtraCostResponse();

    res.setId(record.getId());
    res.setRoomId(record.getRoomId());
    res.setName(record.getName());
    res.setCode(record.getCode());
    res.setType(record.getType());
    res.setTypeName(getFriendlyTypeName(record.getType()));
    res.setAmount(record.getAmount());
    res.setMonth(record.getMonth());
    res.setDescription(record.getDescription());
    res.setStatus(record.getStatus());
    res.setCreatedAt(
      record.getCreatedAt() != null
        ? record.getCreatedAt().toString()
        : null
    );
    contractRepository
      .findActiveTenantFullNameByRoomId(record.getRoomId())
      .ifPresent(res::setFullName);
    return res;
  }

  private void validateRequest(ExtraCostRequest request) {
    if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số tiền không được âm");
    }
  }

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
