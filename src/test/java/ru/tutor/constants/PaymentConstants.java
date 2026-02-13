package ru.tutor.constants;

public class PaymentConstants {
    // Платежная система
    public static final String PAYMENT_SYSTEM = "CloudPayments";

    // Публичный ID для тестов
    public static final String PUBLIC_ID = "pk_92fb5586b2d613a5de95ed98dd5f7";

    // Язык по умолчанию
    public static final String DEFAULT_LANGUAGE = "ru-RU";

    // Типы платежей
    public static final String PAYMENT_TYPE_CHARGE = "charge";
    public static final String PAYMENT_TYPE_AUTH = "auth";

    // Типы платежей (числовые)
    public static final Integer PAYMENT_TYPE_CHARGE_CODE = 0;
    public static final Integer PAYMENT_TYPE_AUTH_CODE = 1;

    // Метод оплаты
    public static final Integer PAYMENT_METHOD_FULL_PAYMENT = 4;

    // Код валюты
    public static final String DEFAULT_CURRENCY = "RUB";

    // Skin
    public static final String DEFAULT_SKIN = "mini";

    // AutoClose значение по умолчанию
    public static final Integer DEFAULT_AUTO_CLOSE = 3;

    // Расчетное место
    public static final String CALCULATION_PLACE = "tutorplace.ru";

    // Минимальная и максимальная сумма платежа
    public static final Double MIN_AMOUNT = 1.0;
    public static final Double MAX_AMOUNT = 1000000.0;

    // Допустимые валюты
    public static final String[] ALLOWED_CURRENCIES = {"RUB", "USD", "EUR"};

    // Допустимые языки
    public static final String[] ALLOWED_LANGUAGES = {"ru-RU", "en-US", "kk-KZ"};

    // Допустимые skin
    public static final String[] ALLOWED_SKINS = {"mini", "classic", "modern"};

    // Допустимые autoClose значения
    public static final Integer[] ALLOWED_AUTO_CLOSE = {0, 1, 2, 3, 5, 10};

    // Эталонные данные для валидации
        public record ExpectedPayment(String description, Double amount, String currency, Integer paymentType) {
    }

    // Эталонные значения для разных типов платежей
    public static final ExpectedPayment ACCESS_PURCHASE =
            new ExpectedPayment("Покупка доступа TutorPlace", 1.0, "RUB", 0);

    public static final ExpectedPayment COURSE_PURCHASE =
            new ExpectedPayment("Покупка курса", 1990.0, "RUB", 0);

    public static final ExpectedPayment SUBSCRIPTION_PURCHASE =
            new ExpectedPayment("Подписка на месяц", 490.0, "RUB", 0);

    public static ExpectedPayment getExpectedPaymentByDescription(String description) {
        return switch (description) {
            case "Покупка доступа TutorPlace" -> ACCESS_PURCHASE;
            case "Покупка курса" -> COURSE_PURCHASE;
            case "Подписка на месяц" -> SUBSCRIPTION_PURCHASE;
            default -> null;
        };
    }

    // Валидация типов
    public static boolean isValidCurrency(String currency) {
        for (String allowedCurrency : ALLOWED_CURRENCIES) {
            if (allowedCurrency.equals(currency)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidLanguage(String language) {
        for (String allowedLanguage : ALLOWED_LANGUAGES) {
            if (allowedLanguage.equals(language)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidSkin(String skin) {
        for (String allowedSkin : ALLOWED_SKINS) {
            if (allowedSkin.equals(skin)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidAutoClose(Integer autoClose) {
        for (Integer allowedAutoClose : ALLOWED_AUTO_CLOSE) {
            if (allowedAutoClose.equals(autoClose)) {
                return true;
            }
        }
        return false;
    }
}
