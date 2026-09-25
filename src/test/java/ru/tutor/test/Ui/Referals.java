package ru.tutor.test.Ui;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.PurchaseFlow;
import ru.tutor.flow.ReferralChainFlow;
import ru.tutor.flow.RenewalModalFlow;
import ru.tutor.flow.RevshareFlow;
import ru.tutor.page.Registration;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.AccountSteps;
import ru.tutor.steps.AdminSteps;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.OrderSteps;
import ru.tutor.steps.OrderToggleSubscriptionSteps;
import ru.tutor.steps.club.ClubSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Epic("Реферальная цепочка клуба")
@Feature("Создание цепочки рефералов")
@DisplayName("🧪 Реферальная цепочка клуба (по этапам)")
public class Referals extends BaseUiTest {

    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
    private static final int PARENT_EXPECTED_BALANCE = 3300;

    private ReferralChainFlow referralChainFlow;

    @BeforeEach
    void setUp() {
        TtPage ttPage = new TtPage();
        CloudPage cloudPage = new CloudPage();
        SberPage sberPage = new SberPage();

        OrderSteps orderSteps = new OrderSteps(BASE_URL);
        RevshareFlow revshareFlow = new RevshareFlow(cloudPage, orderSteps);
        RenewalModalFlow renewalModalFlow = new RenewalModalFlow(
                new Registration(), new Product(), new UnlockNextWeek(), cloudPage,
                ttPage, sberPage, revshareFlow,
                new OrderToggleSubscriptionSteps(BASE_URL)
        );
        referralChainFlow = new ReferralChainFlow(
                new PurchaseFlow(ttPage, cloudPage, sberPage),
                cloudPage,
                renewalModalFlow,
                new AdminSteps(BASE_URL),
                new AuthSteps(BASE_URL),
                new AccountSteps(BASE_URL),
                new ClubSteps(BASE_URL)
        );
    }

    @Test
    @Tag("UI + API")
    @DisplayName("Создание цепочки членов клуба")
    void createParentWithReferralLink() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        ReferralChainFlow.Chain chain = step("Создание реферальной цепочки",
                () -> referralChainFlow.buildChain());
        step("Проверка участников цепочки",
                () -> referralChainFlow.validateChainMembers(chain));

        step("Этап 5: проверка баланса Родителя после цепочки",
                () -> referralChainFlow.verifyParentBalance(chain.parent(), PARENT_EXPECTED_BALANCE));
    }

    @Test
    @Tag("UI + API")
    @DisplayName("Заполнение реквизитов ИП: summary → PUT → GET → DELETE → GET null → profileValid=false")
    void fillIpPayoutRequisites() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заполнение реквизитов ИП и проверка ручки summary",
                () -> referralChainFlow.fillRequisitesAndVerifySummary());
    }

    @Test
    @Tag("UI + API")
    @DisplayName("Заявка на вывод 3000 и отклонение внутри заявки админка (returnBonus)")
    void createWithdrawalAndReject() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заявка на вывод и отклонение в админке",
                () -> referralChainFlow.createWithdrawalAndReject());
    }

    @Test
    @Tag("UI + API")
    @DisplayName("Заявка на вывод 3000 и отклонение внутри заявки админка (returnBonus=false)")
    void createWithdrawalAndRejectNotReturnBonus() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заявка на вывод и отклонение в админке",
                () -> referralChainFlow.createWithdrawalAndRejectNotReturnBonus());
    }

    @Test
    @Tag("UI + API")
    @DisplayName("Заявка на вывод 3000 и удаление в админке (returnBonus=true)")
    void createWithdrawalAndDelete() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заявка на вывод и удаление в админке",
                () -> referralChainFlow.createWithdrawalAndDelete());
    }

    @Test
    @Tag("UI + API")
    @DisplayName("Заявка на вывод 3000 и удаление (не возвращать returnBonus)")
    void createWithdrawalAndDeleteNotReturnBonus() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заявка на вывод и удаление в админке",
                () -> referralChainFlow.createWithdrawalAndDeleteNotReturnBonus());
    }

    @Test
    @Tag("UI + API")
    @DisplayName("Согласованная Заявка на выплату 3000")
    void createWithdrawalAndPay() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Согласованная Заявка на выплату средств ",
                () -> referralChainFlow.withdrawalPayment());
    }
}