package ru.tutor.page.dto;

import java.time.LocalDateTime;

public record OrderDates(LocalDateTime created, LocalDateTime expires, LocalDateTime nextPayment) {

    @Override
    public String toString() {
        return "OrderDates{" +
                "created=" + created +
                ", expires=" + expires +
                ", nextPayment=" + nextPayment +
                '}';
    }
}