package ru.tutor.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginResponse(
    @JsonProperty("token") String token,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("expires_in") Long expiresIn
) {}
