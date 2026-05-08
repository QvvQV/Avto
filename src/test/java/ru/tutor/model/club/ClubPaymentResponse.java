package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClubPaymentResponse(
    @JsonProperty("name") String name,
    @JsonProperty("direction") String direction,
    @JsonProperty("amount") Integer amount,
    @JsonProperty("date") String date
) {
    public static final String DIRECTION_INCOME = "income";
    public static final String DIRECTION_OUTCOME = "outcome";

    public boolean isIncome() {
        return DIRECTION_INCOME.equals(direction);
    }

    public boolean isOutcome() {
        return DIRECTION_OUTCOME.equals(direction);
    }

    public LocalDateTime getDateAsLocalDateTime() {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }

    public boolean isValidAmount() {
        return amount != null && amount > 0;
    }

    @Override
    public String toString() {
        return String.format("ClubPayment{name='%s', direction='%s', amount='%d', date='%s'}",
                name, direction, amount, date);
    }
}
