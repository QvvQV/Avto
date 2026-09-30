package ru.tutor.model.promoCode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ApplyPromoCodeResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("error")
    private String error;

    public ApplyPromoCodeResponse() {}

    public ApplyPromoCodeResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /**
     * Проверка успешного применения промокода
     */
    public boolean isSuccessful() {
        return message != null && !message.isEmpty() && error == null;
    }

    /**
     * Проверка наличия ошибки
     */
    public boolean hasError() {
        return error != null && !error.isEmpty();
    }

    /**
     * Получение периода действия промокода
     */
    public String getPeriod() {
        if (message == null) return null;
        int fromIndex = message.indexOf("с ");
        int toIndex = message.indexOf(" по ");
        if (fromIndex != -1 && toIndex != -1) {
            return message.substring(fromIndex);
        }
        return null;
    }

    /**
     * Создание успешного ответа
     */
    public static ApplyPromoCodeResponse success(String message) {
        return new ApplyPromoCodeResponse(message, null);
    }

    /**
     * Создание ответа с ошибкой
     */
    public static ApplyPromoCodeResponse error(String error) {
        return new ApplyPromoCodeResponse(null, error);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplyPromoCodeResponse that = (ApplyPromoCodeResponse) o;
        return  Objects.equals(message, that.message) &&
                Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, error);
    }

    @Override
    public String toString() {
        if (isSuccessful()) {
            return "ApplyPromoCodeResponse{message='" + message + "'}";
        }
        return "ApplyPromoCodeResponse{error='" + error + "'}";
    }
}
