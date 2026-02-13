package ru.tutor.steps;

import io.qameta.allure.Step;
import ru.tutor.page.SuccessOrderPage;
import ru.tutor.utils.UrlRedirectHelper;

/*
 * Шаги для редиректа страницы после оплаты DEV
 */

public class OrderStepsRedirect {

    @Step("Завершить заказ и обработать редирект")
    public SuccessOrderPage completeOrderWithRedirectHandling(String orderPageUrl) {
        // Настраиваем перехват редиректов
        UrlRedirectHelper.interceptAndModifyRedirect(
            ".*tutorplace\\.ru.*",
            "ttplace.ru"
        );

        // Используем упрощенный метод для обработки редиректа
        return SuccessOrderPage.waitForRedirectAndOpen(orderPageUrl, 10000);
    }

    @Step("Перейти в ЛК через модифицированную кнопку")
    public void navigateToPersonalAccount(SuccessOrderPage successPage) {
        // Проверяем и исправляем href перед кликом
        String originalHref = successPage.getLoginButtonHref();

        if (originalHref != null && originalHref.contains("client.tutorplace.ru")) {
            successPage.fixLoginButtonHref();
        }

        successPage.clickLoginButton();
    }

    @Step("Простая обработка редиректа")
    public SuccessOrderPage simpleRedirectHandling(String redirectUrl) {
        // Просто открываем модифицированную страницу
        return SuccessOrderPage.openAndFixPage(redirectUrl);
    }
}
