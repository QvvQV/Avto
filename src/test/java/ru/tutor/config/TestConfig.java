package ru.tutor.config;

import java.io.InputStream;
import java.util.Properties;
import java.io.IOException;

public class TestConfig {
    private static final Properties properties = new Properties();
    private static final Properties cardProperties = new Properties();

    /*
     * Домены, url, тестовые данные для Sber/Сбер
     */

    static {
        // Загрузка config.properties
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception ex) {
            System.out.println("No config.properties found, using defaults");
        }

        // Загрузка cards.properties
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("cards.properties")) {
            if (input != null) {
                cardProperties.load(input);
                validateRequiredCardsProperties();
            } else {
                throw new IllegalStateException(
                        "ERROR: cards.properties not found in classpath! " +
                                "Copy cards.properties.template to cards.properties and fill in the values."
                );
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load cards.properties", ex);
        }
    }

    private static void validateRequiredCardsProperties() {
        String[] requiredKeys = {
                "card.approved.number",
                "card.approved.expiry",
                "card.approved.cvc",
                "card.insufficient_funds.number",
                "card.insufficient_funds.expiry",
                "card.insufficient_funds.cvc",
                "card.expired.number",
                "card.expired.expiry",
                "card.expired.cvc",
                "card.fixed_balance.number",
                "card.fixed_balance.expiry",
                "card.fixed_balance.cvc",
                "cloud.card.number",
                "cloud.card.short",
                "cloud.card.expiry",
                "cloud.card.cvc"
        };

        for (String key : requiredKeys) {
            String value = cardProperties.getProperty(key);
            if (value == null || value.trim().isEmpty() || value.contains("YOUR_")) {
                throw new IllegalStateException(
                        "Missing or invalid card property: " + key +
                                ". Please check cards.properties file."
                );
            }
        }
    }

    private static String getCardProperty(String key) {
        String value = cardProperties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing required card property: " + key);
        }
        return value;
    }

    // ==================== Базовые URL из конфига ====================

    /**
     * Dev1
     */
    public static String getDev1BaseUrl() {
        return properties.getProperty("dev1.base.url", "config.properties");
    }

    public static String getApi1BaseUrl() {
        return properties.getProperty("api1.base.url", "config.properties");
    }

    public static String getAdminBaseUrl() {
        return properties.getProperty("admin1.base.url", "config.properties");
    }

    public static String getClient1BaseUrl() {
        return properties.getProperty("client1.base.url", "config.properties");
    }

    /**
     * Dev2
     */
    public static String getDev2BaseUrl() {
        return properties.getProperty("dev2.base.url", "config.properties");
    }

    public static String getApi2BaseUrl() {
        return properties.getProperty("api2.base.url", "config.properties");
    }

    public static String getAdmin2BaseUrl() {
        return properties.getProperty("admin2.base.url", "config.properties");
    }

    public static String getClient2BaseUrl() {
        return properties.getProperty("client2.base.url", "config.properties");
    }

    /**
     * Prod
     */

    public static String getProdBaseUrl() {
        return properties.getProperty("prod.base.url", "config.properties");
    }

    public static String getApiProdUrl() {
        return properties.getProperty("api.prod.url", "config.properties");
    }

    public static String getClientBaseUrl() {
        return properties.getProperty("client.base.url", "config.properties");
    }


    // ==================== Валидные тестовые данные ====================

    /**
     * Повседневный пользователь
     */
    public static String getTestEmail() {
        return properties.getProperty("test.email");
    }

    /**
     * Колесо фортуны
     */
    public static String getTestEmailFortune() {
        return properties.getProperty("test.email_for_fortune");
    }

    /**
     * Пароль
     */
    public static String getTestPassword() {
        return properties.getProperty("test.password");
    }

    // ==================== Метод для получения URL в зависимости от окружения ====================
    public static String getCurrentUiUrl() {
        String env = System.getProperty("env.ui", "dev2"); // dev1, dev2, prod
        return switch (env) {
            case "dev2" -> getDev2BaseUrl();
            case "prod" -> getProdBaseUrl(); // или другой prod URL
            default -> getDev1BaseUrl();
        };
    }

    public static String getCurrentApiUrl() {
        String env = System.getProperty("env.api", "api2"); // api1, api2, prod
        return switch (env) {
            case "api2" -> getApi2BaseUrl(); // нужно добавить этот метод
            case "prod" -> getApiProdUrl();
            default -> getApi1BaseUrl();
        };
    }

    public static String getCurrentClientDomain() {
        String env = System.getProperty("env.client", "dev2");

        return switch (env) {
            case "dev2" -> getClient2BaseUrl();
            case "prod" -> getClientBaseUrl();
            default -> getClient1BaseUrl();
        };
    }

    public static String getCurrentAdminDomain() {
        String env = System.getProperty("env.admin", "admin2");

        return switch (env) {
            case "admin2" -> getAdmin2BaseUrl();
            case "prod" -> null;
            default -> getAdminBaseUrl();
        };
    }

    public static String getAdminProductUrl(int productId) {
        return getCurrentAdminDomain() + "/product/edit/" + productId;
    }

    // ==================== Тестовые карты Sber ====================
    public static class Cards {
        public static String getApproved(){
            return getCardProperty("card.approved.number");
        }

        public static String getApprovedExpiry(){
            return getCardProperty("card.approved.expiry");
        }

        public static String getApprovedCvc(){
            return getCardProperty("card.approved.cvc");
        }

        public static String getInsufficientFunds (){
            return getCardProperty("card.insufficient_funds.number");
        }

        public static String getInsufficientFundsExpiry(){
            return getCardProperty("card.insufficient_funds.expiry");
        }

        public static String getInsufficientFundsCvc(){
            return getCardProperty("card.insufficient_funds.cvc");
        }

        public static String getExpired(){
            return getCardProperty("card.expired.number");
        }
        public static String getExpiredExpiry(){
            return getCardProperty("card.expired.expiry");
        }

        public static String getExpiredCvc(){
            return getCardProperty("card.expired.cvc");
        }

        public static String getFixedBalance(){
            return getCardProperty("card.fixed_balance.number");
        }

        public static String getFixedBalanceExpiry(){
            return getCardProperty("card.fixed_balance.expiry");
        }

        public static String getFixedBalanceCvc(){
            return getCardProperty("card.fixed_balance.cvc");
        }

        // CloudPayments карты
        public static String getCloudCardNumber(){
            return getCardProperty("cloud.card.number");
        }

        public static String getCloudCardShort(){
            return getCardProperty("cloud.card.short");
        }

        public static String getCloudCardExpiry(){
            return getCardProperty("cloud.card.expiry");
        }

        public static String getCloudCardCvc(){
            return getCardProperty("cloud.card.cvc");
        }
    }

    /**
     * Тексты ожиданий
     */
    public static class Texts {
        public static final String ERROR_MESSAGE = "Операция отклонена. Проверьте введенные данные, достаточность средств на карте и повторите операцию";
        public static final String EXCEL_PAGE_TITLE = "Excel-таблицы для начинающих - курс, онлайн обучение бесплатно по подписке";
        public static final String MAIN_PAGE_TITLE = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
    }
}
