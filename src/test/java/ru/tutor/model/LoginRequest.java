package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(String email, String password) {

    @Override
    @JsonProperty("email")
    public String email() {
        return email;
    }

    @Override
    @JsonProperty("password")
    public String password() {
        return password;
    }
}
