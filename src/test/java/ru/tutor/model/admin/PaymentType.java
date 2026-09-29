package ru.tutor.model.admin;

public enum PaymentType {
    WELCOME_BONUS(0),
    RECURRING(2),
    CLUB_BALANCE(19);

    private final int code;

    PaymentType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}