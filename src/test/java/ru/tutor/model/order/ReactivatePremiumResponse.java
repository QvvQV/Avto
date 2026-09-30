package ru.tutor.model.order;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReactivatePremiumResponse(
    @JsonProperty("success") boolean success,
    @JsonProperty("message") String message
) {}
