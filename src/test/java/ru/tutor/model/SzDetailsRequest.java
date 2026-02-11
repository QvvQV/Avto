package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Модель запроса

@JsonIgnoreProperties(ignoreUnknown = true)
public class SzDetailsRequest {

    @JsonProperty("addressFact")
    private String addressFact;

    @JsonProperty("addressRegister")
    private String addressRegister;

    @JsonProperty("bankAccount")
    private String bankAccount;

    @JsonProperty("bankKorAccount")
    private String bankKorAccount;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("bik")
    private String bik;

    @JsonProperty("dateBirth")
    private String dateBirth;

    @JsonProperty("email")
    private String email;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("idEdo")
    private String idEdo;

    @JsonProperty("inn")
    private String inn;

    @JsonProperty("isResident")
    private Boolean isResident;

    @JsonProperty("isSameAddress")
    private Boolean isSameAddress;

    @JsonProperty("passDepartment")
    private String passDepartment;

    @JsonProperty("passIssued")
    private String passIssued;

    @JsonProperty("passIssuer")
    private String passIssuer;

    @JsonProperty("passNumber")
    private String passNumber;

    @JsonProperty("passSerial")
    private String passSerial;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("ogrnip")
    private String ogrnip;

    // Конструкторы
    public SzDetailsRequest() {}

    public SzDetailsRequest(String email, Integer userId) {
        this.email = email;
        this.userId = userId;
    }

    public String getAddressFact() {
        return addressFact;
    }

    public void setAddressFact(String addressFact) {
        this.addressFact = addressFact;
    }

    public String getAddressRegister() {
        return addressRegister;
    }

    public void setAddressRegister(String addressRegister) {
        this.addressRegister = addressRegister;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankKorAccount() {
        return bankKorAccount;
    }

    public void setBankKorAccount(String bankKorAccount) {
        this.bankKorAccount = bankKorAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdEdo() {
        return idEdo;
    }

    public void setIdEdo(String idEdo) {
        this.idEdo = idEdo;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public Boolean getIsResident() {
        return isResident;
    }

    public void setIsResident(Boolean resident) {
        isResident = resident;
    }

    public Boolean getSameAddress() {
        return isSameAddress;
    }

    public void setSameAddress(Boolean sameAddress) {
        isSameAddress = sameAddress;
    }

    public String getPassDepartment() {
        return passDepartment;
    }

    public void setPassDepartment(String passDepartment) {
        this.passDepartment = passDepartment;
    }

    public String getPassIssued() {
        return passIssued;
    }

    public void setPassIssued(String passIssued) {
        this.passIssued = passIssued;
    }

    public String getPassIssuer() {
        return passIssuer;
    }

    public void setPassIssuer(String passIssuer) {
        this.passIssuer = passIssuer;
    }

    public String getPassNumber() {
        return passNumber;
    }

    public void setPassNumber(String passNumber) {
        this.passNumber = passNumber;
    }

    public String getPassSerial() {
        return passSerial;
    }

    public void setPassSerial(String passSerial) {
        this.passSerial = passSerial;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getOgrnip() {
        return ogrnip;
    }

    public void setOgrnip(String ogrnip) {
        this.ogrnip = ogrnip;
    }
}
