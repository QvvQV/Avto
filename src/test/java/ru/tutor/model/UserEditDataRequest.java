package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserEditDataRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("telegram")
    private String telegram;

    @JsonProperty("gender")
    private Integer gender;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("notificationTimeFrom")
    private String notificationTimeFrom;

    @JsonProperty("notificationTimeTo")
    private String notificationTimeTo;

    @JsonProperty("about")
    private String about;

    @JsonProperty("emailAdditional")
    private String emailAdditional;

    public UserEditDataRequest email(String email) {
        this.email = email;
        return this;
    }

    public UserEditDataRequest name(String name) {
        this.name = name;
        return this;
    }

    public UserEditDataRequest telegram(String telegram) {
        this.telegram = telegram;
        return this;
    }

    public UserEditDataRequest gender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public UserEditDataRequest phone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserEditDataRequest notificationTimeFrom(String notificationTimeFrom) {
        this.notificationTimeFrom = notificationTimeFrom;
        return this;
    }

    public UserEditDataRequest notificationTimeTo(String notificationTimeTo) {
        this.notificationTimeTo = notificationTimeTo;
        return this;
    }

    public UserEditDataRequest about(String about) {
        this.about = about;
        return this;
    }

    public UserEditDataRequest emailAdditional(String emailAdditional) {
        this.emailAdditional = emailAdditional;
        return this;
    }

    // Getters for serialization
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getTelegram() { return telegram; }
    public Integer getGender() { return gender; }
    public String getPhone() { return phone; }
    public String getNotificationTimeFrom() { return notificationTimeFrom; }
    public String getNotificationTimeTo() { return notificationTimeTo; }
    public String getAbout() { return about; }
    public String getEmailAdditional() { return emailAdditional; }
}
