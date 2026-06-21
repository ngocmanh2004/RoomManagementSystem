package com.techroom.roommanagement.dto;

import lombok.Data;

@Data
public class TenantConfirmRequest {

    // true = hài lòng, false = chưa hài lòng
    private Boolean satisfied;

    // Ý kiến của khách sau khi xử lý
    private String tenantFeedback;
}
