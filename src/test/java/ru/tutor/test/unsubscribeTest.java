package ru.tutor.test;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.tutor.Unsubscribe.LiteUnsubscribePage;
import ru.tutor.Unsubscribe.PremiumUnsubscribePage;
import ru.tutor.base.BaseUiTest;
import ru.tutor.page.CloudPage;
import ru.tutor.page.RedirectPage;
import ru.tutor.page.addressPage;
import ru.tutor.page.dashbords.helloDash;
import ru.tutor.page.ttPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;


@Story("Отписка пользователя от платформы")
public class unsubscribeTest extends BaseUiTest {

    @AfterEach
    protected void tearDown() {
        closeWebDriver();
    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Order(1)
    @DisplayName("✅ Успешное отписка пользователя от Премиума и Лайта")
    void successfulUnsubscriptionOfAUserFromPremiumAndLite() {

        step("1. Начало оформления заказа", () -> {
            open(addressPage.urlMain);


            ttPage.clickBtnOpenFor();
            ttPage.clickPremium();
        });
        step("2. Заполнение данных и оплата", () -> {
            CloudPage.setDateModal();
            AllureAttachments.attachText("Email пользователя", CloudPage.atrEmail());

            CloudPage.clickCardPageBtn();
        });
        step("3. Обработка редиректа после оплаты", () -> {

            RedirectPage.redirectPage();
        });
        step("4. Прохождение приветсвтенного onboarding", () -> {
            helloDash.fillingOutTheWelcomeWindow();
            AllureAttachments.attachText("Onbording", "Приветсвенные модули завершены");
            AllureAttachments.attachScreenshot("Скриншот главного экрана");
        });
        step("5. Переход к отпискам", () -> {
            open(addressPage.SUBSCRIPTION_SETTINGS);
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Страница отписки от Премиума");
        });

        step("6. Полный процесс отмены Premium подписки", () -> {
            PremiumUnsubscribePage.completePremiumUnsubscription();
        });
        step("7. Переход к отпискам", () -> {
            open(addressPage.PAYMENTS_SETTINGS);
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Страница отписки от Лайта");
        });
        step("8. Полный процесс отмены Lite подписки", () -> {
            LiteUnsubscribePage.completeLiteUnsubscription();
        });
    }
}
