package ru.tutor.flow.tariff;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.purchase.PurchaseFlow;
import ru.tutor.flow.purchase.PurchaseResult;
import ru.tutor.flow.account.RenewalModalFlow;
import ru.tutor.model.account.UserEditDataRequest;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.paymentModalWindow.PaymentModal;
import ru.tutor.steps.account.AccountSteps;
import ru.tutor.steps.admin.AdminSteps;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.utils.RewardHelper;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LifetimeTariffFlow {

    private static final int EXTEND_ACCESS_DAYS = -20;
    private final Faker faker = new Faker(Locale.ENGLISH);

    private final PurchaseFlow purchaseFlow;
    private final CloudPage cloudPage;
    private final UnlockNextWeek unlockNextWeek;
    private final RenewalModalFlow renewalModalFlow;
    private final AuthSteps authSteps;
    private final AccountSteps accountSteps;
    private final AdminSteps adminSteps;
    private final OrderSteps orderSteps;

    public LifetimeTariffFlow(PurchaseFlow purchaseFlow, CloudPage cloudPage,
                              UnlockNextWeek unlockNextWeek, RenewalModalFlow renewalModalFlow,
                              AuthSteps authSteps, AccountSteps accountSteps,
                              AdminSteps adminSteps, OrderSteps orderSteps) {
        this.purchaseFlow = purchaseFlow;
        this.cloudPage = cloudPage;
        this.unlockNextWeek = unlockNextWeek;
        this.renewalModalFlow = renewalModalFlow;
        this.authSteps = authSteps;
        this.accountSteps = accountSteps;
        this.adminSteps = adminSteps;
        this.orderSteps = orderSteps;
    }

    @Step("Создание пожизненного доступа у неактивного пользователя")
    public void createLifetimeForInactiveUser() {
        PurchaseResult result = purchaseFlow.premium();
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);
        assertNotNull(result.email(), "email не должен быть null");

        deactivateViaAdminApi(result.email());
        preparePersonalAccountAndPayTariff(result.email(), credentials);
        payLifetimeViaUi();
    }

    @Step("Деактивация доступа через админ-API (PUT /admin/orders/extend-access)")
    private void deactivateViaAdminApi(String email) {
        step("Действия в админке через API", () -> {
            adminSteps.login();
            String orderId = adminSteps.findOrderId(email);
            adminSteps.extendAccessDays(orderId, EXTEND_ACCESS_DAYS);
        });
    }

    private String preparePersonalAccount(String email, OrderSteps.PaymentCredentials credentials) {
        String token = authSteps.loginViaRefreshToken(email, credentials.password());

        accountSteps.validateEditPasswordSuccess(
                accountSteps.editPassword(token, credentials.password(), TestConfig.getTestPassword())
        );
        accountSteps.validateEditDataSuccess(
                accountSteps.editUserData(token,
                        new UserEditDataRequest().name(faker.name().name()).gender(0).email(email)));
        step("Обновление страницы", Selenide::refresh);
        return token;
    }

    @Step("Вход в ЛК, настройка пароля и данных через API, POST pay-lifetime-tariff")
    private void preparePersonalAccountAndPayTariff(String email, OrderSteps.PaymentCredentials credentials) {

        String token = preparePersonalAccount(email, credentials);
        orderSteps.validatePayLifetimeTariffResponse(orderSteps.payLifetimeTariff(token));
    }

    @Step("Покупка пожизненного через UI: модалка безлимита -> Cloud iframe -> продукт 563")
    private void payLifetimeViaUi() {
        renewalModalFlow.openFirstNonOnboardingProductCard();
        unlockNextWeek.openLifetimeAccess();
        cloudPage.clickCardPageBtn();
        WaitHelper.waitForPageLoad();
        renewalModalFlow.verifyProductPresent(563);
    }

    @Step("Покупка Реневалла через UI: модалка реневалла -> Cloud iframe -> продукт 264")
    private void openAccessAndPayViaUi() {
        renewalModalFlow.openFirstNonOnboardingProductCard();
        new PaymentModal().clickOpenAccess();
        cloudPage.clickCardPageBtn();
        WaitHelper.waitForPageLoad();
        renewalModalFlow.verifyProductPresent(264);
    }

    @Step("Покупка пожизненного у inactive-пользователя (prod)")
    public void createLifetimeForInactiveUserProd() {
        String token = authSteps.loginViaRefreshToken(
                TestConfig.getRenewalEmailInactive(),
                TestConfig.getTestPassword()
        );
        WaitHelper.waitForPageLoad();

        new RewardHelper().safeClickCollectRewardIfAppears(Duration.ofSeconds(4));
        renewalModalFlow.openFirstNonOnboardingProductCard();
        unlockNextWeek.openLifetimeAccess();
        renewalModalFlow.assertPaymentFrameVisible();

        orderSteps.validatePayLifetimeTariffResponse(orderSteps.payLifetimeTariff(token));
    }

    @Step("Реневалл: 'Открыть доступ' у неактивного пользователя (dev)")
    public void createRenewalForInactiveUser() {
        PurchaseResult result = purchaseFlow.premium();
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);
        assertNotNull(result.email(), "email не должен быть null");

        deactivateViaAdminApi(result.email());
        preparePersonalAccount(result.email(), credentials);
        openAccessAndPayViaUi();
    }

    @Step("Реневалл: 'Открыть дотуп' -> Cloud iframe (prod)")
    public void createRenewalForInactiveUserProd() {
        authSteps.loginViaRefreshToken(TestConfig.getRenewalEmailInactive(), TestConfig.getTestPassword());
        WaitHelper.waitForPageLoad();

        new RewardHelper().safeClickCollectRewardIfAppears(Duration.ofSeconds(4));
        renewalModalFlow.openFirstNonOnboardingProductCard();
        new  PaymentModal().clickOpenAccess();
        renewalModalFlow.assertPaymentFrameVisible();
    }
}
