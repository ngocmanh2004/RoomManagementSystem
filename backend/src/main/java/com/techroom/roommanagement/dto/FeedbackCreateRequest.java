package com.techroom.roommanagement.dto;

import lombok.Data;

@Data
public class FeedbackCreateRequest {

    // ID phòng mà khách đang thuê
    private Integer roomId;

    // Tiêu đề phản hồi
    private String title;

    // Nội dung phản hồi
    private String content;

    // Ảnh đính kèm (URL)
    private String attachmentUrl;
}
