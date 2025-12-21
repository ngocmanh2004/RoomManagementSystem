package com.techroom.roommanagement.service;

import com.techroom.roommanagement.dto.ElectricityResponse;
import com.techroom.roommanagement.dto.WaterRequest;
import com.techroom.roommanagement.dto.WaterResponse;
import com.techroom.roommanagement.model.ElectricityRecord;
import com.techroom.roommanagement.model.WaterRecord;
import com.techroom.roommanagement.model.Room;
import com.techroom.roommanagement.repository.ContractRepository;
import com.techroom.roommanagement.repository.WaterRepository;
import com.techroom.roommanagement.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WaterService {

  private final WaterRepository repository;
  private final RoomRepository roomRepository;
  private final ContractRepository contractRepository;

  public List<WaterResponse> getAll(String month, WaterRecord.UtilityStatus status) {
    List<WaterRecord> records;
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

  public WaterResponse getById(Long id) {
    WaterRecord record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bản ghi không tồn tại"));
    return toResponse(record);
  }


  public WaterResponse create(WaterRequest request) {
    validateRequest(request);

    repository.findByRoomIdAndMonth(request.getRoomId(), request.getMonth())
      .ifPresent(r -> {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng này đã có số liệu nước của tháng này rồi!");
      });

    String roomName = roomRepository.findById(request.getRoomId())
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Room not found"
      ))
      .getName();

    BigDecimal total = calculateTotal(request.getOldIndex(), request.getNewIndex(), request.getUnitPrice());

    WaterRecord record = WaterRecord.builder()
      .roomId(request.getRoomId())
      .name(roomName)
      .oldIndex(request.getOldIndex())
      .newIndex(request.getNewIndex())
      .unitPrice(request.getUnitPrice())
      .month(request.getMonth())
      .status(WaterRecord.UtilityStatus.UNPAID)
      .totalAmount(total)
      .build();

    return toResponse(repository.save(record));
  }

  public WaterResponse update(Long id, WaterRequest request) {
    validateRequest(request);
    WaterRecord record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bản ghi không tồn tại"));

    record.setOldIndex(request.getOldIndex());
    record.setNewIndex(request.getNewIndex());
    record.setUnitPrice(request.getUnitPrice());
    record.setMonth(request.getMonth());

    BigDecimal total = calculateTotal(request.getOldIndex(), request.getNewIndex(), request.getUnitPrice());
    record.setTotalAmount(total);

    return toResponse(repository.save(record));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bản ghi không tồn tại");
    }
    repository.deleteById(id);
  }

  public void markPaid(Long id) {
    WaterRecord record = repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bản ghi không tồn tại"));
    record.setStatus(WaterRecord.UtilityStatus.PAID);
    repository.save(record);
  }

  // --- HELPERS ---
  private WaterResponse toResponse (WaterRecord record) {
    WaterResponse res = new WaterResponse();
    res.setId(record.getId());
    res.setRoomId(record.getRoomId());
    res.setName(record.getName());
    res.setOldIndex(record.getOldIndex());
    res.setNewIndex(record.getNewIndex());
    res.setUsage(record.getNewIndex() - record.getOldIndex());
    res.setUnitPrice(record.getUnitPrice());
    res.setTotalAmount(record.getTotalAmount());
    res.setMonth(record.getMonth());
    res.setStatus(record.getStatus());
    contractRepository
      .findActiveTenantFullNameByRoomId(record.getRoomId())
      .ifPresent(res::setFullName);
    return res;
  }

  private BigDecimal calculateTotal(int oldIndex, int newIndex, BigDecimal unitPrice) {
    if (newIndex < oldIndex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ số mới không được nhỏ hơn chỉ số cũ");
    }
    BigDecimal usage = new BigDecimal(newIndex - oldIndex);
    return unitPrice.multiply(usage);
  }

  private void validateRequest(WaterRequest request) {
    if (request.getOldIndex() < 0 || request.getNewIndex() < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ số phải >= 0");
    }
    if (request.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn giá phải >= 0");
    }
    if (!request.getMonth().matches("\\d{4}-\\d{2}")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tháng phải ở định dạng YYYY-MM");
    }
  }
}
