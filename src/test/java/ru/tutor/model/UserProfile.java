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

    @JsonProperty("isTarifflifetime")
    private Boolean isTarifflifetime;

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

    public Boolean getTarifflifetime() {
        return isTarifflifetime;
    }

    public void setTarifflifetime(Boolean isTarifflifetime) {
        this.isTarifflifetime = isTarifflifetime;
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


}
