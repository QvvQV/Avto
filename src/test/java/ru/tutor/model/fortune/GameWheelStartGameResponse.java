package ru.tutor.model.fortune;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameWheelStartGameResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("value")
    private Integer value;

    @JsonProperty("iconSmallUrl")
    private String iconSmallUrl;

    @JsonProperty("iconUrl")
    private String iconUrl;

    @JsonProperty("userRewardId")
    private Long userRewardId;

    // Флаг для определения типа ответа
    private boolean success = true;
    private String errorMessage;
    private String errorCode;

    // Конструктор для успешного ответа
    public GameWheelStartGameResponse() {
    }

    // Конструктор для ошибки
    public GameWheelStartGameResponse(String errorMessage, String errorCode) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getIconSmallUrl() {
        return iconSmallUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public Long getUserRewardId() {
        return userRewardId;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }

    @Override
    public String toString() {

        if (!success) {
            return "GameWheelStartGameResponse{" +
                    "success=false" +
                    ", errorMessage='" + errorMessage + '\'' +
                    ", errorCode='" + errorCode + '\'' +
                    '}';
        }

        return "GameWheelStartResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", iconSmallUrl='" + iconSmallUrl + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", userRewardId=" + userRewardId +
                '}';
    }
}