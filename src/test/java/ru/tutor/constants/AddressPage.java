package ru.tutor.constants;

import ru.tutor.config.TestConfig;

/**
 * Указание основных адресов платформы
 */
public class AddressPage {

    public static final String PARAM = "?aff_rid=";
    public static final String VALUE = TestConfig.getAffRid();
    public static final String RECOVERY_FROM_EMAIL = "RECOVERY_FROM_EMAIL_pitanie_v1";
    public static final String CARDIO_DOMAIN_HOST = TestConfig.getCurrentAffRidUrl();
    public static final String CARDIO_DOMAIN_URL = CARDIO_DOMAIN_HOST + "/domain";
    public static final String UTM_SOURCE = "&utm_source=cpaws";

    // ==================== UI URL ====================
    /** Главная страница */
    public static final String URL_MAIN = TestConfig.getCurrentUiUrl();
    public static final String URL_MAIN_REV = URL_MAIN + PARAM + VALUE; // Прод
    public static final String CARDIO_DOMAIN_URL_AFF_RID = CARDIO_DOMAIN_HOST + PARAM + VALUE;

    /** Excel продукт */

    public static final String URL_EXCEL = URL_MAIN + "/catalog/product/excel" + PARAM + VALUE;

//    public static final String URL_MAIN = AUTH_BASE_URL;

    // ==================== Клиентские URL ====================

    private static final String CLIENT = TestConfig.getCurrentClientDomain();

    // ==================== Динамические URL ====================
    public static final String LOGIN = CLIENT + "/login";
    public static final String REGISTER = CLIENT + "/register";
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
        System.out.println("🔧 API URL: " + ApiEndpoints.API_BASE_URL);
        System.out.println("⚙️ Admin URL: " + DEV_ADMIN);
        System.out.println("👤 Client URL: " + LOGIN);
    }
}