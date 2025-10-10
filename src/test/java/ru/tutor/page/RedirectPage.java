package ru.tutor.page;

import ru.tutor.steps.OrderSteps;
import ru.tutor.utils.WaitHelper;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RedirectPage {

    private static final OrderSteps orderSteps = new OrderSteps();

    public static void redirectPage() throws InterruptedException {
        WaitHelper.waitForPaymentProcessing();
//        CloudPage.waitForPaymentProcessing();

        // 3. Ожидаемый URL редиректа после оформления
        // начиная с этого момента нужно переиспользовать код
        String expectedRedirectUrl = "https://dev.tutorplace.ru/success/";

        // 4. Обрабатываем редирект используя исправленные методы
        SuccessOrderPage successPage = orderSteps.simpleRedirectHandling(expectedRedirectUrl);

        WaitHelper.waitForPageLoad();

        // 5. Проверяем, что мы на правильном домене
        String currentUrl = com.codeborne.selenide.WebDriverRunner.getWebDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("ttplace.ru"),
            "Должны быть на домене ttplace.ru, а текущий URL: " + currentUrl);

        // 6. Проверяем и исправляем кнопку входа в ЛК
        String buttonHref = successPage.getLoginButtonHref();
        if (buttonHref != null && buttonHref.contains("client.tutorplace.ru")) {
            successPage.fixLoginButtonHrefByXPath(); // Используем альтернативный метод
        }

        // 7. Переходим в ЛК
        orderSteps.navigateToPersonalAccount(successPage);

        // 8. Проверяем, что попали в ЛК
        String finalUrl = com.codeborne.selenide.WebDriverRunner.getWebDriver().getCurrentUrl();
        assertTrue(finalUrl.contains("client.ttplace.ru"),
            "Должны быть в ЛК на client.ttplace.ru, а текущий URL: " + finalUrl);
    }
}
