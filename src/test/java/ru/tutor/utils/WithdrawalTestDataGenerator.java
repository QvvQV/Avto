package ru.tutor.utils;

import ru.tutor.model.club.WithdrawalRequest;

import java.util.Map;

public class WithdrawalTestDataGenerator {

    /**
     * Генерация валидного запроса на вывод
     */
    public static WithdrawalRequest generateValidWithdrawalRequest() {
        // Стандартная сумма
        return new WithdrawalRequest(100);
    }

    /**
     * Генерация запроса с минимальной суммой
     */
    public static WithdrawalRequest generateMinimalWithdrawalRequest() {
        // Минимальная сумма (1)
        return new WithdrawalRequest(1);
    }

    /**
     * Генерация запроса с большой суммой
     */
    public static WithdrawalRequest generateLargeWithdrawalRequest() {
        // Большая сумма
        return new WithdrawalRequest(10000);
    }

    /**
     * Генерация запроса с нулевой суммой
     */
    public static WithdrawalRequest generateZeroAmountRequest() {
        return new WithdrawalRequest(0);
    }

    /**
     * Генерация запроса с отрицательной суммой
     */
    public static WithdrawalRequest generateNegativeAmountRequest() {
        return new WithdrawalRequest(-1);
    }

    /**
     * Конвертация объекта в Map для передачи в API
     */
    public static Map<String, Object> toMap(WithdrawalRequest request) {
        return Map.of(
                "amount", request.amount()
        );
    }

    /**
     * Создание payload
     */
    public static Map<String, Object> createExamplePayload() {
        return Map.of("amount", 100);
    }
}
