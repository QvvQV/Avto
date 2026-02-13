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

    // Дополнительные методы
    public boolean isValid() {
        return id != null && id > 0;
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
