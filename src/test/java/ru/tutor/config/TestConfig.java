package ru.tutor.config;

import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static final Properties properties = new Properties();
    private static final Properties cardProperties = new Properties();

    /*
     * Default переменные для выставления окружения dev1; dev2; dev3; prod; prodMain
     */
    private static final String def = "dev1";
    private static final String api = "api1";
    private static final String admin = "admin1";

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
        validateRequiredConfigProperties();
    }

    public static boolean isProd() {
        String env = System.getProperty("env.ui", def);
        return "prod".equals(env) || "prodMain".equals(env);
    }

    private static void validateRequiredConfigProperties() {
        String[] requiredKeys = {
                "dev1.base.url", "api1.base.url", "admin1.base.url", "client1.base.url", "dev1.test.email",
                "dev2.base.url", "api2.base.url", "admin2.base.url", "client2.base.url", "dev2.test.email",
                "dev3.base.url", "api3.base.url", "admin3.base.url", "client3.base.url", "dev3.test.email",
                "prod.base.url", "api.prod.url", "client.base.url", "test.password", "aff.rid.dev", "aff.rid.prod",
                "default.utm", "prod.test.email","prod.main.base.url", "aff.rid.url", "prod.renewal.email",
                "prod.renewal.email.inactive", "system.utm", "timeout", "pageLoadTimeout"
        };
        for (String key : requiredKeys) {
            String value = properties.getProperty(key);
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalStateException(
                        "Missing or invalid config property: " + key +
                                ". Please check config.properties file."
                );
            }
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
        return properties.getProperty("dev1.base.url");
    }

    public static String getApi1BaseUrl() {
        return properties.getProperty("api1.base.url");
    }

    public static String getAdminBaseUrl() {
        return properties.getProperty("admin1.base.url");
    }

    public static String getClient1BaseUrl() {
        return properties.getProperty("client1.base.url");
    }

    public static String getEmailDev1(){
        return properties.getProperty("dev1.test.email");
    }

    /**
     * Dev2
     */
    public static String getDev2BaseUrl() {
        return properties.getProperty("dev2.base.url");
    }

    public static String getApi2BaseUrl() {
        return properties.getProperty("api2.base.url");
    }

    public static String getAdmin2BaseUrl() {
        return properties.getProperty("admin2.base.url");
    }

    public static String getClient2BaseUrl() {
        return properties.getProperty("client2.base.url");
    }

    public static String getEmailDev2(){
        return properties.getProperty("dev2.test.email");
    }

    /**
     * Dev3
     */
    public static String getDev3BaseUrl() {
        return properties.getProperty("dev3.base.url");
    }

    public static String getApi3BaseUrl() {
        return properties.getProperty("api3.base.url");
    }

    public static String getAdmin3BaseUrl() {
        return properties.getProperty("admin3.base.url");
    }

    public static String getClient3BaseUrl() {
        return properties.getProperty("client3.base.url");
    }

    public static String getEmailDev3(){
        return properties.getProperty("dev3.test.email");
    }

    /**
     * Prod
     */

    public static String getProdBaseUrl() {
        return properties.getProperty("prod.base.url");
    }

    public static String getProdMainBaseUrl() {return properties.getProperty("prod.main.base.url");}

    public static String getApiProdUrl() {
        return properties.getProperty("api.prod.url");
    }

    public static String getClientBaseUrl() {
        return properties.getProperty("client.base.url");
    }

    public static String getProdEmail() {
        return properties.getProperty("prod.test.email");
    }

    public static String getRenewalEmail() {
        return properties.getProperty("prod.renewal.email");
    }

    public static String getRenewalEmailInactive() {
        return properties.getProperty("prod.renewal.email.inactive");
    }


    // ==================== Валидные тестовые данные ====================

    /**
     * Email подтягиваеться из config.properties
     */

    /**
     * Пароль
     */
    public static String getTestPassword() {
        return properties.getProperty("test.password");
    }

    /**
     * aff_rid метка для тестов
     */
    public static String getAffRidDev() {
        return properties.getProperty("aff.rid.dev");
    }

    public static String getAffRidProd() {
        return properties.getProperty("aff.rid.prod");
    }

    public static String getCurrentAffRidUrl() {return properties.getProperty("aff.rid.url");}

    public static String getDefUtm() {
        return properties.getProperty("default.utm");
    }

    public static String getSystemUtm() {
        return properties.getProperty("system.utm");
    }

    public static long getTimeouts() {
        return Long.parseLong(properties.getProperty("timeout"));
    }

    public static long getPageLoadTimeout() {
        return Long.parseLong(properties.getProperty("pageLoadTimeout"));
    }


    // ==================== Метод для получения URL в зависимости от окружения ====================
    public static String getCurrentUiUrl() {
        String env = System.getProperty("env.ui", def); // dev1, dev2, dev3, prod, prodMain
        return switch (env) {
            case "dev2" -> getDev2BaseUrl();
            case "dev3" -> getDev3BaseUrl();
            case "prod" -> getProdBaseUrl(); // или другой prod URL
            case "prodMain" -> getProdMainBaseUrl();
            default -> getDev1BaseUrl();
        };
    }

    public static String getCurrentApiUrl() {
        String env = System.getProperty("env.api", api); // api1, api2, api3, prod
        return switch (env) {
            case "api2" -> getApi2BaseUrl(); // нужно добавить этот метод
            case "api3" -> getApi3BaseUrl();
            case "prod", "prodMain" -> getApiProdUrl();
            default -> getApi1BaseUrl();
        };
    }

    public static String getCurrentClientDomain() {
        String env = System.getProperty("env.client", def);

        return switch (env) {
            case "dev2" -> getClient2BaseUrl();
            case "dev3" -> getClient3BaseUrl();
            case "prod", "prodMain" -> getClientBaseUrl();
            default -> getClient1BaseUrl();
        };
    }

    public static String getCurrentAdminDomain() {
        String env = System.getProperty("env.admin", admin);

        return switch (env) {
            case "admin2" -> getAdmin2BaseUrl();
            case "admin3" -> getAdmin3BaseUrl();
            case "prod" -> null;
            default -> getAdminBaseUrl();
        };
    }

    public static String getTestEmail() {
        String env = System.getProperty("env.email", def);

        return switch (env) {
            case "dev2" -> getEmailDev2();
            case "dev3" -> getEmailDev3();
            case "prod" -> getProdEmail();
            default -> getEmailDev1();
        };
    }

    public static String getAffRid() {
        String env = System.getProperty("env.ui", def);

        return switch (env) {
            case "prod", "prodMain" -> getAffRidProd();
            default -> getAffRidDev();
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
