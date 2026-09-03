package ru.tutor.flow;

import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.steps.OrderSteps;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static io.qameta.allure.Allure.step;

public class RevshareFlow {

    private final CloudPage cloudPage;
    private final OrderSteps orderSteps;

    public RevshareFlow(CloudPage cloudPage, OrderSteps orderSteps) {
        this.cloudPage = cloudPage;
        this.orderSteps = orderSteps;
    }

    @Step("Оплата по ссылке PROD с полной проверкой")
    public void executeProd() {
        step("Открытие страницы", () -> open(TestConfig.getProdBaseUrl()));

        cloudPage.clickBtnStartProd();
        cloudPage.clickCardPageBtnProd();
        orderSteps.validateSuccessfulPayment(cloudPage);

//        validateSuccessfulPayment();
    }

//    @Step("Полная проверка успешной оплаты")
//    public void validateSuccessfulPayment() {
//        OrderSteps orderSteps = new OrderSteps(TestConfig.getCurrentApiUrl());
//
//        String href = cloudPage.verifySuccessProfilePage();
//        String orderNumber = orderSteps.extractOrderNumberFromUrl(
//                com.codeborne.selenide.WebDriverRunner.getWebDriver().getCurrentUrl()
//        );
//        String loginToken = orderSteps.extractQueryParam(href, "loginToken");
//        String password = orderSteps.extractQueryParam(href, "password");
//
//        sleep(4000);
//
//        orderSteps.validateOrderStatusResponse(
//                orderSteps.getOrderStatus(orderNumber)
//        );
//
//        orderSteps.validateOrderSuccessResponse(
//                orderSteps.getOrderSuccess(orderNumber),
//                orderNumber, loginToken, password
//        );
//    }
}
