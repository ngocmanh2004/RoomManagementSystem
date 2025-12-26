package com.techroom.roommanagement.dto;

import lombok.Data;
@Data
public class CreateFeedbackDTO {
    private String title;
    private String content;
    private String attachmentUrl;
}
