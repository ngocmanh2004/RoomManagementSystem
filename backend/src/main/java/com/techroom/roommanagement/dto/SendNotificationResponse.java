package com.techroom.roommanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendNotificationResponse {
    private boolean success;
    private String message;
    private Integer sentToCount; // Ánh xạ từ 'sentToCount?: number'
}