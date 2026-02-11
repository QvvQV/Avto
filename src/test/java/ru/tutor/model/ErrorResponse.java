package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String error;
    private String message;
    private String path;
    private Integer status;
    private Long timestamp;
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

//    @JsonProperty("path")
//    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }

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

//    public void setTimestamp(Long timestamp) {
//        this.timestamp = timestamp;
//    }

    @JsonProperty("success")
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

//    @JsonAlias({"error.messages"})
//    @JsonProperty("messages")
//    public Map<String, String> getMessages() {
//        return messages;
//    }
//
//    public void setMessages(Map<String, String> messages) {
//        this.messages = messages;
//    }

    // Cтатический метод
    public static ErrorResponse of(String error, String message, String path, Integer status, Long timestamp) {
        return new ErrorResponse(error, message, path, status, timestamp);
    }

//    public boolean hasValidationErrors() {
//        return errorDetails != null && errorDetails.getMessages() != null && !errorDetails.getMessages().isEmpty();
//    }
//
//    public Map<String, String> getValidationMessages() {
//        return errorDetails != null ? errorDetails.getMessages() : null;
//    }

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

//        public ErrorDetails() {}
//
//        public ErrorDetails(Map<String, String> messages) {
//            this.messages = messages;
//        }

        @JsonProperty("messages")
        public Map<String, String> getMessages() {
            return messages;
        }

//        public void setMessages(Map<String, String> messages) {
//            this.messages = messages;
//        }

        @Override
        public String toString() {
            return "ErrorDetails{" +
                    "messages=" + messages +
                    '}';
        }
    }
}
