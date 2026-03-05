package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigResponse {
    @JsonProperty("requestPath")
    private String requestPath;

    @JsonProperty("timeout")
    private Integer timeout;

    @JsonProperty("maxConnections")
    private Integer maxConnections;

    @JsonProperty("environment")
    private String enviroment;

    public String getRequestPath() {
        return requestPath;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }
}
