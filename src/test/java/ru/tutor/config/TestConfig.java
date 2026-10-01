package ru.tutor.config;

import java.io.InputStream;
import java.util.Map;
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

    /**
     * Prod
     */

    public static String getProdBaseUrl() {
        return properties.getProperty("prod.base.url");
    }

    public static String getProdMainBaseUrl() {return properties.getProperty("prod.main.base.url");}

    public static String getRenewalEmail() {
        return properties.getProperty("prod.renewal.email");
    }

    public static String getRenewalEmailInactive() {
        return properties.getProperty("prod.renewal.email.inactive");
    }

    // ==================== Валидные тестовые данные ====================

    /**
     * Пароль
     */
    public static String getTestPassword() {
        return properties.getProperty("test.password");
    }

    /**
     * aff_rid метка для тестов
     */

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

        /* ==================== Выбор окружения: таблицы env -> ключ config.properties
     ==================== */
    private static final Map<String, String> UI_URL_KEYS = Map.of(
            "dev1", "dev1.base.url",
                "dev2", "dev2.base.url",
                "dev3", "dev3.base.url",
                "prod", "prod.base.url",
                "prodMain", "prod.main.base.url"
        );

    private static final Map<String, String> API_URL_KEYS = Map.of(
            "api1", "api1.base.url",
            "api2", "api2.base.url",
            "api3", "api3.base.url",
            "prod", "api.prod.url",
            "prodMain", "api.prod.url"
    );

    private static final Map<String, String> CLIENT_URL_KEYS = Map.of(
            "dev1", "client1.base.url",
            "dev2", "client2.base.url",
            "dev3", "client3.base.url",
            "prod", "client.base.url",
            "prodMain", "client.base.url"
    );

    private static final Map<String, String> ADMIN_URL_KEYS = Map.of(
            "admin1", "admin1.base.url",
            "admin2", "admin2.base.url",
            "admin3", "admin3.base.url"
    );

    private static final Map<String, String> EMAIL_KEYS = Map.of(
            "dev1", "dev1.test.email",
            "dev2", "dev2.test.email",
            "dev3", "dev3.test.email",
            "prod", "prod.test.email"
    );

    private static final Map<String, String> AFF_RID_KEYS = Map.of(
            "dev1", "aff.rid.dev",
            "dev2", "aff.rid.dev",
            "dev3", "aff.rid.dev",
            "prod", "aff.rid.prod",
            "prodMain", "aff.rid.prod"
    );

    /** Значение системного проперти или дефолт */
     private static String env(String prop, String defValue) {
         return System.getProperty(prop, defValue);
     }

     /** Резолв значение по таблице "env -> ключ"; при неизвестном env - дефолт таблицы */
     private static String currentProperty(Map<String, String> keysByEnv, String prop, String defEnv) {
         return properties.getProperty(keysByEnv.getOrDefault(env(prop, defEnv), keysByEnv.get(defEnv)));
     }

    // ==================== Метод для получения URL в зависимости от окружения ====================
    public static boolean isProd() {
        String env = System.getProperty("env.ui", def);
        return "prod".equals(env) || "prodMain".equals(env);
    }

    public static String getCurrentUiUrl() {
         return currentProperty(UI_URL_KEYS, "env.ui", def);
    }

    public static String getCurrentApiUrl() {
         return currentProperty(API_URL_KEYS, "env.api", api);
    }

    public static String getCurrentClientDomain() {
         return currentProperty(CLIENT_URL_KEYS, "env.client", def);
    }

    public static String getCurrentAdminDomain() {
         String envAdmin = env("env.admin", admin);
         String key = ADMIN_URL_KEYS.get(envAdmin);
         if (key == null) {
             throw new IllegalStateException(
                     "Прод-контур не поддерживает админку: env.admin='" + envAdmin +
                             "' (нет admin URL). Используйте admin1/admin2/admin3 или" +
                             "добавьте в config.properties."
             );
         }
         return properties.getProperty(key);
    }

    public static String getTestEmail() {
         return currentProperty(EMAIL_KEYS, "env.email", def);
    }

    public static String getAffRid() {
         return currentProperty(AFF_RID_KEYS, "env.ui", def);
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
        public static final String MAIN_PAGE_TITLE = "TutorPlace - Платформа для обучения, курсы по подписке, онлайн школа";
    }
}