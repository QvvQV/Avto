package ru.tutor.page;

/**
 * Указание основных адресов платформы
 */
public class AddressPage {

    public static final String param = "aff_rid";
    public static final String value = "1012_EUeg";
    public static final String recovery_from_email = "recovery_from_email_pitanie_v1";
    public static final String utm_source = "&utm_source=customutm";

    // eхcel продукт
    public static final String urlExcel = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/catalog/product/excel?aff_rid=1012_ESVn";

    // главная страница
    public static final String urlMain = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru";

    public static final String urlMainRev = "https://admin:JWwppsEA84B4BozJgE44sNiZZ@ttplace.ru/?" + param + "=" + value;

//    public static final String URL_MAIN = AUTH_BASE_URL;

    // стр для регистрации зарегистрированного пользователя
    public static final String login = "https://client.ttplace.ru/login";

    // стр регистрации нового пользователя
    public static final String register = "https://client.ttplace.ru/register";

    //стр admin Dev
    public static final String devAdmin = "https://admin.ttplace.ru";

    // стр продукта под задачу TP-5345 Поля "навыки" для новых карточек в настройке продукта
    public static final String product525 = "https://admin.ttplace.ru/product/edit/525";

    //стр для Api тестов
    public static final String apiDevAdress = "https://api.dev.tutorplace.ru";

    // стр Отписки от Premium
    public static final String SUBSCRIPTION_SETTINGS = "https://client.ttplace.ru/user/settings/subscription";

    // стр отписки от Lite
    public static final String PAYMENTS_SETTINGS = "https://client.ttplace.ru/user/settings/payments";

    // ссылка на Revshare Prod
    public static final String PAY_WITH_REVSHARE = "https://tester.tutorplace.ru/?aff_rid=1012_ESVn";
}
