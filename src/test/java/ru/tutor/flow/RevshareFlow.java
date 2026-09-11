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
    public OrderSteps.PaymentCredentials executeProd() {
        step("Открытие страницы", () -> open(TestConfig.getProdBaseUrl()));

        cloudPage.clickBtnStartProd();
        cloudPage.clickCardPageBtnProd();
        return orderSteps.validateSuccessfulPayment(cloudPage);
    }
}
