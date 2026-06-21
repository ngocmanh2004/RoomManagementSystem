package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.ElectricityRequest;
import com.techroom.roommanagement.dto.ElectricityResponse;
import com.techroom.roommanagement.model.ElectricityRecord;
import com.techroom.roommanagement.service.ElectricityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utilities/electric")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class ElectricityController {

  private final ElectricityService service;

  // GET all với filter
  @GetMapping
  public List<ElectricityResponse> getAll(
    @RequestParam(required = false) String month,
    @RequestParam(required = false) ElectricityRecord.UtilityStatus status
  ) {
    return service.getAll(month, status);
  }

  @GetMapping("/{id}")
  public ElectricityResponse getById(@PathVariable Integer id) {
    return service.getById(id);
  }

  @PostMapping
  public ElectricityResponse create(@Valid @RequestBody ElectricityRequest request) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public ElectricityResponse update(
    @PathVariable Integer id,
    @Valid @RequestBody ElectricityRequest request
  ) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    service.delete(id);
  }

  @PutMapping("/{id}/mark-paid")
  public void markPaid(@PathVariable Integer id) {
    service.markPaid(id);
  }

  @RestControllerAdvice
  public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
      return ResponseEntity.status(ex.getStatusCode())
        .body(Map.of("message", ex.getReason()));
    }
  }

}
