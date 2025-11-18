package ru.tutor.test.orders;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tutor.ProductConfiguration;
import ru.tutor.base.BaseUiTest;
import ru.tutor.flow.SubscriptionTestFlow;
import ru.tutor.page.CloudPage;
import ru.tutor.page.OrderDatesPage;
import ru.tutor.page.SberPage;
import ru.tutor.page.admin.AdminOrderDetails;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.dto.OrderDates;
import ru.tutor.page.ttPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.DateValidator;
import ru.tutor.utils.WaitHelper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.codeborne.selenide.Selenide.refresh;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;


public class unsubscribeInAdminCheckDate extends BaseUiTest {

    @BeforeAll
    public static void setup() {
        setupBrowserWithoutIncognito();
    }

    // Константы
    private static final int ALLOWED_TIME_DIFFERENCE_MINUTES = 1;


    // Page Objects
    private final AdminOrderDetails adminOrderDetails = new AdminOrderDetails();
    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final ttPage ttPage = new ttPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();

    // Вспомогательные классы
    private final OrderDatesPage orderDatesPage = new OrderDatesPage(adminOrderDetails);
    private final DateValidator dateValidator = new DateValidator(ALLOWED_TIME_DIFFERENCE_MINUTES);

    private SubscriptionTestFlow flow = new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);

    @Test
    @DisplayName("Комплексная проверка дат заказа после серии рекуррентных платежей Cloud")
    void testComplexOrderDateAfterMultiplePayments() {

        executeRecurrentPaymentTest(() -> flow.purchasePremiumSubscription());

    }

    @Test
    @DisplayName("Комплексная проверка дат заказа Lite после серии рекуррентных платежей Cloud")
    void testComplexOrderDateAfterMultiplePaymentsLiteCloud() {

        executeRecurrentPaymentLiteTest(() -> flow.purchaseLiteSubscription());

    }

    @Test
    @DisplayName("Комплексная проверка дат заказа после серии рекуррентных платежей Sber")
    void testComplexOrderDateAfterMultiplePaymentsSber() {

        executeRecurrentPaymentTest(() -> flow.purchasePremiumSubscriptionSber());

    }

    private class TestExecutionContext {
        private LocalDateTime originalCreatedDateTime;
        private LocalDateTime expiresBeforePayment;
        private final Map<String, String> stateSnapshots = new HashMap<>();

        public void setOriginalCreatedDate(LocalDateTime dateTime) {
            this.originalCreatedDateTime = dateTime;
        }

        public void setExpiresBeforePayment(LocalDateTime dateTime) {
            this.expiresBeforePayment = dateTime;
        }

        public void saveState(String key, String value) {
            stateSnapshots.put(key, value);
        }
    }

    private void performPaymentCycle(String paymentName, int expectedDaysBefore,
                                     int expectedDaysAfter, TestExecutionContext context) {
        Allure.step(paymentName.toUpperCase() + " ЦИКЛ ОПЛАТЫ", () -> {
            performPrePaymentChecks(paymentName, expectedDaysBefore, context);
            context.setExpiresBeforePayment(orderDatesPage.extractExpirationDate());
            context.saveState("expires_before_" + paymentName, adminOrderDetails.getExpirationDate());

            // Получаем ID продукта и выполняем оплату
            String productId = adminOrderDetails.getFirstProductId();
            executePayment(paymentName, productId);

            orderDatesPage.refreshOrderData();
            performPostPaymentChecks(paymentName, expectedDaysAfter, context);
            verifyNextPaymentEqualsExpires(paymentName);
        });
    }

    private void performPrePaymentChecks(String paymentName, int expectedDays, TestExecutionContext context) {
        if ("первый".equals(paymentName)) {
            verifyInitialOrderState(expectedDays);
        } else {
            verifyCreationDateUnchanged(context.originalCreatedDateTime);
        }
    }

    private void verifyInitialOrderState(int expectedDays) {
        step("Проверка начального состояния заказа", () -> {
            OrderDates dates = orderDatesPage.extractAllOrderDates();
            dateValidator.verifyDateDifference(dates.getCreated(), dates.getExpires(), expectedDays, "начальное состояние Истекает");
            dateValidator.verifyTimeOfDayDifference(dates.getCreated(), dates.getExpires(), "начальное состояние времени Истекает");
            orderDatesPage.logOrderState("Начальное состояние заказа", dates);
        });
    }

    private void verifyCreationDateUnchanged(LocalDateTime originalCreatedDateTime) {
        step("Проверка неизменности даты создания", () -> {
            LocalDateTime currentCreatedDateTime = orderDatesPage.extractCreationDate();
            assertEquals(originalCreatedDateTime, currentCreatedDateTime,
                    "Дата и время 'Создан' не должны изменяться после оплат");
            AllureAttachments.attachText("Проверка неизменности даты создания",
                    String.format("Оригинал: %s\nТекущее: %s", originalCreatedDateTime, currentCreatedDateTime));
        });
    }

    private void executePayment(String paymentName, String productId) {
        step("Выполнение " + paymentName + " оплаты", () -> {
            adminOrdersPage.makePayment(productId);
            AllureAttachments.attachText("Оплата", paymentName + " оплата выполнена успешно " + productId);
        });
    }

    private void performPostPaymentChecks(String paymentName, int expectedDays, TestExecutionContext context) {
        step("Проверка после " + paymentName + " оплаты", () -> {
            // Получаем ID продукта для определения настроек
            String productId = adminOrderDetails.getFirstProductId();
            ProductConfiguration.ProductSettings settings = ProductConfiguration.getSettings(productId);

            OrderDates dates = orderDatesPage.extractAllOrderDates();
            dateValidator.verifyDateDifference(dates.getCreated(), dates.getExpires(), expectedDays, "истекает после " + paymentName + " оплаты");
            dateValidator.verifyTimeOfDayDifference(dates.getCreated(), dates.getExpires(), "Истекает после " + paymentName + " оплаты");

            if ("второй".equals(paymentName)) {
                // Используем разницу между настройками для второго платежа
                int betweenPaymentsDays = settings.getAfterSecondPaymentDays() - settings.getAfterFirstPaymentDays();
                dateValidator.verifyDateDifference(context.expiresBeforePayment, dates.getExpires(), betweenPaymentsDays, "Увеличение Истекает после второй оплаты");
            }
            orderDatesPage.logOrderState("Состояние после " + paymentName + " оплаты", dates);
        });
    }

    private void verifyNextPaymentEqualsExpires(String paymentName) {
        step("Проверка соответствия 'След. платеж' и 'Истекает' после " + paymentName + " оплаты", () -> {
            OrderDates dates = orderDatesPage.extractAllOrderDates();
            assertEquals(dates.getExpires().toLocalDate(), dates.getNextPayment().toLocalDate(),
                    "После " + paymentName + " оплаты дата 'След. платеж' должна быть равна дате 'Истекает'");
            dateValidator.verifyTimeOfDayDifference(dates.getExpires(), dates.getNextPayment(), "След. платеж от Истекает после " + paymentName + " оплаты");
        });
    }

    private void performFinalComprehensiveCheck() {
        Allure.step("ФИНАЛЬНАЯ КОМПЛЕКСНАЯ ПРОВЕРКА", () -> {
            // Получаем ID продукта для определения финальных настроек
            String productId = adminOrderDetails.getFirstProductId();
            ProductConfiguration.ProductSettings settings = ProductConfiguration.getSettings(productId);
            int finalExpectedDays = settings.getAfterSecondPaymentDays();

            OrderDates dates = orderDatesPage.extractAllOrderDates();
            assertAll("Финальная проверка всех условий",
                    () -> assertNotNull(dates.getCreated(), "Дата создания не должна быть null"),
                    () -> assertNotNull(dates.getExpires(), "Дата истечения не должна быть null"),
                    () -> assertNotNull(dates.getNextPayment(), "Дата следующего платежа не должна быть null"),
                    () -> dateValidator.verifyDateDifference(dates.getCreated(), dates.getExpires(), finalExpectedDays,
                            "финальная проверка Истекает"),
                    () -> dateValidator.verifyTimeOfDayDifference(dates.getCreated(), dates.getExpires(),
                            "финальная проверка времени Истекает"),
                    () -> assertEquals(dates.getExpires().toLocalDate(), dates.getNextPayment().toLocalDate(),
                            "Финальная проверка: даты Истекает и След. платеж должны совпадать"),
                    () -> dateValidator.verifyTimeOfDayDifference(dates.getExpires(), dates.getNextPayment(),
                            "финальная проверка времени След. платеж"));
            orderDatesPage.logOrderState("Финальное состояние заказа", dates);

            AllureAttachments.attachText("Финальная проверка", "Проверка завершена для продукта " +
                    productId + " с ожиданием " + finalExpectedDays + " дней");
        });
    }

    private void executeRecurrentPaymentTest(Supplier<SubscriptionTestFlow> purchaseMethod) {
        TestExecutionContext context = new TestExecutionContext();
        SubscriptionTestFlow flow = new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);

