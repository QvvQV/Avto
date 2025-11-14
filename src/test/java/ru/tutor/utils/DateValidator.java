package ru.tutor.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateValidator {

    private final int allowedTimeDifferenceMinutes;

    public DateValidator(int allowedTimeDifferenceMinutes) {
        this.allowedTimeDifferenceMinutes = allowedTimeDifferenceMinutes;
    }

    public void verifyTimeOfDayDifference(LocalDateTime dateTime1, LocalDateTime dateTime2,
                                         String description) {
        if (dateTime1 != null && dateTime2 != null) {
            LocalTime time1 = dateTime1.toLocalTime();
            LocalTime time2 = dateTime2.toLocalTime();

            long timeDifferenceMinutes = Math.abs(Duration.between(time1, time2).toMinutes());
            assertTrue(timeDifferenceMinutes <= allowedTimeDifferenceMinutes,
                    String.format("Разница во времени для %s превышает допустимую: %d минут (максимум %d). Время1: %s, Время2: %s",
                            description, timeDifferenceMinutes, allowedTimeDifferenceMinutes, time1, time2));
        }
    }

    public void verifyDateDifference(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                    int expectedDaysDifference, String description) {
        if (startDateTime != null && endDateTime != null) {
            long actualDaysDifference = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), endDateTime.toLocalDate());
            assertEquals(expectedDaysDifference, actualDaysDifference,
                    String.format("разница в днях для %s: ожидалось %d дней, фактически %d дней",
                            description, expectedDaysDifference, actualDaysDifference));
        }
    }
}
