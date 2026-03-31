package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RegistrationResponse {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("token")
    private String token;

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("requiresEmailConfirmation")
    private Boolean requiresEmailConfirmation;

    // ==================== КОНСТРУКТОРЫ ====================

    public RegistrationResponse() {
    }

    public RegistrationResponse(Boolean success, String message, Integer userId,
                                String token, String email, String name,
                                Boolean requiresEmailConfirmation) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.token = token;
        this.email = email;
        this.name = name;
        this.requiresEmailConfirmation = requiresEmailConfirmation;
    }

    // ==================== ГЕТТЕРЫ ====================

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Boolean getRequiresEmailConfirmation() {
        return requiresEmailConfirmation;
    }

    // ==================== СЕТТЕРЫ ====================

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequiresEmailConfirmation(Boolean requiresEmailConfirmation) {
        this.requiresEmailConfirmation = requiresEmailConfirmation;
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /**
     * Проверка успешности регистрации
     */
    public boolean isSuccessful() {
        if (success != null && success) {
            return true;
        }
        return "ok".equals(message);
    }

    /**
     * Проверка наличия userId
     */
    public boolean hasUserId() {
        return userId != null && userId > 0;
    }

    /**
     * Получение короткой версии токена для логирования
     */
    public String getShortToken() {
        if (token == null) return null;
        if (token.length() <= 20) return token;
        return token.substring(0, 20) + "...";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationResponse that = (RegistrationResponse) o;
        return Objects.equals(success, that.success) &&
                Objects.equals(message, that.message) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(token, that.token) &&
                Objects.equals(email, that.email) &&
                Objects.equals(name, that.name) &&
                Objects.equals(requiresEmailConfirmation, that.requiresEmailConfirmation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, userId, token, email, name, requiresEmailConfirmation);
    }

    @Override
    public String toString() {
        return "RegistrationResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                ", token='" + getShortToken() + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", requiresEmailConfirmation=" + requiresEmailConfirmation +
                '}';
    }
}
