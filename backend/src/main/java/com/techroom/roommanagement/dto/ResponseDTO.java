package com.techroom.roommanagement.dto;

/**
 * Generic Response DTO
 * Chuẩn hóa format response API
 */
public class ResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;

    public ResponseDTO() {
    }

    public ResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Static factory methods để tạo response dễ dàng
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(true, "Success", data);
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(true, message, data);
    }

    public static <T> ResponseDTO<T> error(String message) {
        return new ResponseDTO<>(false, message, null);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}