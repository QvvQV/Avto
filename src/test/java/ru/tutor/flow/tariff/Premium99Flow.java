package ru.tutor.flow.tariff;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.purchase.PurchaseFlow;
import ru.tutor.flow.purchase.PurchaseResult;
import ru.tutor.flow.account.RenewalModalFlow;
import ru.tutor.model.account.UserEditDataRequest;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.personalAccount.HotNovelties;
import ru.tutor.steps.account.AccountSteps;
import ru.tutor.steps.admin.AdminSteps;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.utils.RewardHelper;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.refresh;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Premium99Flow {

    private final Faker faker = new Faker(Locale.ENGLISH);
    private final HotNovelties hotNovelties = new HotNovelties();

    private final PurchaseFlow purchaseFlow;
    private final CloudPage cloudPage;
    private final RenewalModalFlow renewalModalFlow;
    private final AuthSteps authSteps;
    private final AccountSteps accountSteps;
    private final AdminSteps adminSteps;
    private final OrderSteps orderSteps;

    private String userEmail;

    public Premium99Flow(PurchaseFlow purchaseFlow, CloudPage cloudPage,
                         RenewalModalFlow renewalModalFlow,
                         AuthSteps authSteps, AccountSteps accountSteps,
                         AdminSteps adminSteps, OrderSteps orderSteps) {
        this.purchaseFlow = purchaseFlow;
        this.cloudPage = cloudPage;
        this.renewalModalFlow = renewalModalFlow;
        this.authSteps = authSteps;
        this.accountSteps = accountSteps;
        this.adminSteps = adminSteps;
        this.orderSteps = orderSteps;
    }

    @Step("Премиум 99₽: заказ Лайт → подписка API (623,625) → Горячие новинки → Cloud → продукт 264")
    public void createPremium99FromHotNovelties() {
        step("Создание заказа Лайт", () -> {
            PurchaseResult result = purchaseFlow.lite();
            userEmail = result.email();
            assertNotNull(userEmail, "email не должен быть null");
        });

        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);

        step("Проверка создания заказа в админке (API)", () ->
                adminSteps.assertOrderExists(userEmail));

        String token = preparePersonalAccount(userEmail, credentials);

        subscribeToProducts(token);
        refresh();

        hotNovelties.scrollToBlock();
        hotNovelties.clickStartFirstCard();
        hotNovelties.clickYes();
        hotNovelties.clickPremiumAccess99();

        cloudPage.clickCardPageBtn();
        renewalModalFlow.verifyProductPresent(264);
    }

    @Step("Вход через refresh_token + приветственная модалка через API")
    private String preparePersonalAccount(String email, OrderSteps.PaymentCredentials credentials) {
        String token = authSteps.loginViaRefreshToken(email, credentials.password());

        accountSteps.validateEditPasswordSuccess(
                accountSteps.editPassword(token, credentials.password(), TestConfig.getTestPassword()));
        accountSteps.validateEditDataSuccess(
                accountSteps.editUserData(token,
                        new UserEditDataRequest().name(faker.name().name()).gender(0).email(email)));
        step("Обновление страницы", Selenide::refresh);
        return token;
    }

    @Step("Подписка на продукты через API: 623, 625")
    private void subscribeToProducts(String token) {
        step("Подписка на продукты через API", () -> {
            orderSteps.validateSubscribeResponse(orderSteps.subscribeToOrder(token, 623));
            orderSteps.validateSubscribeResponse(orderSteps.subscribeToOrder(token, 625));
        });
    }

    @Step("Премиум 99₽ (prod): login renewaltest@test.ru → Rewards → первый продукт → Горячие новинки 99₽ → Cloud iframe")
    public void createPremium99FromHotNoveltiesProd() {
        authSteps.loginViaRefreshToken(TestConfig.getRenewalEmail(), TestConfig.getTestPassword());
        WaitHelper.waitForPageLoad();

        new RewardHelper().safeClickCollectRewardIfAppears(Duration.ofSeconds(4));

        hotNovelties.scrollToBlock();
        hotNovelties.clickStartFirstCard();
        hotNovelties.clickYes();
        hotNovelties.clickPremiumAccess99();

        renewalModalFlow.assertPaymentFrameVisible();
    }
}