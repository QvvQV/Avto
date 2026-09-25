package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShareRewardResponse(
    @JsonProperty("granted") Boolean granted,
    @JsonProperty("balance") Integer balance
) {}