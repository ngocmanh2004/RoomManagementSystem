package com.techroom.roommanagement.service;

import com.techroom.roommanagement.config.VNPayConfig;
import com.techroom.roommanagement.model.Invoice;
import com.techroom.roommanagement.model.Payment;
import com.techroom.roommanagement.repository.InvoiceRepository;
import com.techroom.roommanagement.repository.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Payment Service
 * Xử lý logic thanh toán VNPay
 */
@Service
public class PaymentService {

    @Autowired
    private VNPayConfig vnPayConfig;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public String createPaymentUrl(Integer invoiceId, HttpServletRequest request) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        String txnRef = "INV" + invoiceId + "_" + System.currentTimeMillis();

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setTransactionRef(txnRef);
        payment.setAmount(invoice.getTotalAmount().doubleValue());
        payment.setStatus("PENDING");
        payment.setCreatedAt(new Date());
        paymentRepository.save(payment);

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());

        long amount = invoice.getTotalAmount()
                .multiply(new BigDecimal("100"))
                .longValue();
        vnpParams.put("vnp_Amount", String.valueOf(amount));

        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", txnRef);

        String orderInfo =
                "Payment invoice " + invoice.getId() +
                        " month " + invoice.getMonth().replace("-", "");
        vnpParams.put("vnp_OrderInfo", orderInfo);

        vnpParams.put("vnp_OrderType", "billpayment");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());

        String clientIp = getClientIp(request);
        vnpParams.put("vnp_IpAddr", clientIp);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        vnpParams.put("vnp_CreateDate", sdf.format(cal.getTime()));
        cal.add(Calendar.MINUTE, 15);
        vnpParams.put("vnp_ExpireDate", sdf.format(cal.getTime()));

        String queryString = vnPayConfig.getQueryString(vnpParams);
        String secureHash = vnPayConfig.hmacSHA512(
                vnPayConfig.getHashSecret(),
                queryString
        );

        String paymentUrl = vnPayConfig.getVnpayUrl()
                + "?" + queryString
                + "&vnp_SecureHash=" + secureHash;

        return paymentUrl;
    }

    @Transactional
    public Map<String, Object> processReturn(Map<String, String> vnpParams) {
        Map<String, Object> result = new HashMap<>();

        try {
            String secureHash = vnpParams.get("vnp_SecureHash");
            vnpParams.remove("vnp_SecureHashType");
            vnpParams.remove("vnp_SecureHash");

            String signValue = vnPayConfig.hmacSHA512(
                    vnPayConfig.getHashSecret(),
                    vnPayConfig.getQueryString(vnpParams)
            );

            if (!signValue.equals(secureHash)) {
                result.put("success", false);
                result.put("message", "Invalid signature");
                return result;
            }

            String txnRef = vnpParams.get("vnp_TxnRef");
            String responseCode = vnpParams.get("vnp_ResponseCode");

            Payment payment = paymentRepository.findByTransactionRef(txnRef)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            if ("00".equals(responseCode)) {
                payment.setStatus("PAID");
                payment.setPaymentDate(new Date());

                Invoice invoice = payment.getInvoice();
                invoice.updateStatusToPaid();
                invoiceRepository.save(invoice);

                result.put("success", true);
            } else {
                payment.setStatus("FAILED");
                result.put("success", false);
            }

            paymentRepository.save(payment);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip) || (ip != null && ip.contains(":"))) {
            ip = "127.0.0.1";
        }

        return ip;
    }
    @Transactional
    public Map<String, Object> processIPN(Map<String, String> vnpParams) {

        Map<String, Object> result = new HashMap<>();

        try {
            String secureHash = vnpParams.get("vnp_SecureHash");
            vnpParams.remove("vnp_SecureHashType");
            vnpParams.remove("vnp_SecureHash");

            String signValue = vnPayConfig.hmacSHA512(
                    vnPayConfig.getHashSecret(),
                    vnPayConfig.getQueryString(vnpParams)
            );

            if (!signValue.equals(secureHash)) {
                result.put("RspCode", "97");
                result.put("Message", "Invalid signature");
                return result;
            }

            String txnRef = vnpParams.get("vnp_TxnRef");
            String responseCode = vnpParams.get("vnp_ResponseCode");
            String transactionNo = vnpParams.get("vnp_TransactionNo");
            String amount = vnpParams.get("vnp_Amount");

            Payment payment = paymentRepository.findByTransactionRef(txnRef)
                    .orElse(null);

            if (payment == null) {
                result.put("RspCode", "01");
                result.put("Message", "Order not found");
                return result;
            }

            long expectedAmount = (long) (payment.getAmount() * 100);
            if (!String.valueOf(expectedAmount).equals(amount)) {
                result.put("RspCode", "04");
                result.put("Message", "Invalid amount");
                return result;
            }

            if ("PAID".equals(payment.getStatus())) {
                result.put("RspCode", "02");
                result.put("Message", "Order already confirmed");
                return result;
            }

            if ("00".equals(responseCode)) {
                payment.setStatus("PAID");
                payment.setVnpayTransactionNo(transactionNo);
                payment.setPaymentDate(new Date());

                Invoice invoice = payment.getInvoice();
                invoice.updateStatusToPaid();
                invoiceRepository.save(invoice);
            } else {
                payment.setStatus("FAILED");
            }

            paymentRepository.save(payment);

            result.put("RspCode", "00");
            result.put("Message", "Confirm success");

        } catch (Exception e) {
            result.put("RspCode", "99");
            result.put("Message", "Unknown error");
        }

        return result;
    }

}
