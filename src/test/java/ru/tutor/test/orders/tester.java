package ru.tutor.test.orders;

import io.qameta.allure.AllureId;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.flow.SubscriptionTestFlow;
import ru.tutor.page.CloudPage;
import ru.tutor.page.SberPage;
import ru.tutor.page.addressPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.ttPage;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;


public class tester extends BaseUiTest {

    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final ttPage ttPage = new ttPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();

    private SubscriptionTestFlow flow = new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);

//    @Test
    @AllureId("1")
    //        @RepeatedTest(2)
    @DisplayName("Оплата с продукта exel")
    public void PayFromExcel() {
        step("Открытие страницы", () -> {
            open(addressPage.urlExcel);
        });
        step("проверяем title", () -> {
            String expected = "Excel-таблицы для начинающих - курс, онлайн обучение бесплатно по подписке";
            Assertions.assertEquals(expected, title());
        });

        CloudPage.clickBtnStart();
        System.out.println("Оплата с продукта Excel за 1 руб: " + CloudPage.atrEmail());
        CloudPage.clickCardPageBtn();
        sleep(1000);
    }

    @Test
    @AllureId("3")
    @DisplayName("Оплата с главной за 99 руб")
    public void PayMain99() {

        flow.purchaseLiteSubscription();

    }

    @Test
    @AllureId("2")
//        @RepeatedTest(3)
    @DisplayName("Оплата с главной за 399 руб")
    @Owner("Vadim")
    public void PayMain399() {

        flow.purchasePremiumSubscription();

    }

    @Test
    @AllureId("4")
    @DisplayName("Оплата с главной за Пожизненный")
    public void PayMainLife() {

        flow.purchaseLifeSubscription();

    }

    @Test
    @AllureId("5")
    @DisplayName("Оплата Долями с главной за Пожизненный")
    public void PayMainLifeInstallments() {

        flow.purchaseLifeInstallmentsSubscription();

    }
}