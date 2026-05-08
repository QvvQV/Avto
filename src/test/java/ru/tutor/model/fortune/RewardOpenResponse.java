package ru.tutor.model.fortune;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RewardOpenResponse(
    @JsonProperty("success") boolean success,
    @JsonProperty("message") String message,
    @JsonProperty("openRewardId") Long openRewardId,
    @JsonProperty("rewardDetails") UserRewardResponse rewardDetails
) {}
