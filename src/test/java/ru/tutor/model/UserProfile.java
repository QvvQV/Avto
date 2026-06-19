package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("premium")
    private Boolean premium;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("hasLoginToday")
    private Boolean hasLoginToday;

    @JsonProperty("clubMembership")
    private String clubMembership;

    @JsonProperty("diamondBalance")
    private Integer diamondBalance;

    @JsonProperty("registrationPlatform")
    private String registrationPlatform;

    @JsonProperty("isEnabledGameWheel")
    private Boolean isEnabledGameWheel;

    @JsonProperty("isTariffLifetime")
    private Boolean isTariffLifetime;

    @JsonProperty("isYtSubscribed")
    private Boolean isYtSubscribed;

    @JsonProperty("isVkSubscribed")
    private Boolean isVkSubscribed;

    @JsonProperty("isTgSubscribed")
    private Boolean isTgSubscribed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getGender() {
        return gender;
    }

    public Boolean getIsTariffLifetime() {
        return isTariffLifetime;
    }

    public void setIsTariffLifetime(Boolean isTariffLifetime) {
        this.isTariffLifetime = isTariffLifetime;
    }

    public Boolean getYtSubscribed() {
        return isYtSubscribed;
    }

    public void setYtSubscribed(Boolean isYtSubscribed) {
        this.isYtSubscribed = isYtSubscribed;
    }

    public Boolean getVkSubscribed() {
        return isVkSubscribed;
    }

    public void setVkSubscribed(Boolean isVkSubscribed) {
        this.isVkSubscribed = isVkSubscribed;
    }

    public Boolean getTgSubscribed() {
        return isTgSubscribed;
    }

    public void setTgSubscribed(Boolean isTgSubscribed) {
        this.isTgSubscribed = isTgSubscribed;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getHasLoginToday() {
        return hasLoginToday;
    }

    public void setHasLoginToday(Boolean hasLoginToday) {
        this.hasLoginToday = hasLoginToday;
    }

    public String getClubMemberShip() {
        return clubMembership;
    }

    public void setClubMemberShip(String clubMemberShip) {
        this.clubMembership = clubMemberShip;
    }

    public Integer getDiamondBalance() {
        return diamondBalance;
    }

    public void setDiamondBalance(Integer diamondBalance) {
        this.diamondBalance = diamondBalance;
    }

    public String getRegistrationPlatform() {
        return registrationPlatform;
    }

    public void setRegistrationPlatform(String registrationPlatform) {
        this.registrationPlatform = registrationPlatform;
    }

    public Boolean getEnabledGameWheel() {
        return isEnabledGameWheel;
    }

    public void setEnabledGameWheel(Boolean enabledGameWheel) {
        isEnabledGameWheel = enabledGameWheel;
    }


}
