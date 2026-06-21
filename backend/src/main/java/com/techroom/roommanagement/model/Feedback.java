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

    public enum FeedbackType {
        MAINTENANCE,
        SUGGESTION,
        COMPLAINT
    }
    @Enumerated(EnumType.STRING)
    private FeedbackType type;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender; // Khách thuê

    @ManyToOne
    @JoinColumn(name = "receiver_id")
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

    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;

    @Column(columnDefinition = "TEXT")
    private String landlordNote;        // Ghi chú của chủ trọ

    @Column(columnDefinition = "TEXT")
    private String tenantFeedback;      // Phản hồi của khách sau xử lý

    private Boolean tenantSatisfied;    // true = hài lòng
}