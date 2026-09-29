package ru.tutor.flow.account;

import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.modals.WelcomeBackModal;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.steps.AdminSteps;
import ru.tutor.steps.OrderSteps;
import ru.tutor.utils.AllureAttachments;

import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

public class PremiumWelcomeBackFlow {

    private final TtPage ttPage;
    private final CloudPage cloudPage;
    private final AdminSteps adminSteps;
    private final WelcomeBackModal welcomeBackModal = new WelcomeBackModal();

    public PremiumWelcomeBackFlow(TtPage ttPage, CloudPage cloudPage) {
        this(ttPage, cloudPage, new AdminSteps(TestConfig.getCurrentApiUrl()));
    }

    public PremiumWelcomeBackFlow(TtPage ttPage, CloudPage cloudPage, AdminSteps adminSteps) {
        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.adminSteps = adminSteps;
    }

    private String purchaseAndRepeat(TtPage.SubscriptionType first, TtPage.SubscriptionType repeat,
                                     Consumer<String> onRepeat) {
        step("Открытие главной и выбор тарифа «" + first.getDisplayName() + "»", () -> {
            open(AddressPage.URL_MAIN);
            ttPage.clickBtnOpenFor();
            ttPage.selectSubscription(first);
        });

        String email = cloudPage.setDateModal();
        cloudPage.clickCardPageBtn();
        OrderSteps.validateSuccessfulPayment(cloudPage);

        step("Повторная покупка «" + repeat.getDisplayName() + "» по тому же email", () -> {
            open(AddressPage.URL_MAIN);
            ttPage.clickBtnOpenFor();
            ttPage.selectSubscription(repeat);
            cloudPage.submitEmail(email);
        });

        onRepeat.accept(email);
        AllureAttachments.attachText("Email пользователя", email);
        return email;
    }

    @Step("Оплата {first} с главной → повтор {repeat} тем же email → «Рады вновь вас видеть»")
    public String purchaseAndCheckWelcomeBack(TtPage.SubscriptionType first,
                                              TtPage.SubscriptionType repeat) {
        return purchaseAndRepeat(first, repeat, email -> welcomeBackModal.assertWelcomeBackModal());
    }

    @Step("Оплата {first} с главной → оплата {target} тем же email → проверка тарифа в админке")
    public String purchaseAndPay(TtPage.SubscriptionType first, TtPage.SubscriptionType target) {
        return purchaseAndRepeat(first, target, email -> payAndVerifyTariffInAdmin(email, target));
    }

    private void payAndVerifyTariffInAdmin(String email, TtPage.SubscriptionType target) {
        step("Оплата «" + target.getDisplayName() + "» Cloud-картой", () -> {
            cloudPage.clickCardPageBtn();
            OrderSteps.validateSuccessfulPayment(cloudPage);
        });
        step("Проверка в админке: заказ с тарифом «" + target.getDisplayName() + "»", () ->
                adminSteps.assertOrderTariffForEmail(email, target.getDisplayName()));
    }

    private static String adminProductKeyword(TtPage.SubscriptionType type) {
        return switch (type) {
            case PREMIUM -> "Премиум";
            case LITE -> "Лайт";
            case LIFE -> "Пожизненный";
        };
    }

    @Step("Покупка «{target}» по существующему email {email} → модалка «Рады вновь вас видеть»")
    public void assertWelcomeBackForExistingEmail(String email, TtPage.SubscriptionType target) {
        step("Открытие главной и выбор тарифа «" + target.getDisplayName() + "»", () -> {
            open(AddressPage.URL_MAIN);
            ttPage.clickBtnOpenFor();
            ttPage.selectSubscription(target);
        });
        step("Ввод email клиента", () -> cloudPage.submitEmail(email));
        step("Ожидание модалки «Рады вновь вас видеть»", () -> welcomeBackModal.assertWelcomeBackModal());
    }

    @Step("Покупка Пожизненного по существующему email {email}: модалку не показываем, старые заказы status=2")
    public void purchaseLifetimeForExistingEmail(String email, String liteOrderId, String renewalOrderId) {
        step("Открытие главной и выбор «Пожизненный доступ»", () -> {
            open(AddressPage.URL_MAIN);
            ttPage.clickBtnOpenFor();
            ttPage.selectSubscription(TtPage.SubscriptionType.LIFE);
        });
        step("Ввод email клиента", () -> cloudPage.submitEmail(email));
        step("Модалка «Рады вновь вас видеть» не должна появиться",
                () -> welcomeBackModal.assertWelcomeBackModalNotShow());
        step("Оплата Пожизненного Cloud-картой", () -> {
            cloudPage.clickCardPageBtn();
            OrderSteps.validateSuccessfulPayment(cloudPage);
        });
        step("Проверка в админке: заказ с тарифом «Пожизненный»",
                () -> adminSteps.assertOrderTariffForEmail(email, "Пожизненный"));
        step("Проверка в админке: все старые заказы в status=2",
                () -> adminSteps.assertOrdersDeactivated(liteOrderId, renewalOrderId));
    }
}