package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Getter @Setter @NoArgsConstructor
public class Feedback {

    public enum Status {
        PENDING,
        PROCESSING,
        RESOLVED,
        TENANT_CONFIRMED,
        TENANT_REJECTED,
        CANCELED
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // Khách thuê

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // Chủ trọ

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(name = "attachment_url", length = 255)
    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "landlord_note", columnDefinition = "TEXT")
    private String landlordNote;        // Ghi chú của chủ trọ

    @Column(name = "tenant_feedback", columnDefinition = "TEXT")
    private String tenantFeedback;      // Phản hồi của khách sau xử lý
    @Column(name = "tenant_satisfied")
    private Boolean tenantSatisfied;    // true = hài lòng

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}