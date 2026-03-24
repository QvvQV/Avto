package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WithdrawalResponse {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private WithdrawalData data;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WithdrawalData {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("userId")
        private Integer userId;

        @JsonProperty("amount")
        private Integer amount;

        @JsonProperty("status")
        private String status;

        @JsonProperty("createdAt")
        private String createdAt;

        @JsonProperty("updatedAt")
        private String updatedAt;

        // Геттеры и сеттеры
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public Integer getAmount() { return amount; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

    }

    // Геттеры и сеттеры
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WithdrawalData getData() {
        return data;
    }

    public void setData(WithdrawalData data) {
        this.data = data;
    }

    // Дополнительные методы
    public boolean isSuccessful() {
        return Boolean.TRUE.equals(success);
    }

    public boolean hasData() {
        return data != null;
    }

    public Integer getWithdrawalId() {
        return hasData() ? data.getId() : null;
    }

    public Integer getWithdrawalAmount() {
        return hasData() ? data.getAmount() : null;
    }
}