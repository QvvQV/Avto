package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String error;
    private String message;
    private final String path;
    private Integer status;
    private final Long timestamp;
    private Boolean success;
    private ErrorDetails errorDetails;

    private Map<String, String> messages;


    // Конструктор

    public ErrorResponse(String error, String message, String path, Integer status, Long timestamp) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.status = status;
        this.timestamp = timestamp;
    }

    @JsonProperty("error")
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("success")
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    // Cтатический метод
    public static ErrorResponse of(String error, String message, String path, Integer status, Long timestamp) {
        return new ErrorResponse(error, message, path, status, timestamp);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", success=" + success +
                ", errorDetails=" + errorDetails +
                '}';
    }

    // Вложенный класс для деталей ошибки валидации
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorDetails {
        private Map<String, String> messages;

        @JsonProperty("messages")
        public Map<String, String> getMessages() {
            return messages;
        }

        @Override
        public String toString() {
            return "ErrorDetails{" +
                    "messages=" + messages +
                    '}';
        }
    }
}
