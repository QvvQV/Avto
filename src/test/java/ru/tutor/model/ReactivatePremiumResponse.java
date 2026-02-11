package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class ReactivatePremiumResponse {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    public ReactivatePremiumResponse() {}

    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
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
