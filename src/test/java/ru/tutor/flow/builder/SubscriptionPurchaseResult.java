package ru.tutor.flow.builder;

public record SubscriptionPurchaseResult(String userEmail) {

    @Override
    public String userEmail() {
        return userEmail != null ? userEmail : "не установлен";
    }
}