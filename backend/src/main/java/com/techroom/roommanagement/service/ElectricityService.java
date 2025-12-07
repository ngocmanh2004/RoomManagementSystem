package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.ElectricityRequest;
import com.techroom.roommanagement.dto.ElectricityResponse;
import com.techroom.roommanagement.model.ElectricityRecord;
import com.techroom.roommanagement.repository.ElectricityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectricityService {

  private final ElectricityRepository repository;

  // Get all with optional filter
  public List<ElectricityResponse> getAll(String month, ElectricityRecord.UtilityStatus status) {
    List<ElectricityRecord> records;

    if (month != null && status != null) {
      records = repository.findByMonthAndStatus(month, status);
    } else if (month != null) {
      records = repository.findByMonth(month);
    } else if (status != null) {
      records = repository.findByStatus(status);
    } else {
      records = repository.findAll();
    }

    return records.stream().map(this::toResponse).collect(Collectors.toList());
  }

  public ElectricityResponse getById(Integer id) {
    ElectricityRecord record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    return toResponse(record);
  }

  public ElectricityResponse create(ElectricityRequest request) {
    validateRequest(request);

    // Check duplicate room + month
    repository.findByRoomIdAndMonth(request.getRoomId(), request.getMonth())
      .ifPresent(r -> {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record for this room and month already exists");
      });

    ElectricityRecord record = ElectricityRecord.builder()
      .roomId(request.getRoomId())
      .oldIndex(request.getOldIndex())
      .newIndex(request.getNewIndex())
      .unitPrice(request.getUnitPrice())
      .month(request.getMonth())
      .source(request.getSource())
      .status(ElectricityRecord.UtilityStatus.UNPAID)
      .totalAmount(calculateTotal(request.getOldIndex(), request.getNewIndex(), request.getUnitPrice()))
      .build();

    return toResponse(repository.save(record));
  }

  public ElectricityResponse update(Integer id, ElectricityRequest request) {
    validateRequest(request);

    ElectricityRecord record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));

    record.setOldIndex(request.getOldIndex());
    record.setNewIndex(request.getNewIndex());
    record.setUnitPrice(request.getUnitPrice());
    record.setMonth(request.getMonth());
    record.setSource(request.getSource());
    record.setTotalAmount(calculateTotal(request.getOldIndex(), request.getNewIndex(), request.getUnitPrice()));

    return toResponse(repository.save(record));
  }

  public void delete(Integer id) {
    ElectricityRecord record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    repository.delete(record);
  }

  public void markPaid(Integer id) {
    ElectricityRecord record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    record.setStatus(ElectricityRecord.UtilityStatus.PAID);
    repository.save(record);
  }

  // =============================
  // Helpers
  // =============================
  private ElectricityResponse toResponse(ElectricityRecord record) {
    ElectricityResponse res = new ElectricityResponse();
    res.setId(record.getId());
    res.setRoomId(record.getRoomId());
    res.setOldIndex(record.getOldIndex());
    res.setNewIndex(record.getNewIndex());
    res.setUsage(record.getNewIndex() - record.getOldIndex());
    res.setUnitPrice(record.getUnitPrice());
    res.setTotalAmount(record.getTotalAmount());
    res.setMonth(record.getMonth());
    res.setStatus(record.getStatus());
    res.setSource(record.getSource());
    return res;
  }

  private double calculateTotal(int oldIndex, int newIndex, double unitPrice) {
    if (newIndex < oldIndex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ số mới không được nhỏ hơn chỉ số cũ");
    }
    return (newIndex - oldIndex) * unitPrice;
  }

  private void validateRequest(ElectricityRequest request) {
    if (request.getOldIndex() < 0 || request.getNewIndex() < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ số phải >= 0");
    }
    if (request.getUnitPrice() < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn giá phải >= 0");
    }
    if (!request.getMonth().matches("\\d{4}-\\d{2}")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Month phải ở định dạng YYYY-MM");
    }
  }
}
