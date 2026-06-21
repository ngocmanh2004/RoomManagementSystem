package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Payment Entity
 * Lưu trữ thông tin giao dịch thanh toán VNPay
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với Invoice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    // Mã giao dịch của hệ thống (unique)
    @Column(name = "transaction_ref", unique = true, nullable = false)
    private String transactionRef;

    // Mã giao dịch từ VNPay (sau khi thanh toán thành công)
    @Column(name = "vnpay_transaction_no")
    private String vnpayTransactionNo;

    // Số tiền thanh toán
    @Column(name = "amount", nullable = false)
    private Double amount;

    // Trạng thái: PENDING, PAID, FAILED
    @Column(name = "status", nullable = false)
    private String status;

    // Ngày thanh toán thành công
    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    // Ngày tạo giao dịch
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // Ghi chú
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Constructors
    public Payment() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public String getVnpayTransactionNo() {
        return vnpayTransactionNo;
    }

    public void setVnpayTransactionNo(String vnpayTransactionNo) {
        this.vnpayTransactionNo = vnpayTransactionNo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}