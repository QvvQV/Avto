package ru.tutor.steps;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.page.addressPage;
import ru.tutor.utils.WaitHelper;

import static io.qameta.allure.Allure.step;

/**
 * класс для добавления aff_rid значений в url ЛК
 */

public class UrlModificationSteps {

    @Step("Добавить параметр aff_rid к текущему URL")
    public static void addAffiliateParameter(){
        String currentUrl = WebDriverRunner.url();
        Allure.addAttachment("Текущий Url: ", currentUrl);
        String modifiedUrl = String.format("%s?%s=%s",
                currentUrl,
                addressPage.param,
                addressPage.value);

        Allure.addAttachment("Модифицированный URl: ",  modifiedUrl);

        // Открываем новый URl
        step("Открываем URL c aff_rid параметром: ", () -> {
            Selenide.open(modifiedUrl);
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
        });

        Allure.addAttachment("Финальный Url после перехода: ", WebDriverRunner.url());
    }

    @Step("Добавить параметр aff_rid и модалку email-рассылки к текущему URL")
    public static void addAffiliateParameterByEmail(){
        String currentUrl = WebDriverRunner.url();
        Allure.addAttachment("Текущий Url: ", currentUrl);
//        String modifiedUrl = (currentUrl + "?" + addressPage.param + "=" + addressPage.value);
        String modifiedUrl = String.format("%s?%s=%s&%s",
                currentUrl,
                addressPage.param,
                addressPage.value,
                addressPage.recovery_from_email);

        Allure.addAttachment("Модифицированный URl: ",  modifiedUrl);

        // Открываем новый URl
        step("Открываем URL c aff_rid параметром: ", () -> {
            Selenide.open(modifiedUrl);
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
        });

        Allure.addAttachment("Финальный Url после перехода: ", WebDriverRunner.url());
    }
}
