package ru.tutor.model.fortune;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameStatsResponse {

    @JsonProperty("level")
    private Integer level;

    @JsonProperty("experience")
    private Integer experience;

    @JsonProperty("experienceLevel")
    private Integer experienceLevel;

    @JsonProperty("levelUpLimit")
    private Integer levelUpLimit;

    @JsonProperty("diamond")
    private Integer diamond;

    @JsonProperty("countNewRewards")
    private Integer countNewRewards;

    @JsonProperty("season")
    private Integer season;

    @JsonProperty("seasonEnd")
    private String seasonEnd;

    @JsonProperty("hasLoginToday")
    private Boolean hasLoginToday;

    @JsonProperty("showModal")
    private Boolean showModal;

    @JsonProperty("days")
    private List<Day> days;

    // Вложенный класс для дней
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Day {
        @JsonProperty("item")
        private String item;

        @JsonProperty("score")
        private Integer score;

        @JsonProperty("done")
        private Boolean done;

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public Boolean getDone() {
            return done;
        }

        public void setDone(Boolean done) {
            this.done = done;
        }

        // Дополнительные методы
        public boolean isExperience() {
            return "experience".equals(item);
        }

        public boolean isDiamond() {
            return "diamond".equals(item);
        }

        public String getType() {
            return item;
        }
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(Integer experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public Integer getLevelUpLimit() {
        return levelUpLimit;
    }

    public void setLevelUpLimit(Integer levelUpLimit) {
        this.levelUpLimit = levelUpLimit;
    }

    public Integer getDiamond() {
        return diamond;
    }

    public void setDiamond(Integer diamond) {
        this.diamond = diamond;
    }

    public Integer getCountNewRewards() {
        return countNewRewards;
    }

    public void setCountNewRewards(Integer countNewRewards) {
        this.countNewRewards = countNewRewards;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public String getSeasonEnd() {
        return seasonEnd;
    }

    public void setSeasonEnd(String seasonEnd) {
        this.seasonEnd = seasonEnd;
    }

    public Boolean getHasLoginToday() {
        return hasLoginToday;
    }

    public void setHasLoginToday(Boolean hasLoginToday) {
        this.hasLoginToday = hasLoginToday;
    }

    public Boolean getShowModal() {
        return showModal;
    }

    public void setShowModal(Boolean showModal) {
        this.showModal = showModal;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    // Дополнительные методы
    public  Integer getRemainingExperience() {
        if (levelUpLimit != null && experienceLevel != null) {
            return levelUpLimit - experienceLevel;
        }
        return null;
    }

    public boolean isLevelUpAvailable() {
        return getRemainingExperience() != null && getRemainingExperience() <= 0;
    }

    public Double getProgressPercentage() {
        if (levelUpLimit != null && experience != null && levelUpLimit > 0 && experience > levelUpLimit) {
            return (experienceLevel * 100.0) / levelUpLimit;
        }
        return 0.0;
    }

    public Long getDaysCount() {
        return days != null ? (long) days.size() : 0L;
    }

    public Long getCompletedDaysCount() {
        return days != null ? days.stream().filter(day -> Boolean.TRUE.equals(day.getDone())).count() : 0L;
    }

    public Long getExperienceDaysCount() {
        return days != null ? days.stream().filter(Day::isExperience).count() : 0L;
    }

    public Long getDiamondDaysCount() {
        return days != null ? days.stream().filter(Day::isDiamond).count() : 0L;
    }

    public Integer getTotalExperienceAvailable() {
        if (days != null) {
            return days.stream()
                    .filter(Day::isExperience)
                    .mapToInt(Day::getScore)
                    .sum();
        }

        return 0;
    }

    public Integer getTotalDiamondAvailable() {
        if (days != null) {
            return days.stream()
                    .filter(Day::isDiamond)
                    .mapToInt(Day::getScore)
                    .sum();
        }
        return 0;
    }

    public Integer getEarnedExperience() {
        if (days != null) {
            return days.stream()
                    .filter(day -> Boolean.TRUE.equals(day.getDone()) && day.isExperience())
                    .mapToInt(Day::getScore)
                    .sum();
        }
        return 0;
    }

    public Integer getEarnedDiamonds() {
        if (days != null) {
            return days.stream()
                    .filter(day -> Boolean.TRUE.equals(day.getDone()) && day.isDiamond())
                    .mapToInt(Day::getScore)
                    .sum();
        }
        return 0;
    }
}
