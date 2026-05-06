package ru.tutor.test.api.club;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.base.RecurrentPaymentTestBase;
import ru.tutor.flow.club.ReferralFlow;
import ru.tutor.test.executor.RecurrentPaymentTestExecutor;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Реферальная цепочка клуба")
public class ReferralsTest extends RecurrentPaymentTestBase {

    private ReferralFlow referralFlow;
    private RecurrentPaymentTestExecutor testExecutor;

    @BeforeEach
    void setUpTest() {
        super.initializePaymentCycleExecutor();
        testExecutor = new RecurrentPaymentTestExecutor(
                orderDatesPage,
                adminOrderDetails,
                adminOrdersPage,
                paymentCycleExecutor
        );

        referralFlow = new ReferralFlow(
                ttPage, cloudPage, sberPage, adminOrdersPage, adminOrderDetails, testExecutor, context
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
     * изменения статуса в клубе - не член клуба, может, не может выводит средства (это зависит от успеха последнего рекуррента)
     */

}