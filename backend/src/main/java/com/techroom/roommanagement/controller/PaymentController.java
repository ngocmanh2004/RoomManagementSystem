package com.techroom.roommanagement.controller;

import com.techroom.roommanagement.dto.ResponseDTO;
import com.techroom.roommanagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Payment Controller
 * Xử lý các API endpoints cho thanh toán VNPay
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*") // Cho phép Angular gọi API
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * API 1: Tạo URL thanh toán VNPay
     * Endpoint: POST /api/payments/create
     *
     * Request body: { "invoiceId": 123 }
     * Response: { "success": true, "data": { "paymentUrl": "https://..." } }
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @RequestBody Map<String, Integer> requestBody,
            HttpServletRequest request) {
        try {
            // 1. Lấy invoice ID từ request (sửa: Long → Integer)
            Integer invoiceId = requestBody.get("invoiceId");

            if (invoiceId == null) {
                return ResponseEntity.badRequest()
                        .body(ResponseDTO.error("Invoice ID is required"));
            }

            // 2. Tạo payment URL
            String paymentUrl = paymentService.createPaymentUrl(invoiceId, request);

            // 3. Trả về response
            Map<String, String> data = new HashMap<>();
            data.put("paymentUrl", paymentUrl);

            return ResponseEntity.ok(ResponseDTO.success(data));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.error("Error creating payment: " + e.getMessage()));
        }
    }

    /**
     * API 2: Xử lý callback từ VNPay (ReturnUrl)
     * Endpoint: GET /api/payments/vnpay-return
     *
     * VNPay sẽ redirect user về URL này kèm theo query params
     * Example: /api/payments/vnpay-return?vnp_ResponseCode=00&vnp_TxnRef=...
     */
    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnpayReturn(@RequestParam Map<String, String> params) {
        try {
            Map<String, Object> result = paymentService.processReturn(params);
            return ResponseEntity.ok(ResponseDTO.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.error("Error processing return: " + e.getMessage()));
        }
    }

    /**
     * API 3: Xử lý IPN từ VNPay
     * Endpoint: POST /api/payments/vnpay-ipn
     *
     * VNPay sẽ gọi API này để thông báo kết quả thanh toán
     * Response phải theo format của VNPay: { "RspCode": "00", "Message": "..." }
     */
    @PostMapping("/vnpay-ipn")
    public ResponseEntity<?> vnpayIPN(@RequestParam Map<String, String> params) {
        try {
            Map<String, Object> result = paymentService.processIPN(params);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("RspCode", "99");
            errorResult.put("Message", "Error: " + e.getMessage());
            return ResponseEntity.ok(errorResult);
        }
    }
}