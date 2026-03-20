package ru.tutor.flow;

import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.OrderDatesPage;
import ru.tutor.page.RedirectPage;
import ru.tutor.page.admin.AdminOrderDetails;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.modals.GreetingsModal;
import ru.tutor.page.modals.product;
import ru.tutor.page.modals.unlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.page.unsubscribe.LiteUnsubscribePage;
import ru.tutor.steps.AdminToUserLoginSteps;
import ru.tutor.steps.OrderToggleSubscriptionSteps;
import ru.tutor.steps.ReactivatePremiumSteps;
import ru.tutor.steps.UrlModificationSteps;
import ru.tutor.test.context.TestExecutionContext;
import ru.tutor.test.executor.PaymentCycleExecutor;
import ru.tutor.test.executor.RecurrentPaymentTestExecutor;
import ru.tutor.utils.WaitHelper;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

public class ReactivatePremiumFlow {

    private final TtPage ttPage;
    private final SberPage sberPage;
    private final CloudPage cloudPage;
    private final AdminOrdersPage adminOrdersPage;
    private final AdminOrderDetails adminOrderDetails;
    private final OrderDatesPage orderDatesPage;
    private final PaymentCycleExecutor paymentCycleExecutor;
    private final TestExecutionContext contex;
    private final Integer TEST_ORDER_ID = 180;

    private RecurrentPaymentTestExecutor testExecutor;
    private final OrderToggleSubscriptionSteps orderSteps;
    private final ReactivatePremiumSteps reactivatePremiumSteps;
    private final AdminToUserLoginSteps adminToUserLoginSteps;

    private String authToken;
    private String userEmail;

    public ReactivatePremiumFlow(
            TtPage ttPage, CloudPage cloudPage, SberPage sberPage, AdminOrdersPage adminOrdersPage,
            AdminOrderDetails adminOrderDetails, OrderDatesPage orderDatesPage,
            PaymentCycleExecutor paymentCycleExecutor, TestExecutionContext contex) {

        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.sberPage = sberPage;
        this.adminOrdersPage = adminOrdersPage;
        this.adminOrderDetails = adminOrderDetails;
        this.orderDatesPage = orderDatesPage;
        this.paymentCycleExecutor = paymentCycleExecutor;
        this.contex = contex;

        String baseUrl = TestConfig.getCurrentApiUrl();
        this.orderSteps = OrderToggleSubscriptionSteps.withBaseUrl(baseUrl);
        this.reactivatePremiumSteps = ReactivatePremiumSteps.withBaseUrl(baseUrl);
        this.adminToUserLoginSteps = new AdminToUserLoginSteps();

        initTestExecutor();
    }

    private void initTestExecutor() {
        this.testExecutor = new RecurrentPaymentTestExecutor(
                orderDatesPage,
                adminOrderDetails,
                adminOrdersPage,
                paymentCycleExecutor
        );
    }


    /**
     * Базовый шаг: создание пользователя с aff_rid и рекуррент
     */
    @Step("Создание пользователя с aff_rid и проведение рекуррента")
    public ReactivatePremiumFlow createUserWithAffRidAndRecurrent() {
        testExecutor.executePaymentTest(
                "Cloud Premium",
                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage)
                        .purchasePremiumSubscriptionAffRid(),
                SubscriptionTestFlow::processRecurrentInAdmin,
                contex, 1
        );

