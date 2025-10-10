package ru.tutor.steps;

import io.qameta.allure.Step;
import org.openqa.selenium.support.ui.Wait;
import static com.codeborne.selenide.Selenide.Wait;
import ru.tutor.page.SuccessOrderPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.UrlRedirectHelper;

import static io.qameta.allure.Allure.step;
import static java.time.Duration.ofSeconds;

public class OrderSteps {

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
