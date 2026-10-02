package ru.tutor.test.Ui.club;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;
import ru.tutor.base.BaseUiTest;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.purchase.PurchaseFlow;
import ru.tutor.flow.club.ReferralChainFlow;
import ru.tutor.flow.account.RenewalModalFlow;
import ru.tutor.flow.purchase.RevshareFlow;
import ru.tutor.page.account.Registration;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.account.AccountSteps;
import ru.tutor.steps.admin.AdminSteps;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.steps.orders.OrderToggleSubscriptionSteps;
import ru.tutor.steps.club.ClubSteps;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import ru.tutor.constants.Tags;

@Epic("Реферальная цепочка клуба")
@Feature("Создание цепочки рефералов")
@DisplayName("🧪 Реферальная цепочка клуба (по этапам)")
public class Referrals extends BaseUiTest {

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
                new Product(), new UnlockNextWeek(), cloudPage,
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
    @Order(1)
    @Tag(Tags.E2E)
    @Tag(Tags.UI_API)
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
    @Order(2)
    @Tag(Tags.E2E)
    @Tag(Tags.UI_API)
    @DisplayName("Заполнение реквизитов ИП: summary → PUT → GET → DELETE → GET null → profileValid=false")
    void fillIpPayoutRequisites() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заполнение реквизитов ИП и проверка ручки summary",
                () -> referralChainFlow.fillRequisitesAndVerifySummary());
    }

    @Test
    @Order(3)
    @Tag(Tags.E2E)
    @Tag(Tags.UI_API)
    @DisplayName("Заполнение реквизитов СЗ: summary → PUT → GET → DELETE → GET null → profileValid=false")
    void fillSzPayoutRequisites() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заполнение реквизитов СЗ и проверка ручки summary",
                () -> referralChainFlow.fillSzRequisitesAndVerifySummary());
    }

    @Test
    @Order(4)
    @Tag(Tags.E2E)
    @Tag(Tags.UI_API)
    @DisplayName("Заявка на вывод 3000 и отклонение внутри заявки админка (returnBonus)")
    void createWithdrawalAndReject() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заявка на вывод и отклонение в админке",
                () -> referralChainFlow.createWithdrawalAndReject());
    }

    @Test
    @Order(5)
    @Tag(Tags.E2E)
    @Tag(Tags.UI_API)
    @DisplayName("Заявка на вывод 3000 и отклонение внутри заявки админка (returnBonus=false)")
    void createWithdrawalAndRejectNotReturnBonus() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заявка на вывод и отклонение в админке",
                () -> referralChainFlow.createWithdrawalAndRejectNotReturnBonus());
    }

    @Test
    @Order(6)
    @Tag(Tags.E2E)
    @Tag(Tags.UI_API)
    @DisplayName("Заявка на вывод 3000 и удаление в админке (returnBonus=true)")
    void createWithdrawalAndDelete() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заявка на вывод и удаление в админке",
                () -> referralChainFlow.createWithdrawalAndDelete());
    }

    @Test
    @Order(7)
    @Tag(Tags.E2E)
    @Tag(Tags.UI_API)
    @DisplayName("Заявка на вывод 3000 и удаление (не возвращать returnBonus)")
    void createWithdrawalAndDeleteNotReturnBonus() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Заявка на вывод и удаление в админке",
                () -> referralChainFlow.createWithdrawalAndDeleteNotReturnBonus());
    }

    @Test
    @Order(8)
    @Tag(Tags.E2E)
    @Tag(Tags.UI_API)
    @DisplayName("Согласованная Заявка на выплату 3000")
    void createWithdrawalAndPay() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Согласованная Заявка на выплату средств ",
                () -> referralChainFlow.withdrawalPayment());
    }

    @Test
    @Order(9)
    @Tag(Tags.E2E)
    @Tag(Tags.UI_API)
    @DisplayName("Премиум + Пожизненный (semi)")
    void premiumShareAndLife() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Премиум, Пожизненный, проверка summary",
                () -> referralChainFlow.premiumShareAndLifeSemi());
    }

    @Test
    @Order(10)
    @Tag(Tags.E2E)
    @Tag(Tags.UI_API)
    @DisplayName("Премиум без оплаты + Пожизненный: оплата родителя с баланса клуба (type=19)")
    void premiumShareAndLifePayFromClubBalance() {
        assumeTrue(!TestConfig.isProd(), "Тест запускается только на dev-окружение");

        step("Премиум, Пожизненный, оплата родителя с баланса клуба (type=19)",
                () -> referralChainFlow.premiumShareAndLifePayFromClubBalance());
    }
}