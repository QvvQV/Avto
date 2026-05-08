package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConfigResponse(
    @JsonProperty("requestPath") String requestPath,
    @JsonProperty("timeout") Integer timeout,
    @JsonProperty("maxConnections") Integer maxConnections,
    @JsonProperty("environment") String environment
) {}
