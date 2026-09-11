package ru.tutor.flow;

import io.qameta.allure.Step;
import net.datafaker.Faker;
import ru.tutor.config.TestConfig;
import ru.tutor.model.LoginResponse;
import ru.tutor.model.UserEditDataRequest;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.steps.AccountSteps;
import ru.tutor.steps.AdminSteps;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.OrderSteps;
import ru.tutor.utils.WaitHelper;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;
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

    @Step("Вход в ЛК, настройка пароля и данных через API, POST pay-lifetime-tariff")
    private void preparePersonalAccountAndPayTariff(String email, OrderSteps.PaymentCredentials credentials) {
//        open(credentials.href());
//        WaitHelper.waitForPageLoad();

//        LoginResponse tokens = authSteps.loginAndGetToken(email, credentials.password());

        String token = loginViaRefreshToken(email, credentials.password());

        accountSteps.validateEditPasswordSuccess(
                accountSteps.editPassword(token, credentials.password(), TestConfig.getTestPassword())
        );
        accountSteps.validateEditDataSuccess(
                accountSteps.editUserData(token,
                        new UserEditDataRequest().name(faker.name().name()).gender(0).email(email)));
        refresh();

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

    @Step("Покупка пожизненного у inactive-пользователя (prod)")
    public void createLifetimeForInactiveUserProd() {
        String token = loginViaRefreshToken(
                TestConfig.getRenewalEmailInactive(),
                TestConfig.getTestPassword()
        );
        WaitHelper.waitForPageLoad();

        renewalModalFlow.openFirstNonOnboardingProductCard();
        unlockNextWeek.openLifetimeAccess();
        renewalModalFlow.assertPaymentFrameVisible();

        orderSteps.validatePayLifetimeTariffResponse(orderSteps.payLifetimeTariff(token));
    }

    private String loginViaRefreshToken(String email, String password) {
        LoginResponse tokens = authSteps.loginAndGetTokens(email, password);
        step("Переход в ЛК через refresh_token", () ->{
            open(AddressPage.LOGIN + "?refresh_token=" + tokens.refreshToken());
            WaitHelper.waitForPageLoad();
        });
        return tokens.token();
    }
}
