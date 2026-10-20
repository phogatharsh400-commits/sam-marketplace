package com.sam.marketplace.common.response;

import java.time.Instant;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private Instant timestamp;
    private String path;

    public ApiResponse() {
        this.timestamp = Instant.now();
    }

    public ApiResponse(boolean success, String message, T data, String errorCode, Instant timestamp, String path) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.path = path;
    }

    public static <T> ApiResponse<T> success(T data, String message, String path) {
        return new ApiResponse<>(true, message, data, null, Instant.now(), path);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return success(data, message, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully", null);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, String path) {
        return new ApiResponse<>(false, message, null, errorCode, Instant.now(), path);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;
        private String errorCode;
        private Instant timestamp = Instant.now();
        private String path;

        public Builder<T> success(boolean success) { this.success = success; return this; }
        public Builder<T> message(String message) { this.message = message; return this; }
        public Builder<T> data(T data) { this.data = data; return this; }
        public Builder<T> errorCode(String errorCode) { this.errorCode = errorCode; return this; }
        public Builder<T> timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public Builder<T> path(String path) { this.path = path; return this; }
        public ApiResponse<T> build() {
            return new ApiResponse<>(success, message, data, errorCode, timestamp, path);
        }
    }
}
