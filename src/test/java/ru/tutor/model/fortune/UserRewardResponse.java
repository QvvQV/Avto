package ru.tutor.model.fortune;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRewardResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("rewardType")
    private String rewardType;

    @JsonProperty("rewardValue")
    private Integer rewardValue;

    @JsonProperty("isShowValue")
    private Boolean isShowValue;

    @JsonProperty("iconSmallUrl")
    private String iconSmallUrl;

    @JsonProperty("iconSmallAtlUrl")
    private String iconSmallAtlUrl;

    @JsonProperty("iconUrl")
    private String iconUrl;

    @JsonProperty("userRewardId")
    private Long userRewardId;

    @JsonProperty("expiresAt")
    private String expiresAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRewardType() {
        return rewardType;
    }

    public Integer getRewardValue() {
        return rewardValue;
    }

    public Boolean getShowValue() {
        return isShowValue;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

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
