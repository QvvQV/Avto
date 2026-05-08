package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WithdrawalResponse(
    @JsonProperty("success") Boolean success,
    @JsonProperty("message") String message,
    @JsonProperty("data") WithdrawalData data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WithdrawalData(
        @JsonProperty("id") Integer id,
        @JsonProperty("userId") Integer userId,
        @JsonProperty("amount") Integer amount,
        @JsonProperty("status") String status,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("updatedAt") String updatedAt
    ) {}

    public boolean isSuccessful() {
        return Boolean.TRUE.equals(success);
    }

    public boolean hasData() {
        return data != null;
    }

    public Integer getWithdrawalId() {
        return hasData() ? data.id() : null;
    }

    public Integer getWithdrawalAmount() {
        return hasData() ? data.amount() : null;
    }
}
