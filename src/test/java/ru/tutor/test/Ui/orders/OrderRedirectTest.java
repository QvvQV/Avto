package ru.tutor.test.Ui.orders;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.flow.purchase.PurchaseFlow;
import ru.tutor.flow.purchase.PurchaseResult;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.RedirectPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ru.tutor.constants.Tags;

@Epic("Order Processing")
@Feature("Order Redirect Handling")
@Story("Order completion with URL modifications")
@DisplayName("🛒 Тесты обработки редиректов при заказе")
@Tag(Tags.E2E)
class OrderRedirectTest extends BaseUiTest {

    private static final TtPage ttPage = new TtPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final PurchaseFlow purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);

    @Test
//    @RepeatedTest(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешное оформление заказа с коррекцией редиректа")
    void successfulOrderWithRedirectCorrection() {
        // 1. Выполняем оформление заказа
        PurchaseResult result = purchaseFlow.premium();
        assertNotNull(result.email());

        // 2. Обработка редиректа после оплаты
        step("2. Обработка редиректа после оплаты", new RedirectPage()::redirectPage
        );
        }
    }