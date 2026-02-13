package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReactivatePremiumResponse {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    public ReactivatePremiumResponse() {}

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
}
