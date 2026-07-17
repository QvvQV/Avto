package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StoryScreenResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("storyId")
    private String storyId; // или Long, смотрите по факту

    @JsonProperty("screenId")
    private String screenId;

    @JsonProperty("viewedAt")
    private String viewedAt;

    @JsonProperty("status")
    private String status;

    public Long getId() {
        return id;
    }

    public String getStoryId() {
        return storyId;
    }

    public String getScreenId() {
        return screenId;
    }

    public String getViewedAt() {
        return viewedAt;
    }

    public String getStatus() {
        return status;
    }
}
