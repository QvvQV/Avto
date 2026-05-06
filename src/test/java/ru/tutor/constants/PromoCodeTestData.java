package ru.tutor.constants;

public class PromoCodeTestData {

    // Типы ошибок промокодов
    public static final String ERROR_ALREADY_USED = "Этот промокод уже использовался пользователем";
    public static final String ERROR_INACTIVE = "Промокод неактивный или неверно настроен";
    public static final String ERROR_EMPTY_PROMO_CODE_ORDER_NUMBER = "Invalid input. [orderNumber] - Значение не должно быть пустым. [orderNumber] - Значение слишком короткое. Должно быть равно 8 символам или больше. ";
    public static final String ERROR_NOT_FOUND = "PromoCode is not found by name";
    public static final String ERROR_EMPTY_PROMO_CODE_NAME= "Invalid input. [promoCodeName] - Значение не должно быть пустым.";

    // Сообщение об успехе
    public static final String SUCCESS_MESSAGE_PREFIX = "Промокод успешно применён";

    /**
     * Проверка, является ли ответ успешным
     */
    public static boolean isSuccessResponse(String message) {
        return message!=null && message.contains(SUCCESS_MESSAGE_PREFIX);
    }

    /**
     * Проверка типа ошибки
     */
    public static String getErrorType(String error) {
        if (error == null) return null;
        if (error.contains(ERROR_ALREADY_USED) ) return ERROR_ALREADY_USED;
        if (error.contains(ERROR_INACTIVE) ) return ERROR_INACTIVE;
        if (error.contains(ERROR_EMPTY_PROMO_CODE_NAME) ) return ERROR_EMPTY_PROMO_CODE_NAME;
        if (error.contains(ERROR_NOT_FOUND) ) return ERROR_NOT_FOUND;
        if (error.contains(ERROR_EMPTY_PROMO_CODE_ORDER_NUMBER) ) return ERROR_EMPTY_PROMO_CODE_ORDER_NUMBER;
        return "unknown";
    }

    /**
     * Проверка, является ли ошибка ожидаемого типа
     */
    public static boolean isExpectedError(String error, String expectedErrorType) {
        String actualError = getErrorType(error);
        return actualError.equals(expectedErrorType);
    }
}
