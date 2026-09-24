package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubWithdrawalResponse(
    @JsonProperty("id") Integer id,
    @JsonProperty("number") String number,
    @JsonProperty("amount") Integer amount,
    @JsonProperty("createdAt") String createdAt,
    @JsonProperty("status") Status status,
    @JsonProperty("method") Method method
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Status(
        @JsonProperty("code") String code,
        @JsonProperty("label") String label
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Method(
        @JsonProperty("code") String code,
        @JsonProperty("label") String label
    ) {}

    public boolean isNew() {
        return status != null && "new".equals(status.code());
    }

    public boolean isRejected() {
        return status != null && "rejected".equals(status.code());
    }
}