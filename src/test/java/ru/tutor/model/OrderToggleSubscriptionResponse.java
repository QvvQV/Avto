package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderToggleSubscriptionResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("isSubscribed")
    private boolean isSubscribed;

    @JsonProperty("subscriptionStatus")
    private String subscriptionStatus;

    @JsonProperty("orderNumber")
    private String orderNumber;

    @JsonProperty("toggleCount")
    private Integer toggleCount;

    @JsonProperty("lastToggled")
    private String lastToggled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getIsSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(boolean subscribed) {
        this.isSubscribed = isSubscribed;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getToggleCount() {
        return toggleCount;
    }

    public void setToggleCount(Integer toggleCount) {
        this.toggleCount = toggleCount;
    }

    public String getLastToggled() {
        return lastToggled;
    }

    public void setLastToggled(String lastToggled) {
        this.lastToggled = lastToggled;
    }

    // Дополнительные методы
    public boolean isValid() {
        return id != null && id > 0;
    }

    public boolean isSubscribed() {
        return Boolean.TRUE.equals(isSubscribed);
    }

    public boolean hasSubscriptionStatus() {
        return subscriptionStatus != null && !subscriptionStatus.trim().isEmpty();
    }

    public boolean isActiveSubscription() {
        return "active".equalsIgnoreCase(subscriptionStatus) ||
                Boolean.TRUE.equals(isSubscribed);
    }

    public boolean hasToggledRecently() {
        if (lastToggled == null) return false;
        return true;
    }

    @Override
    public String toString() {
        return "OrderToggleSubscriptionResponse{" +
                "orderId=" + id +
                ", isSubscribed=" + isSubscribed +
                ", subscriptionStatus='" + subscriptionStatus + '\'' +
                '}';
    }
}
