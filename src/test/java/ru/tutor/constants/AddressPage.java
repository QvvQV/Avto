package ru.tutor.constants;

import ru.tutor.config.TestConfig;

/**
 * Указание основных адресов платформы
 */
public class AddressPage {

    public static final String param = "aff_rid";
    public static final String value = "1012_EUz3";
    public static final String recovery_from_email = "recovery_from_email_pitanie_v1";
    public static final String utm_source = "&utm_source=customutm";

    // ==================== UI URL ====================
    /** Главная страница */
    public static final String urlMain = TestConfig.getCurrentUiUrl();
    public static final String urlMainRev = urlMain + "?" + param + "=" + value;

    /** Excel продукт */

    public static final String urlExcel = urlMain + "/catalog/product/excel" + "?" + param + "=" + value;

//    public static final String URL_MAIN = AUTH_BASE_URL;

    // ==================== Клиентские URL ====================

    private static final String CLIENT = TestConfig.getClientBaseUrl();

    // ==================== Динамические URL ====================
    public static final String login = CLIENT + "/login";
    public static final String register = CLIENT + "/register";
    public static final String SUBSCRIPTION_SETTINGS = CLIENT + "/user/settings/subscription"; // стр Отписки от Premium
    public static final String PAYMENTS_SETTINGS = CLIENT + "/user/settings/payments";  // стр отписки от Lite


    // ==================== Admin URL ====================
    //стр admin Dev
    public static final String devAdmin = TestConfig.getAdminBaseUrl();

    // стр продукта под задачу TP-5345 Поля "навыки" для новых карточек в настройке продукта
    public static final String product525 = TestConfig.getAdminProductUrl(525);

    // ==================== Специальные ссылки ====================

    // ссылка на Revshare Prod
    public static final String PAY_WITH_REVSHARE = "https://tester.tutorplace.ru/?aff_rid=1012_ESVn";

    // Для отладки
    static {
        System.out.println("🌐 UI URL: " + urlMain);
        System.out.println("🔧 API URL: " + ApiEndpoints.apiBaseUrl);
        System.out.println("⚙️ Admin URL: " + devAdmin);
    }
}

// ==== Устаревшие домены ====
//    public static final String urlMain = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru";
//    public static final String urlMainRev = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/?" + param + "=" + value;
//    public static final String urlExcel = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel?aff_rid=1012_ESVn";
//стр для Api тестов
//    public static final String apiDevAdress = "https://api.dev.tutorplace.ru";