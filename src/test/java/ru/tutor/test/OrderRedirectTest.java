package ru.tutor.test;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.page.*;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;

import static io.qameta.allure.Allure.step;

@Epic("Order Processing")
@Feature("Order Redirect Handling")
@Story("Order completion with URL modifications")
@DisplayName("🛒 Тесты обработки редиректов при заказе")
class OrderRedirectTest extends BaseUiTest {

    private static final TtPage ttPage = new TtPage();
    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final ru.tutor.flow.SubscriptionTestFlow flow = new ru.tutor.flow.SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);

    @Test
//    @RepeatedTest(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешное оформление заказа с коррекцией редиректа")
    void successfulOrderWithRedirectCorrection() {
        // 1. Выполняем оформление заказа
        flow.purchasePremiumSubscription();

        // 2. Обработка редиректа после оплаты
        step("2. Обработка редиректа после оплаты", new RedirectPage()::redirectPage
        );
        }
    }