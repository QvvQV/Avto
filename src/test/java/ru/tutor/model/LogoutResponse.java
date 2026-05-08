package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LogoutResponse(
    @JsonProperty("success") Boolean success,
    @JsonProperty("message") String message,
    @JsonProperty("timestamp") long timestamp
) {}
