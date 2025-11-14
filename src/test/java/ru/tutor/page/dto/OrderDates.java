package ru.tutor.page.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class OrderDates {
    private final LocalDateTime created;
    private final LocalDateTime expires;
    private final LocalDateTime nextPayment;

    public OrderDates(LocalDateTime created, LocalDateTime expires, LocalDateTime nextPayment) {
        this.created = created;
        this.expires = expires;
        this.nextPayment = nextPayment;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public LocalDateTime getNextPayment() {
        return nextPayment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDates that = (OrderDates) o;
        return Objects.equals(created, that.created) &&
                Objects.equals(expires, that.expires) &&
                Objects.equals(nextPayment, that.nextPayment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(created, expires, nextPayment);
    }

    @Override
    public String toString() {
        return "OrderDates{" +
                "created=" + created +
                ", expires=" + expires +
                ", nextPayment=" + nextPayment +
                '}';
    }
}