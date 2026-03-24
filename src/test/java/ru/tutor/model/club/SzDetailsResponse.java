package ru.tutor.model.club;

// Модель ответа

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SzDetailsResponse {
    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private SzDetailsData data;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SzDetailsData {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("userId")
        private Integer userId;

        @JsonProperty("fullName")
        private String fullName;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("dateBirth")
        private String dateBirth;

        @JsonProperty("inn")
        private String inn;

        @JsonProperty("idEdo")
        private String idEdo;

        @JsonProperty("addressRegister")
        private String addressRegister;

        @JsonProperty("addressFact")
        private String addressFact;

        @JsonProperty("isSameAddress")
        private Boolean isSameAddress;

        @JsonProperty("passSerial")
        private String passSerial;

        @JsonProperty("passNumber")
        private String passNumber;

        @JsonProperty("passIssued")
        private String passIssued;

        @JsonProperty("passIssuer")
        private String passIssuer;

        @JsonProperty("passDepartment")
        private String passDepartment;

        @JsonProperty("bankName")
        private String bankName;

        @JsonProperty("bankAccount")
        private String bankAccount;

        @JsonProperty("bankKorAccount")
        private String bankKorAccount;

        @JsonProperty("bik")
        private String bik;

        @JsonProperty("isResident")
        private Boolean isResident;

        @JsonProperty("createdAt")
        private String createdAt;

        @JsonProperty("updateAt")
        private String updateAt;

        @JsonProperty("ogrnip")
        private String ogrnip;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SzDetailsData getData() {
        return data;
    }

    public void setData(SzDetailsData data) {
        this.data = data;
    }

    // Дополнительный метод
    public boolean isSuccessful() {
        return Boolean.TRUE.equals(success);
    }

}
