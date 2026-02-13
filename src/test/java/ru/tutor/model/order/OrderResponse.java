package ru.tutor.model.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {

    @JsonProperty("orderId")
    private Integer orderId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("subscriptionType")
    private String subscriptionType;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("expiresAt")
    private String expiresAt;

    @JsonProperty("paymentUrl")
    private String paymentUrl;

    @JsonProperty("email")
    private String email;

    @JsonProperty("userId")
    private String userId;

    // Конструктор
    public OrderResponse() {
    }

    // Геттеры и сеттеры
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Методы для проверки статуса
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    public boolean hasPaymentUrl() {
        return paymentUrl != null && !paymentUrl.trim().isEmpty();
    }

    public boolean isValid() {
        return orderId != null && orderId > 0 && status != null;
    }
}