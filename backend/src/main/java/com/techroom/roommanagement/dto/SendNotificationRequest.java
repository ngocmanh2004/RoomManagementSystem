package com.techroom.roommanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class SendNotificationRequest {
    private String title;
    private String message;
    // "ALL", "ALL_TENANTS", "USERS", "ROOMS"
    private String sendTo;
    private List<Integer> userIds;
    private List<Integer> roomIds;
    private boolean sendEmail = false;
}
