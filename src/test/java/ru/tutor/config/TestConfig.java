package ru.tutor.config;

import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static final Properties properties = new Properties();

    // Домены и Url

    static {
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception ex) {
            System.out.println("No config.properties found, using defaults");
        }
    }

    public static String getApiBaseUrl() {
        return properties.getProperty("api.base.url", "https://api.dev.tutorplace.ru");
    }

    public static String getClientBaseUrl() {
        return properties.getProperty("client.base.url", "https://client.ttplace.ru");
    }

    public static String getAdminBaseUrl() {
        return properties.getProperty("admin.base.url", "https://admin.ttplace.ru");
    }

    public static String getTestEmail() {
        return properties.getProperty("test.email", "tp5052_2@test.ru");
    }

    public static String getTestPassword() {
        return properties.getProperty("test.password", "12345678");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless", "true"));
    }

    // Тестовые карты Sber
    public static class Cards {
        public static final String APPROVED = "2202205000012424";
        public static final String INSUFFICIENT_FUNDS = "2202205000060910";
        public static final String EXPIRED = "2202202107593166";
        public static final String FIXED_BALANCE = "2202205300068092";

        // Данные карт
        public static class Approved {
            public static final String EXPIRY = "0535";
            public static final String CVC = "669";
        }

        public static class InsufficientFunds {
            public static final String EXPIRY = "0628";
            public static final String CVC = "810";
        }

        public static class Expired {
            public static final String EXPIRY = "0622";
            public static final String CVC = "123";
        }

        public static class FixBalance {
            public static final String EXPIRY = "0526";
            public static final String CVC = "583";
        }
    }

    // Тексты ожиданий
    public static class Texts {
        public static final String ERROR_MESSAGE = "Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию";
        public static final String EXCEL_PAGE_TITLE = "Excel-таблицы для начинающих - курс, онлайн обучение бесплатно по подписке";
        public static final String MAIN_PAGE_TITLE = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
    }
}
