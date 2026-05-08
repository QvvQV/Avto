package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WithdrawalRequest(
    @JsonProperty("amount") Integer amount
) {}
