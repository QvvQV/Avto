package ru.tutor.flow.builder;

public class SubscriptionPurchaseResult {
    private final String userEmail;

    public SubscriptionPurchaseResult(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail != null ? userEmail : "не установлен";
    }

    public boolean hasEmail() {
        return userEmail != null && !userEmail.isEmpty();
    }
}