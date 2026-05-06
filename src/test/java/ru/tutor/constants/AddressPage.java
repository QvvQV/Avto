package ru.tutor.constants;

import ru.tutor.config.TestConfig;

/**
 * Указание основных адресов платформы
 */
public class AddressPage {

    public static final String param = "aff_rid";
    public static final String value = "1012_EVqb";
    public static final String recovery_from_email = "recovery_from_email_pitanie_v1";
    public static final String utm_source = "&utm_source=customutm";

    // ==================== UI URL ====================
    /** Главная страница */
    public static final String URL_MAIN = TestConfig.getCurrentUiUrl();
    public static final String URL_MAINRev = URL_MAIN + "?" + param + "=" + value; // Прод

    /** Excel продукт */

    public static final String urlExcel = URL_MAIN + "/catalog/product/excel" + "?" + param + "=" + value;

//    public static final String URL_MAIN = AUTH_BASE_URL;

    // ==================== Клиентские URL ====================

    private static final String CLIENT = TestConfig.getCurrentClientDomain();

    // ==================== Динамические URL ====================
    public static final String login = CLIENT + "/login";
    public static final String register = CLIENT + "/register";
    public static final String SUBSCRIPTION_SETTINGS = CLIENT + "/user/settings/subscription"; // стр Отписки от Premium
    public static final String PAYMENTS_SETTINGS = CLIENT + "/user/settings/payments";  // стр отписки от Lite
    public static final String CATALOG = CLIENT + "/user/catalog";


    // ==================== Admin URL ====================
    //стр admin Dev
    public static final String DEV_ADMIN = TestConfig.getCurrentAdminDomain();

    // стр продукта под задачу TP-5345 Поля "навыки" для новых карточек в настройке продукта
    public static final String product525 = TestConfig.getAdminProductUrl(525);

    // ==================== Специальные ссылки ====================

    // Для отладки
    static {
        System.out.println("🌐 UI URL: " + URL_MAIN);
        System.out.println("🔧 API URL: " + ApiEndpoints.apiBaseUrl);
        System.out.println("⚙️ Admin URL: " + DEV_ADMIN);
        System.out.println("👤 Client URL: " + login);
    }
}