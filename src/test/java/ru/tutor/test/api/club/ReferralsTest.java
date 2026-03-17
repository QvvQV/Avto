package ru.tutor.test.api.club;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.base.RecurrentPaymentTestBase;
import ru.tutor.flow.ReferralFlow;
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
        List<String> referralLinks = referralFlow.createReferralChain(4);

        step("Проверка создания цепочки", () -> {
            assertEquals(4, referralLinks.size());
        });

        referralFlow.verifyFullChainStatistics();
    }
}