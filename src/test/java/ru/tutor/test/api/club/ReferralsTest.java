package ru.tutor.test.api.club;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.base.RecurrentPaymentTestBase;
import ru.tutor.flow.AdminFlow;
import ru.tutor.flow.PurchaseFlow;
import ru.tutor.flow.club.ReferralFlow;
import ru.tutor.test.executor.RecurrentPaymentTestExecutor;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Реферальная цепочка клуба")
public class ReferralsTest extends RecurrentPaymentTestBase {

    private ReferralFlow referralFlow;
    private RecurrentPaymentTestExecutor testExecutor;
    private PurchaseFlow purchaseFlow;
    private AdminFlow adminFlow;

    @BeforeEach
    void setUpTest() {
        super.initializePaymentCycleExecutor();
        purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
        adminFlow = new AdminFlow(adminOrdersPage);
        testExecutor = new RecurrentPaymentTestExecutor(
                orderDatesPage,
                adminOrderDetails,
                adminOrdersPage,
                paymentCycleExecutor
        );

        referralFlow = new ReferralFlow(
                ttPage, cloudPage, sberPage, adminOrdersPage, adminOrderDetails, testExecutor, context, purchaseFlow, adminFlow
        );
    }

    @Tag("UI + API")
    @Test
    @DisplayName("Создание цепочки из 4 членов клуба")
    void createFourLevelReferralChain() {
        List<String> referralLinks = referralFlow.createReferralChain(2);

        step("Проверка создания цепочки", () -> {
            assertEquals(2, referralLinks.size());
        });

        referralFlow.verifyFullChainStatistics();
    }

    /**
     * зачисление по клубу (проведение рекурентного платежа или пожизненного доступа на аккаунте реферала)
     * списание средств по клубу
     * изменения статуса в клубе - не член клуба, может, не может выводить средства (это зависит от успеха последнего рекуррента)
     */

}