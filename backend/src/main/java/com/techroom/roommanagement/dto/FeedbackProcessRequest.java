package com.techroom.roommanagement.dto;

import com.techroom.roommanagement.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dùng để cập nhật trạng thái phản hồi từ phía chủ trọ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackProcessRequest {

    /**
     * Trạng thái phản hồi
     * Chỉ chấp nhận PROCESSING, RESOLVED, CANCELED
     */
    private Feedback.Status status;

    /**
     * Ghi chú của chủ trọ (chỉ cần khi RESOLVED)
     */
    private String landlordNote;
}
