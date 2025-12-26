package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Payment Repository
 * Quản lý truy vấn database cho Payment
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Tìm payment theo mã giao dịch hệ thống
     */
    Optional<Payment> findByTransactionRef(String transactionRef);

    /**
     * Tìm payment theo mã giao dịch VNPay
     */
    Optional<Payment> findByVnpayTransactionNo(String vnpayTransactionNo);

    /**
     * Tìm tất cả payment của 1 invoice
     */
    List<Payment> findByInvoiceId(Long invoiceId);

    /**
     * Tìm payment theo status
     */
    List<Payment> findByStatus(String status);
}