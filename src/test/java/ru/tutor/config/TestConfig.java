package ru.tutor.config;

import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static final Properties properties = new Properties();

    /*
     * Домены, url, тестовые данные для Sber/Сбер
     */

    static {
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception ex) {
            System.out.println("No config.properties found, using defaults");
        }
    }

    // ==================== Базовые URL из конфига ====================

    /** Dev1 */
    public static String getDev1BaseUrl() {
        return properties.getProperty("dev1.base.url", "https://dev1.ttplace.online");
    }

    public static String getApi1BaseUrl() {
        return properties.getProperty("api1.base.url", "https://api1.ttplace.online");
    }

    public static String getAdminBaseUrl() {
        return properties.getProperty("admin1.base.url", "https://admin1.ttplace.online");
    }

    public static String getClient1BaseUrl() {
        return properties.getProperty("client1.base.url", "https://client1.ttplace.online");
    }

    /** Dev2 */
    public static String getDev2BaseUrl() {
        return properties.getProperty("dev2.base.url", "https://dev2.ttplace.online");
    }

    public static String getApi2BaseUrl() {
        return properties.getProperty("api2.base.url", "https://api2.ttplace.online");
    }

    public static String getAdmin2BaseUrl() {
        return properties.getProperty("admin2.base.url", "https://admin2.ttplace.online");
    }

    public static String getClient2BaseUrl() {
        return properties.getProperty("client2.base.url",  "https://client2.ttplace.online");
    }

    /** Prod */

    public static String getProdBaseUrl() {
        return properties.getProperty("prod.base.url", "https://tester.tutorplace.ru");
    }

    public static String getApiProdUrl() {
        return properties.getProperty("api.prod.url", "https://api.tutorplace.ru");
    }

    public static String getClientBaseUrl() {
        return properties.getProperty("client.base.url",  "https://client.tutorplace.ru");
    }


    // ==================== Валидные тестовые данные ====================
    /** Повседневный пользователь */
    public static String getTestEmail() {
        return properties.getProperty("test.email");
    }

    /** Колесо фортуны */
    public static String getTestEmailFortune() {
        return properties.getProperty("test.email_for_fortune");
    }

    /** Пароль */
    public static String getTestPassword() {
        return properties.getProperty("test.password");
    }

    // ==================== Метод для получения URL в зависимости от окружения ====================
    public static String getCurrentUiUrl() {
        String env = System.getProperty("env.ui", "dev1"); // dev1, dev2, prod
        return switch (env) {
            case "dev2" -> getDev2BaseUrl();
            case "prod" -> getProdBaseUrl(); // или другой prod URL
            default -> getDev1BaseUrl();
        };
    }

    public static String getCurrentApiUrl() {
        String env = System.getProperty("env.api", "api1"); // api1, api2, prod
        return switch (env) {
            case "api2" -> getApi2BaseUrl(); // нужно добавить этот метод
            case "prod" -> getApiProdUrl();
            default -> getApi1BaseUrl();
        };
    }

    public static String getCurrentClientDomain() {
        String env = System.getProperty("env.client", "dev1");

        return switch (env) {
            case  "dev2" -> getClient2BaseUrl();
            case "prod" -> getClientBaseUrl();
            default -> getClient1BaseUrl();
        };
    }

    public static String getCurrentAdminDomain() {
        String env = System.getProperty("env.admin", "admin1");

        return switch (env) {
            case  "admin2" -> getAdmin2BaseUrl();
            case "prod" -> null;
            default -> getAdminBaseUrl();
        };
    }

    public static String getAdminProductUrl(int productId) {
        return getCurrentAdminDomain() + "/product/edit/" + productId;
    }

    // ==================== Тестовые карты Sber ====================
    public static class Cards {
        public static final String APPROVED = "2202205000012424";
        public static final String INSUFFICIENT_FUNDS = "2202205000060910";
        public static final String EXPIRED = "2202202107593166";
        public static final String FIXED_BALANCE = "2202205300068092";

        /** Валидная карта */
        public static class Approved {
            public static final String EXPIRY = "0535";
            public static final String CVC = "669";
        }


        /** Недостаточно средств */
        public static class InsufficientFunds {
            public static final String EXPIRY = "0628";
            public static final String CVC = "810";
        }

        /** Истек срок */
        public static class Expired {
            public static final String EXPIRY = "0622";
            public static final String CVC = "123";
        }

        /** Фиксированная карта */
        public static class FixBalance {
            public static final String EXPIRY = "0526";
            public static final String CVC = "583";
        }
    }

    /** Тексты ожиданий */
    public static class Texts {
        public static final String ERROR_MESSAGE = "Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию";
        public static final String EXCEL_PAGE_TITLE = "Excel-таблицы для начинающих - курс, онлайн обучение бесплатно по подписке";
        public static final String MAIN_PAGE_TITLE = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
    }
}
