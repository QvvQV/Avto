package ru.tutor.flow;

import java.util.Objects;

public record PurchaseResult (String email) {
    public PurchaseResult {
        Objects.requireNonNull(email, "Email не должен быть null после успешной покупки");
    }
}
