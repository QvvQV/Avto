package ru.tutor.test.orders;

import io.qameta.allure.AllureId;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.flow.SubscriptionTestFlow;
import ru.tutor.page.CloudPage;
import ru.tutor.page.SberPage;
import ru.tutor.page.AddressPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.TtPage;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;


public class CloudTest extends BaseUiTest {

    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();

    private final SubscriptionTestFlow flow = new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);

//    @Test
    @AllureId("1")
    //        @RepeatedTest(2)
    @DisplayName("Оплата с главной на СисАк Oфферум")
    public void PayFromMainOfferrum() {
        step("Открытие страницы", () ->
            open(AddressPage.urlMainRev)
        );
        flow.purchasePremiumSubscription();
    }

    @Test
//    @RepeatedTest(3)
    @AllureId("3")
    @DisplayName("Оплата с главной за 99 руб")
    public void PayMain99() {

        flow.purchaseLiteSubscription();

    }

    @Test
    @AllureId("2")
//        @RepeatedTest(2)
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

//        @Test
//            @RepeatedTest(5)
    @DisplayName("Оплата по ссылке revshare PROD")
    public void PayFromRevshare() {
        step("Открытие страницы", () ->
            open(AddressPage.PAY_WITH_REVSHARE)
        );

        CloudPage.clickBtnStartProd();
        CloudPage.clickCardPageBtnProd();
        sleep(1000);
    }
}