//        try {
//            purchaseMethod.get().processRecurrentInAdmin();
//
//            refresh();
//            WaitHelper.waitForPageLoad();
//            WaitHelper.waitForAnimationComplete();
//
//            String productId = adminOrderDetails.getFirstProductId();
//            String productName = adminOrderDetails.getFirstProductName();
//
//                    AllureAttachments.attachText("Найденный продукт", productId + " - " + productName);
//        AllureAttachments.attachText("Тип заказа", productName.toLowerCase().contains("лайт") ||
//                productName.toLowerCase().contains("lite") ? "Lite" : "Premium");
//
//
//            ProductConfiguration.ProductSettings settings = ProductConfiguration.getSettings(productId);
//            context.setOriginalCreatedDate(orderDatesPage.extractCreationDate());
//
//            performPaymentCycle("первый", settings.getInitialExpiresDays(), settings.getAfterFirstPaymentDays(), context);
//            performPaymentCycle("второй", settings.getAfterFirstPaymentDays(), settings.getAfterSecondPaymentDays(), context);
//
//            performFinalComprehensiveCheck();
//            flow.logTestCompletion();
//        } catch (Exception e) {
////        captureFailureState(context);
////        throw e;
//        }

        try {
            // Выполняем покупку с переданным методом
            purchaseMethod.get().processRecurrentInAdmin();

            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            refresh();
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();

            // Получаем ID продукта один раз в начале
            String productId = adminOrderDetails.getFirstProductId();

            // Получаем настройки для конкретного продукта
            ProductConfiguration.ProductSettings settings = ProductConfiguration.getSettings(productId);

            context.setOriginalCreatedDate(orderDatesPage.extractCreationDate());

            // Выполняем платежные циклы - productId будет получен внутри методов
            performPaymentCycle("первый",
                    settings.getInitialExpiresDays(),
                    settings.getAfterFirstPaymentDays(),
                    context);
            performPaymentCycle("второй",
                    settings.getAfterFirstPaymentDays(),
                    settings.getAfterSecondPaymentDays(),
                    context);

            performFinalComprehensiveCheck();
            flow.logTestCompletion();
        } catch (Exception e) {
//            captureFailureState(context);
//            throw e;
        }
    }

    private void executeRecurrentPaymentLiteTest(Supplier<SubscriptionTestFlow> purchaseMethod) {
        TestExecutionContext context = new TestExecutionContext();
        SubscriptionTestFlow flow = new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);

        try {
            // Выполняем покупку с переданным методом
            purchaseMethod.get().processRecurrentInAdminLite();

            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            refresh();
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();

            // Получаем ID продукта один раз в начале
            String productId = adminOrderDetails.getFirstProductId();

            // Получаем настройки для конкретного продукта
            ProductConfiguration.ProductSettings settings = ProductConfiguration.getSettings(productId);

            context.setOriginalCreatedDate(orderDatesPage.extractCreationDate());

            // Выполняем платежные циклы - productId будет получен внутри методов
            performPaymentCycle("первый",
                    settings.getInitialExpiresDays(),
                    settings.getAfterFirstPaymentDays(),
                    context);
            performPaymentCycle("второй",
                    settings.getAfterFirstPaymentDays(),
                    settings.getAfterSecondPaymentDays(),
                    context);

            performFinalComprehensiveCheck();
            flow.logTestCompletion();
        } catch (Exception e) {
//            captureFailureState(context);
//            throw e;
        }
    }

    private void captureFailureState(TestExecutionContext context) {
        AllureAttachments.attachText("Состояние при ошибке", "тест завершился с ошибкой\n" + "Снимки состояния: " + context.stateSnapshots.toString());
    }
}