        // Получаем email после создания
        this.userEmail = adminOrdersPage.getLastSearchedEmail();
        return this;
    }

    /**
     * Базовый шаг: создание пользователя без aff_rid и рекуррент
     */
    @Step("Создание пользователя без aff_rid и проведение рекуррента")
    public ReactivatePremiumFlow createUserAndRecurrent() {
        testExecutor.executePaymentTest(
                "Cloud Premium",
                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage)
                        .purchasePremiumSubscription(),
                SubscriptionTestFlow::processRecurrentInAdmin,
                contex, 1
        );

        userEmail = adminOrdersPage.getLastSearchedEmail();
        return this;
    }

    /**
     * Базовый шаг: переход в ЛК пользователя по кнопке из админки
     */
    @Step("Переход в личный кабинет пользователя")
    public ReactivatePremiumFlow goToUserPersonalAccount() {
        step("Переход в ЛК", () -> {
            WaitHelper.waitForAnimationComplete();
            adminOrdersPage.goToUserPersonalAccount();
        });
        return this;
    }

    /**
     * Добавление aff_rid параметра в URL
     */
    @Step("Добавление aff_rid параметра")
    public ReactivatePremiumFlow addAffiliateParameter() {
        step("Добавление aff_rid", UrlModificationSteps::addAffiliateParameter);
        return this;
    }

    /**
     * Добавление aff_rid параметра из email-рассылки
     */
    @Step("Добавление aff_rid параметра из email-рассылки")
    public ReactivatePremiumFlow addAffiliateParameterFromEmail() {
        step("Добавление aff_rid из email", UrlModificationSteps::addAffiliateParameterByEmail);
        return this;
    }

    /**
     * Получение токена авторизации
     */
    @Step("Получение токена авторизации")
    public ReactivatePremiumFlow fetchAuthToken() {
        this.authToken = AdminToUserLoginSteps.switchToTabAndGetToken();
        return this;
    }

    /**
     * Отписка до тарифа Лайт
     */
    @Step("Отписка до тарифа Лайт")
    public ReactivatePremiumFlow downgradeToLite() {
        step("Отписка до Лайта", () -> {
            if (authToken == null) {
                fetchAuthToken();
            }
            step("POST /user/orders/toggle_subscription/" + TEST_ORDER_ID, () ->
                    orderSteps.toggleOrderSubscription(authToken, TEST_ORDER_ID));
        });
        return this;
    }

    /**
     * Реактивация подписки
     */
    @Step("Реактивация подписки")
    public ReactivatePremiumFlow reactivatePremium() {
        step("Реактивация подписки", () -> {
            if (authToken == null) {
                fetchAuthToken();
            }
            reactivatePremiumSteps.reactivatePremium(authToken);
        });
        return this;
    }

    /**
     * Реактивация подписки с aff_rid
     */
    @Step("Реактивация подписки с aff_rid: {affRid}")
    public ReactivatePremiumFlow reactivatePremiumWithAffRid(String affRid) {
        step("Реактивация с aff_rid: " + affRid, () -> {
            if (authToken == null) {
                fetchAuthToken();
            }
            reactivatePremiumSteps.reactivatePremium(authToken, affRid);
        });
        return this;
    }

    /**
     * Реактивация подписки через email-рассылку
     */
    @Step("Реактивация подписки через email-рассылку")
    public ReactivatePremiumFlow reactivatePremiumByEmail() {
        step("Реактивация через email", () -> {
            if (authToken == null) {
                fetchAuthToken();
            }
            reactivatePremiumSteps.reactivatePremiumByEmail(authToken);
        });
        return this;
    }

    /**
     * Проверка aff_rid метки
     */
    @Step("Проверка aff_rid метки заказа")
    public ReactivatePremiumFlow verifyAffRid() {
        step("Проверка aff_rid", adminOrdersPage::renewal);
        return this;
    }

    /**
     * Отмена заказа через админку
     */
    @Step("Отмена заказа через админку")
    public ReactivatePremiumFlow cancelOrderInAdmin() {
        step("Отмена заказа", adminOrdersPage::cancelOrderWithAdmin);
        return this;
    }

    /**
     * Оплата подписки из email-рассылки
     */
    @Step("Оплата подписки из email-рассылки")
    public ReactivatePremiumFlow payFromEmailNewsletter() {
        step("Оплата из email", () -> {
            adminToUserLoginSteps.sendEmailPay();
            CloudPage.clickCardPageBtn();
        });
        return this;
    }

    /**
     * Обработка редиректа и заполнение данных
     */
    @Step("Обработка послеоплатных шагов")
    public ReactivatePremiumFlow handlePostPaymentSteps() {
        step("Редирект после оплаты", RedirectPage::redirectPage);
        step("Заполнение модалки", GreetingsModal::fillAccessInput );
        product.beginProduct();
        unlockNextWeek.openUnlimitedAccess();
        return this;
    }

    /**
     * Оформление Renewall внутри ЛК
     */
    @Step("Оформление Renewall внутри ЛК")
    public ReactivatePremiumFlow processRenewalInPersonalAccount() {
        step("Переход к отпискам", () -> {
            open(AddressPage.PAYMENTS_SETTINGS);
            WaitHelper.waitForPageLoad();
            step("Вызов модального окна оплаты", LiteUnsubscribePage::clickSubscribe);
        });
        CloudPage.clickCardPageBtn();
        return this;
    }

    /**
     * Получение aff_rid из конфига
     */
    public String getAffRid() {
        return AddressPage.value;
    }
}
