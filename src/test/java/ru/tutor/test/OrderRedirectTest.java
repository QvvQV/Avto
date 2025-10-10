package ru.tutor.test;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.page.*;
import ru.tutor.steps.OrderSteps;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.UrlRedirectHelper;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Order Processing")
@Feature("Order Redirect Handling")
@Story("Order completion with URL modifications")
@DisplayName("🛒 Тесты обработки редиректов при заказе")
class OrderRedirectTest extends BaseUiTest {

    private final OrderSteps orderSteps = new OrderSteps();

    @Test
//    @RepeatedTest(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешное оформление заказа с коррекцией редиректа")
    void successfulOrderWithRedirectCorrection() throws InterruptedException {
        // 1. Выполняем оформление заказа
        step("1. Начало оформления заказа", () -> {
        open(addressPage.urlMain);


        ttPage.clickBtnOpenFor();
        ttPage.clickPremium();});

        // 2. Заполнение данных и оплата
        step("Заполнение данных и оплата", () -> {
        CloudPage.setDateModal();
        AllureAttachments.attachText("Email пользователя", CloudPage.atrEmail());
            System.out.println("Оплата продукта с главной Премиум: " + CloudPage.atrEmail());

            CloudPage.clickCardPageBtn();
        });

        // 3. Обработка редиректа после оплаты
        step("3. Обработка редиректа после оплаты", () -> {

        RedirectPage.redirectPage();});
        }
    }