package com.techroom.roommanagement.dto;

import lombok.Data;

@Data
public class FeedbackUpdateRequest {

    private String title;
    private String content;
    private String attachmentUrl;
}
