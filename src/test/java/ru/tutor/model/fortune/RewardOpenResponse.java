package ru.tutor.model.fortune;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RewardOpenResponse {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("openRewardId")
    private Long openRewardId;

    @JsonProperty("rewardDetails")
    private UserRewardResponse rewardDetails;
}
