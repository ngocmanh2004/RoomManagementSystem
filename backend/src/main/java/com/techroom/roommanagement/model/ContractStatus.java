package com.techroom.roommanagement.model;

public enum ContractStatus {
    PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    ACTIVE("Đang hiệu lực"),
    EXPIRED("Hết hạn"),
    CANCELLED("Đã hủy"),
    REJECTED("Bị từ chối"),
    COMPLETED("Hoàn thành"),
    TERMINATED("Đã chấm dứt");

    private final String displayName;

    ContractStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}