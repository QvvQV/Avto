package ru.tutor.model.fortune;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameWheelRewardResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("chance")
    @JsonSetter(nulls = Nulls.SKIP)
    private Double chance = 0.0;

    @JsonProperty("rewardType")
    private String rewardType;

    @JsonProperty("rewardValue")
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer rewardValue = 0;

    @JsonProperty("winLimit")
    private Integer winLimit;

    @JsonProperty("winCount")
    private Integer winCount;

    @JsonProperty("iconSmallUrl")
    private String iconSmallUrl;

    @JsonProperty("iconUrl")
    private String iconUrl;

    @JsonProperty("showValue")
    private Boolean showValue;

    @JsonProperty("iconSmallAltUrl")
    private String iconSmallAltUrl;

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

    public Double getChance() {
        return chance;
    }

    public void setChance(Double chance) {
        this.chance = chance != null ? chance : 0.0;
    }

    public String getRewardType() {
        return rewardType;
    }

    public Integer getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(Integer rewardValue) {
        this.rewardValue = rewardValue != null ? rewardValue : 0;
    }

    public boolean isValidReward() {
        return id != null && id > 0;
    }

    public boolean hasValidChance() {
        return chance != null && chance >= 0;
    }

    public boolean hasRewardType() {
        return rewardType != null && !rewardType.trim().isEmpty();
    }

    public boolean isShowValueEnabled() {
        return Boolean.TRUE.equals(showValue);
    }

    public boolean isWon() {
        return winCount != null && winCount > 0;
    }

    public boolean isWinLimitReached() {
        if (winLimit == null || winCount == null) return false;
        return winCount >= winLimit;
    }

    public boolean hasIconUrl() {
        return iconUrl != null && !iconUrl.trim().isEmpty();
    }

    public boolean hasIconSmallUrl() {
        return iconSmallUrl != null && !iconSmallUrl.trim().isEmpty();
    }

    public boolean hasRewardValue() {
        return rewardValue != null;
    }

    public boolean isAvailableForWin() {
        if (winLimit == null || winCount == null) return true;
        return winCount < winLimit;
    }

    public String getFormattedChance() {
        if (chance != null) {
            return String.format("%.2f%%", chance);
        }
        return "0%";
    }

    public boolean isChanceValid() {
        return chance != null;
    }

    public boolean isRewardValueValid() {
        return rewardValue != null;
    }
}