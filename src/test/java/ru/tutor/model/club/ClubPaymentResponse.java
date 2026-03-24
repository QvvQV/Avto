package ru.tutor.model.club;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClubPaymentResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("date")
    private String date;

    // Констансты для направлений платежей
    public static final String DIRECTION_INCOME = "income";
    public static final String DIRECTION_OUTCOME = "outcome";

    // Проверка направления платежа
    public boolean isIncome() {
        return DIRECTION_INCOME.equals(direction);
    }

    public boolean isOutcome() {
        return DIRECTION_OUTCOME.equals(direction);
    }

    public String getName() {return name;}

    public String getDirection() {return direction;}
    public Integer getAmount() {return amount;}
    public String getDate() {return date;}

    // Парсинг даты
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

    // Проверка валидности суммы
    public boolean isValidAmount() {
        return amount != null && amount > 0;
    }

    @Override
    public String toString() {
        return String.format("ClubPayment{name='%s', direction='%s', amount='%d', date='%s'}",
                name, direction, amount, date);
    }
}
