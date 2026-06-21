package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.ApiResponse;
import com.techroom.roommanagement.dto.InvoiceCreateRequest;
import com.techroom.roommanagement.dto.InvoiceDTO;
import com.techroom.roommanagement.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // ==================== CREATE ====================

    /**
     * Tạo hóa đơn mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceDTO>> createInvoice(@RequestBody InvoiceCreateRequest request) {
        try {
            InvoiceDTO invoice = invoiceService.createInvoice(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Tạo hóa đơn thành công",
                            invoice
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ==================== READ ====================

    /**
     * Lấy tất cả hóa đơn
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getAllInvoices() {
        try {
            List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách hóa đơn thành công",
                    invoices
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Lấy hóa đơn theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceById(@PathVariable Integer id) {
        try {
            InvoiceDTO invoice = invoiceService.getInvoiceById(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy hóa đơn thành công",
                    invoice
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Lấy hóa đơn theo hợp đồng
     */
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByContractId(@PathVariable Integer contractId) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByContractId(contractId);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách hóa đơn thành công",
                    invoices
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Lấy hóa đơn theo phòng
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByRoomId(@PathVariable Integer roomId) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByRoomId(roomId);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách hóa đơn thành công",
                    invoices
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Lấy hóa đơn theo người thuê
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByTenantId(@PathVariable Integer tenantId) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByTenantId(tenantId);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách hóa đơn thành công",
                    invoices
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Lấy hóa đơn theo chủ trọ
     */
    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByLandlordId(@PathVariable Integer landlordId) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByLandlordId(landlordId);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách hóa đơn thành công",
                    invoices
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Lấy hóa đơn theo tháng
     */
    @GetMapping("/month/{month}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByMonth(@PathVariable String month) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByMonth(month);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách hóa đơn thành công",
                    invoices
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Lấy hóa đơn theo trạng thái
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByStatus(@PathVariable String status) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách hóa đơn thành công",
                    invoices
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Lấy hóa đơn theo chủ trọ và trạng thái
     */
    @GetMapping("/landlord/{landlordId}/status/{status}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getInvoicesByLandlordIdAndStatus(
            @PathVariable Integer landlordId,
            @PathVariable String status) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByLandlordIdAndStatus(landlordId, status);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách hóa đơn thành công",
                    invoices
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ==================== UPDATE ====================

    /**
     * Cập nhật hóa đơn
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> updateInvoice(
            @PathVariable Integer id,
            @RequestBody InvoiceCreateRequest request) {
        try {
            InvoiceDTO invoice = invoiceService.updateInvoice(id, request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Cập nhật hóa đơn thành công",
                    invoice
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * Cập nhật trạng thái hóa đơn
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<InvoiceDTO>> updateInvoiceStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        try {
            InvoiceDTO invoice = invoiceService.updateInvoiceStatus(id, status);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Cập nhật trạng thái hóa đơn thành công",
                    invoice
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ==================== DELETE ====================

    /**
     * Xóa hóa đơn
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(@PathVariable Integer id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Xóa hóa đơn thành công",
                    null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
