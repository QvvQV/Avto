package ru.tutor.steps;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.constants.AddressPage;
import ru.tutor.utils.WaitHelper;

import java.net.URI;
import java.net.URISyntaxException;

import static io.qameta.allure.Allure.step;

/*
 * класс для добавления aff_rid значений в url ЛК
 */

public class UrlModificationSteps {

//    @Step("Добавить параметр aff_rid к текущему URL")
//    public static void addAffiliateParameter(){
//        String currentUrl = WebDriverRunner.url();
//        if (currentUrl == null){
//            currentUrl = Configuration.baseUrl;
//        }
//        Allure.addAttachment("Текущий Url: ", currentUrl);
//        String modifiedUrl = String.format("%s?%s=%s",
//                currentUrl,
//                AddressPage.param,
//                AddressPage.value);
//
//        Allure.addAttachment("Модифицированный URl: ",  modifiedUrl);
//
//        // Открываем новый URl
//        step("Открываем URL c aff_rid параметром: ", () -> {
//            Selenide.open(modifiedUrl);
//            WaitHelper.waitForPageLoad();
//            WaitHelper.waitForAnimationComplete();
//        });
//
//        Allure.addAttachment("Финальный Url после перехода: ", currentUrl);
//    }
//
//    @Step("Добавить параметр aff_rid и модалку email-рассылки к текущему URL")
//    public static void addAffiliateParameterByEmail(){
//        String currentUrl = WebDriverRunner.url();
//        if (currentUrl == null){
//            currentUrl = Configuration.baseUrl;
//        }
//        Allure.addAttachment("Текущий Url: ", currentUrl);
////        String modifiedUrl = (currentUrl + "?" + addressPage.param + "=" + addressPage.value);
//        String modifiedUrl = String.format("%s?%s=%s&%s",
//                currentUrl,
//                AddressPage.param,
//                AddressPage.value,
//                AddressPage.recovery_from_email);
//
//        Allure.addAttachment("Модифицированный URl: ",  modifiedUrl);
//
//        // Открываем новый URl
//        step("Открываем URL c aff_rid параметром: ", () -> {
//            Selenide.open(modifiedUrl);
//            WaitHelper.waitForPageLoad();
//            WaitHelper.waitForAnimationComplete();
//        });
//
//        Allure.addAttachment("Финальный Url после перехода: ", currentUrl);
//    }

    @Step("Добавить параметр aff_rid к текущему URL")
    public static void addAffiliateParameter() {
        String currentUrl = getCurrentUrl();
        String modifiedUrl = buildUrlWithParams(currentUrl,
                AddressPage.PARAM + "=" + AddressPage.value);

        logUrlModification(currentUrl, modifiedUrl);
        openUrl(modifiedUrl);
    }

    @Step("Добавить параметр aff_rid и модалку email-рассылки к текущему URL")
    public static void addAffiliateParameterByEmail() {
        String currentUrl = getCurrentUrl();
        String modifiedUrl = buildUrlWithParams(currentUrl,
                AddressPage.PARAM + "=" + AddressPage.VALUE,
                AddressPage.RECOVERY_FROM_EMAIL);

        logUrlModification(currentUrl, modifiedUrl);
        openUrl(modifiedUrl);
    }

    @Step("Добавить параметр к текущему URL")
    public static void removeAffiliateParameterByEmail() {
        String currentUrl = getCurrentUrl();
        String modifiedUrl = buildUrlWithParams(currentUrl,
                AddressPage.RECOVERY_FROM_EMAIL);

        logUrlModification(currentUrl, modifiedUrl);
        openUrl(modifiedUrl);
    }

    private static String getCurrentUrl(){
        String url = WebDriverRunner.url();
        return (url != null && !url.isEmpty()) ? url : Configuration.baseUrl;
    }

    private static String buildUrlWithParams(String baseUrl, String... params) {
        try {
            URI uri = new URI(baseUrl);
            StringBuilder query = new StringBuilder();

            if (uri.getQuery() != null) {
                query.append(uri.getQuery()).append("&");
            }

            query.append(String.join("&", params));

            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(),
                    query.toString(), uri.getFragment()).toString();
        } catch (URISyntaxException e) {
            String separator = baseUrl.contains("?") ? "&" : "?";
            return baseUrl + separator + String.join("&", params);
        }
    }

    private static void logUrlModification(String original, String modified) {
        Allure.addAttachment("Исходный URL ", original);
        Allure.addAttachment("Модифицированный URL ", modified);
    }

    private static void openUrl(String url) {
        step("Открытие модифицированного URL", () -> {
            Selenide.open(url);
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
        });
    }
}
