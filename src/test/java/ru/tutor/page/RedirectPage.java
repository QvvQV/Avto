package ru.tutor.page;

import ru.tutor.steps.OrderStepsRedirect;
import ru.tutor.utils.ConfigReader;
import ru.tutor.utils.WaitHelper;

import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Редирект для deva. Попасть после заказа в ЛК пользователя UI
 */

public class RedirectPage {

    private static final OrderStepsRedirect ORDER_STEPS_REDIRECT = new OrderStepsRedirect();

    public static void redirectPage() {
        WaitHelper.waitForPaymentProcessing();

        // 3. Ожидаемый URL редиректа после оформления
        // начиная с этого момента нужно переиспользовать код
        String expectedRedirectUrl = "https://ttplace.online/success/";

        // 4. Обрабатываем редирект используя исправленные методы
        SuccessOrderPage successPage = ORDER_STEPS_REDIRECT.simpleRedirectHandling(expectedRedirectUrl);

        WaitHelper.waitForPageLoad();

        // 5. Проверяем, что мы на правильном домене
        String currentUrl = com.codeborne.selenide.WebDriverRunner.getWebDriver().getCurrentUrl();
        assert currentUrl != null;
        assertTrue(currentUrl.contains(ConfigReader.get("api.base.url")),
            "Должны быть на домене ttplace.ru, а текущий URL: " + currentUrl);

        // 6. Проверяем и исправляем кнопку входа в ЛК
        String buttonHref = successPage.getLoginButtonHref();
        if (buttonHref != null && buttonHref.contains("client.tutorplace.ru")) {
            successPage.fixLoginButtonHrefByXPath(); // Используем альтернативный метод
        }

        // 7. Переходим в ЛК
        ORDER_STEPS_REDIRECT.navigateToPersonalAccount(successPage);

        // 8. Проверяем, что попали в ЛК
        String finalUrl = com.codeborne.selenide.WebDriverRunner.getWebDriver().getCurrentUrl();
        assertTrue(finalUrl.contains("client.ttplace.ru"),
            "Должны быть в ЛК на client.ttplace.ru, а текущий URL: " + finalUrl);
    }
}