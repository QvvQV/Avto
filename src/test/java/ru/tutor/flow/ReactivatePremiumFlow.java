package ru.tutor.flow;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.OrderDatesPage;
import ru.tutor.page.RedirectPage;
import ru.tutor.page.admin.AdminOrderDetails;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.modals.GreetingsModal;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.page.unsubscribe.LiteUnsubscribePage;
import ru.tutor.steps.*;
import ru.tutor.test.context.TestExecutionContext;
import ru.tutor.test.executor.PaymentCycleExecutor;
import ru.tutor.test.executor.RecurrentPaymentTestExecutor;
import ru.tutor.utils.WaitHelper;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

public class ReactivatePremiumFlow {

    private final CloudPage cloudPage;
    private final AdminOrdersPage adminOrdersPage;
    private final AdminOrderDetails adminOrderDetails;
    private final OrderDatesPage orderDatesPage;
    private final PaymentCycleExecutor paymentCycleExecutor;
    private final TestExecutionContext context;
    private final Integer TEST_ORDER_ID = 180;

    private RecurrentPaymentTestExecutor testExecutor;
    private final OrderToggleSubscriptionSteps orderSteps;
    private final ReactivatePremiumSteps reactivatePremiumSteps;
    private final AdminToUserLoginSteps adminToUserLoginSteps;
    private final OrderPremiumGiftSteps orderStepsPremiumGift;
    private final Product product = new Product();

    private final PurchaseFlow purchaseFlow;
    private final AdminFlow adminFlow;

    private String authToken;

    // Константы UTM меток
    private static final String DEFAULT_UTM = TestConfig.getDefUtm();
    private static final String SYSTEM_UTM = TestConfig.getSystemUtm();

    private boolean isRenewalInPersonalAccount = false;
    private String userEmail;

    public ReactivatePremiumFlow(
            TtPage ttPage, CloudPage cloudPage, SberPage sberPage, AdminOrdersPage adminOrdersPage,
            AdminOrderDetails adminOrderDetails, OrderDatesPage orderDatesPage,
            PaymentCycleExecutor paymentCycleExecutor, TestExecutionContext context) {
        
        this.cloudPage = cloudPage;
        this.adminOrdersPage = adminOrdersPage;
        this.adminOrderDetails = adminOrderDetails;
        this.orderDatesPage = orderDatesPage;
        this.paymentCycleExecutor = paymentCycleExecutor;
        this.context = context;

        this.purchaseFlow = new PurchaseFlow(ttPage, cloudPage, sberPage);
        this.adminFlow = new AdminFlow(adminOrdersPage);

        String baseUrl = TestConfig.getCurrentApiUrl();
        this.orderSteps = new OrderToggleSubscriptionSteps(baseUrl);
        this.orderStepsPremiumGift = new OrderPremiumGiftSteps(baseUrl);
        this.reactivatePremiumSteps = new ReactivatePremiumSteps(baseUrl);
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
    public ReactivatePremiumFlow createUserWithAffRidAndRecurrent(int cycles) {
        PurchaseResult result = purchaseFlow.premiumAffRid();
        this.userEmail = result.email();

        adminFlow.withUtmCheck(true).processRecurrent(userEmail);
        testExecutor.executePaymentTest("Cloud Premium", userEmail, context, cycles);
        return this;
    }

    /**
     * Базовый шаг: создание пользователя без aff_rid и рекуррент
     */
    @Step("Создание пользователя без aff_rid и проведение реккурента")
    public ReactivatePremiumFlow createUserAndRecurrent(int cycles) {
        PurchaseResult result = purchaseFlow.premium();
        this.userEmail = result.email();

        adminFlow.processRecurrent(userEmail);
        testExecutor.executePaymentTest("Cloud Premium", userEmail, context, cycles);
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
     * Добавление параметра email-рассылки
     */
    @Step("Добавление параметра из email-рассылки, без aff_rid")
    public ReactivatePremiumFlow removeAffiliateParameterFromEmail() {
        step("Добавление параметра из email-рассылки без aff_rid", UrlModificationSteps::removeAffiliateParameterByEmail);
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
     * Отписка до тарифа Подарок
     */
    @Step("Отписка до тарифа Подарок")
    public ReactivatePremiumFlow downgradeToPremiumGift() {
        step("Отписка до Подарка", () -> {
            if (authToken == null) {
                fetchAuthToken();
            }
            Response response = step("POST /user/orders/get-premium-gift", () ->
                    orderStepsPremiumGift.getPremiumGift(authToken));
            step("Валидация ответа", () ->
                    orderStepsPremiumGift.validateGetPremiumGiftResponse(response));
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
        if (isRenewalInPersonalAccount) {
            step("Проверка aff_rid (системная метка после Renewal в ЛК)", () -> {
                adminOrdersPage.renewal(SYSTEM_UTM);
            });

            // Сбрасываем флаг после проверки
            isRenewalInPersonalAccount = false;
        } else {
            step("Проверка aff_rid (вебовская метка)", () ->
                    adminOrdersPage.renewal(DEFAULT_UTM));
        }
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
            cloudPage.clickCardPageBtn();
        });
        return this;
    }

    /**
     * Обработка редиректа и заполнение данных
     */
    @Step("Обработка редиректа и заполнение данных")
    public static void handlePostPaymentSteps() {
        step("Редирект после оплаты", () -> new RedirectPage().redirectPage());
        step("Заполнение модалки", new GreetingsModal()::fillAccessInput);
    }

    /**
     * Открытие первой недели курса
     */
    @Step("Обработка послеоплатных шагов")
    public void openFirstWeek() {
        handlePostPaymentSteps();
        product.beginProduct();
        new UnlockNextWeek().openUnlimitedAccess();
    }

    /**
     * Оформление Renewall внутри ЛК
     */
    @Step("Оформление Renewall внутри ЛК")
    public ReactivatePremiumFlow processRenewalInPersonalAccount() {
        this.isRenewalInPersonalAccount = true;

        step("Переход к отпискам", () -> {
            open(AddressPage.PAYMENTS_SETTINGS);
            WaitHelper.waitForPageLoad();
            step("Вызов модального окна оплаты", new LiteUnsubscribePage()::clickSubscribe);
        });
        cloudPage.clickCardPageBtn();
        return this;
    }

    @Step("Получить кешбек и заполнить приветсвенное окно")
    public ReactivatePremiumFlow takeCashbackAndFillWelcomeWindow() {
        product.pickUpBonus();
        new GreetingsModal().fillOutTwoFieldsWelcomeModal();
        return this;
    }

    /**
     * Получение aff_rid из конфига
     */
    public String getAffRid() {
        return AddressPage.VALUE;
    }
}
