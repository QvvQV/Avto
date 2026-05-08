package ru.tutor.model.fortune;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRewardResponse(
    @JsonProperty("id") Long id,
    @JsonProperty("name") String name,
    @JsonProperty("rewardType") String rewardType,
    @JsonProperty("rewardValue") Integer rewardValue,
    @JsonProperty("isShowValue") Boolean isShowValue,
    @JsonProperty("iconSmallUrl") String iconSmallUrl,
    @JsonProperty("iconSmallAtlUrl") String iconSmallAtlUrl,
    @JsonProperty("iconUrl") String iconUrl,
    @JsonProperty("userRewardId") Long userRewardId,
    @JsonProperty("expiresAt") String expiresAt
) {
    @Override
    public String toString() {
        return "UserRewardResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rewardValue=" + rewardValue +
                ", rewardType='" + rewardType + '\'' +
                ", expiresAt='" + expiresAt + '\'' +
                ", isShowValue=" + isShowValue +
                '}';
    }
}
