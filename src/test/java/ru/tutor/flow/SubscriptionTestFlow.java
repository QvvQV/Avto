package ru.tutor.flow;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.builder.SubscriptionPurchaseBuilder;
import ru.tutor.flow.builder.SubscriptionPurchaseResult;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.CloudPage;
import ru.tutor.page.SberPage;
import ru.tutor.page.admin.AdminLoginPage;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.TtPage;
import ru.tutor.page.modals.GreetingsModal;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.TabManager;
import ru.tutor.utils.WaitHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionTestFlow {

    // Поле для хранения флага
    private boolean currentOrderHasAffRid = false;

    private String userEmail;
    private final TtPage ttPage;
    private final CloudPage cloudPage;
    private final SberPage sberPage;
    private final AdminOrdersPage adminOrdersPage;

    private SubscriptionPurchaseResult purchase(Consumer<SubscriptionPurchaseBuilder> configurator) {
        SubscriptionPurchaseBuilder builder = SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage);
        configurator.accept(builder);

        this.currentOrderHasAffRid = builder.hasAffRid();
        SubscriptionPurchaseResult result = builder.execute();
        this.userEmail = result.userEmail();
        return result;
    }

    public SubscriptionTestFlow(TtPage ttPage, CloudPage cloudPage, SberPage sberPage, AdminOrdersPage adminOrdersPage) {
        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.sberPage = sberPage;
        this.adminOrdersPage = adminOrdersPage;
    }

    // Короткие методы-обертки для различных сценариев покупки
    @Step("Оплата Премиума по Клауду")
    public SubscriptionTestFlow purchasePremiumSubscription() {
        purchase(builder ->
                builder.forPremium()
                        .viaCloud()
                        .withStandardPayment()
        );
        return this;
    }

    @Step("Оплата по aff_rid ссылке Клауд")
    public SubscriptionTestFlow purchasePremiumSubscriptionAffRid() {
        purchase(builder ->
                builder.forPremium()
                        .viaCloud()
                        .withStandardPayment()
                        .withAffRid()
        );
        return this;
    }

    @Step("Оплата Пожизненного доступа по Клауду")
    public SubscriptionTestFlow purchaseLifeSubscription() {
        purchase(builder ->
                builder.forLife()
                        .viaCloud()
                        .withStandardPayment()
        );
        return this;
    }

    @Step("Оплата долями по Клауду")
    public SubscriptionTestFlow purchaseLifeInstallmentsSubscription() {
        purchase(builder ->
                builder.forLife()
                        .viaCloud()
                        .withInstallments()
        );
        return this;
    }

    @Step("Оплата Лайт по Клауду")
    public SubscriptionTestFlow purchaseLiteSubscription() {
        purchase(builder ->
                builder.forLite()
                        .viaCloud()
                        .withStandardPayment()
        );
        return this;
    }

    @Step("Оплата Премиум по карте Сбер")
    public SubscriptionTestFlow purchasePremiumSubscriptionSber() {
        purchase(builder ->
                builder.forPremium()
                        .viaSber()
                        .withApprovedCard()
        );
        return this;
    }

    @Step("Оплата Лайт по карте Сбер")
    public SubscriptionTestFlow purchaseLiteSubscriptionSber() {
        purchase(builder ->
                builder.forLite()
                        .viaSber()
                        .withApprovedCard()
        );
        return this;
    }

    @Step("Оплата Пожизненного по карте Сбер")
    public SubscriptionTestFlow purchaseLifeSubscriptionSber() {
        purchase(builder ->
                builder.forLife()
                        .viaSber()
                        .withApprovedCard()
        );
        return this;
    }

    @Step("Оплата Премиум по карте Сбер с фиксированным остатком = 100р")
    public SubscriptionTestFlow purchasePremiumSubscriptionFixSber() {
        purchase(builder ->
                builder.forPremium()
                        .viaSber()
                        .withFixedBalanceCard()
        );
        return this;
    }

    @Step("Ошибка 'Недостаточно средств' по карте Сбер")
    public SubscriptionTestFlow purchasePremiumSubscriptionInsufficientFundsSber() {
        purchase(builder ->
                builder.forPremium()
                        .viaSber()
                        .withInsufficientFundsCard()
                        .withExpectedError(TestConfig.Texts.ERROR_MESSAGE)
        );
        this.captureScreenshot();
        return this;
    }

    @Step("Ошибка 'Невалидная карта' по карте Сбер")
    public SubscriptionTestFlow purchasePremiumSubscriptionExpireSber() {
        purchase(builder ->
                builder.forPremium()
                        .viaSber()
                        .withExpiredCard()
                        .withExpectedError(TestConfig.Texts.ERROR_MESSAGE)
        );
        this.captureScreenshot();
        return this;
    }

    // Методы для работы с админкой

    @Step("Рекурент по тарифу Премиум")
    public SubscriptionTestFlow processRecurrentInAdmin() {
        step("Проведение рекуррентного платежа через админку", () -> {
            String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();

            try {
                TabManager.openAdminInNewWindow(originalWindow);
                loginToAdmin();
                adminOrdersPage
                        .withUtmCheck(currentOrderHasAffRid)
                        .navigateToOrders()
                        .findOrderByEmail(userEmail)
                        .processRecurrentPayment();
                WaitHelper.waitForAnimationComplete();
            } finally {
                // Можно добавить закрытие окна админки если нужно
            }
        });
        return this;
    }

    @Step("Рекуррент по тарифу Лайт")
    public void processRecurrentInAdminLite() {
        processRecurrentInAdmin();
        step("Проведение рекуррентного платежа Lite через админку", () -> {
            sleep(2000);
            adminOrdersPage.processRecurrentPayment();
            refresh();
            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();
//            AllureAttachments.attachScreenshot("Заказ найден в него перешли");
        });
    }

    @Step("Переход в Заказ по Тарифу Пожизненный доступ")
    public void processUnlimitedInAdmin() {
        step("Переход в заказ для пожизненного доступа через админку", () -> {
            String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();

            try {
                TabManager.openAdminInNewWindow(originalWindow);
                loginToAdmin();
                adminOrdersPage
                        .navigateToOrders()
                        .findOrderByEmail(userEmail)
                        .processRecurrentPayment();
                WaitHelper.waitForAnimationComplete();
                WaitHelper.waitForPageLoad();
            } finally {
                // Можно добавить закрытие окна админки если нужно
            }
        });
    }

    @Step("Авторизация в админке")
    private void loginToAdmin() {
        step("Авторизация в админке", () -> {
            AdminLoginPage.login();
            AllureAttachments.attachScreenshot("Авторизация в админке успешна");
        });
    }

    @Step("Поиск заказа для проведения рекурентного платежа")
    private void processRecurrentPayment() {
        step("Поиск заказа", () -> {
            adminOrdersPage
                    .navigateToOrders()
                    .findOrderByEmail(userEmail)
                    .processRecurrentPayment();
            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();
//            AllureAttachments.attachScreenshot("Заказ найден в него перешли");
        });
    }

    // ==================== Методы для перехода в ЛК ====================

    @Step("Переход в Личный кабинет пользователя")
    public SubscriptionTestFlow goToUserPersonalAccount() {
        // Передаём флаг в AdminOrdersPage
        adminOrdersPage
                .withUtmCheck(currentOrderHasAffRid) // Устанавливаем флаг
                .goToUserPersonalAccount(); // Вызываем метод перехода
        return this;
    }

    @Step("Переход в ЛИчный кабинет с принудительной проверкой UTM")
    public SubscriptionTestFlow goToUserPersonalAccountWithUtmCheck() {
        adminOrdersPage
                .enableUtmCheck()
                .goToUserPersonalAccount();
        return this;
    }

    @Step("Переход в Личный кабинет без проверки UTM")
    public SubscriptionTestFlow goToUserPersonalAccountWithoutUtmCheck() {
        adminOrdersPage
                .disableUtmCheck()
                .goToUserPersonalAccount();
        return this;
    }

    @Step("Регистрация нового пользователя по реферальной ссылке")
    public SubscriptionTestFlow registerNewUserByReferral(String referralLink, GreetingsModal greetingsModal) {
        step("Открытие страницы с реферальной ссылкой", () -> {
            String url = AddressPage.urlMain + "?club_ref=" +  referralLink;
            open(url);

            Allure.addAttachment("Реферальная ссылка", url);
        });

        step("Регистрация нового пользователя", () -> {
            ttPage.clickBtnOpenFor();


            Allure.addAttachment("Регистрация", "✅ Новый пользователь зарегистрирован по реферальной ссылке");
        });
        return this;
    }

    public void logTestCompletion() {
        step("Завершение теста", () -> {
            String userEmail = adminOrdersPage.getLastSearchedEmail();
            AllureAttachments.attachText("Тест завершен",
                    "✅ Все проверки рекуррентных платежей выполнены успешно\n" +
                            "📧 Email: " + userEmail + "\n" +
                            "⏱️ Время выполнения: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        });
    }

    public SubscriptionTestFlow verifyErrorMessage(String expectedError) {
        step("Проверка сообщения об ошибке", () -> {
            String actualError = sberPage.getErrorText();
            assertEquals(expectedError, actualError);
            AllureAttachments.attachText("Проверка ошибки",
                    "Ожидалось: " + expectedError + "\nФактически: " + actualError);
        });
        return this;
    }

    public void captureScreenshot() {
        AllureAttachments.attachScreenshot("Скриншот после теста");
    }
}