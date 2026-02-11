package ru.tutor.test.executor;

import ru.tutor.ProductConfiguration;
import ru.tutor.flow.SubscriptionTestFlow;
import ru.tutor.page.OrderDatesPage;
import ru.tutor.page.admin.AdminOrderDetails;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.test.context.TestExecutionContext;
import ru.tutor.utils.AllureAttachments;

import java.util.function.Supplier;
import java.util.function.Consumer;

import static com.codeborne.selenide.Selenide.refresh;
import static ru.tutor.utils.WaitHelper.waitForAnimationComplete;
import static ru.tutor.utils.WaitHelper.waitForPageLoad;

public class RecurrentPaymentTestExecutor {

    private final OrderDatesPage orderDatesPage;
    private final AdminOrderDetails adminOrderDetails;
    private final AdminOrdersPage adminOrdersPage;
    private final PaymentCycleExecutor paymentCycleExecutor;
    private final ComprehensiveCheckExecutor comprehensiveChechExecutor;

    public RecurrentPaymentTestExecutor(OrderDatesPage orderDatesPage,
                                        AdminOrderDetails adminOrderDetails,
                                        AdminOrdersPage adminOrdersPage,
                                        PaymentCycleExecutor paymentCycleExecutor) {
        this.orderDatesPage = orderDatesPage;
        this.adminOrderDetails = adminOrderDetails;
        this.adminOrdersPage = adminOrdersPage;
        this.paymentCycleExecutor = paymentCycleExecutor;
        this.comprehensiveChechExecutor = new ComprehensiveCheckExecutor(orderDatesPage, adminOrderDetails);
    }

    /**
     * Выполнение теста с двумя рекуррентными платежами.
     */
    public void executeTwoPaymentTest(String testName, Supplier<SubscriptionTestFlow> flowSupplier,
                                      Consumer<SubscriptionTestFlow> processMethod,
                                      TestExecutionContext context) {
        context.setTestName(testName);

        try {
            // Получаем экземпляр flow
            SubscriptionTestFlow flow = flowSupplier.get();

            // Выполняем экземпляр рекуррентной оплаты
            processMethod.accept(flow);

            // Обновляем страницу
            refreshPage();

            // Получаем ID родукта и настройки
            String productId = adminOrderDetails.getFirstProductId();
            context.setCurrentProductId(productId);
            ProductConfiguration.ProductSettings settings = ProductConfiguration.getSettings(productId);

            initializeTestContext(context);

            // Выполняем первый платежный цикл
            paymentCycleExecutor.performPaymentCycle("первый",
                    settings.getInitialExpiresDays(),
                    settings.getAfterFirstPaymentDays(),
                    context);

            // Выполняем второй платежный цикл
            paymentCycleExecutor.performPaymentCycle("второй",
                    settings.getAfterFirstPaymentDays(),
                    settings.getAfterSecondPaymentDays(),
                    context);

            // Выполняем финальную проверку
            comprehensiveChechExecutor.performFinalComprehensiveCheck(productId);

            // Логируем завершение теста
            flow.logTestCompletion();
        } catch (Exception e) {
            captureFailureState(context);
            throw e;
        }
    }

    /**
     * Выполнение теста по Африд/aff_rid ссылке с рекурентом
     */
    public void executePaymentTestAffRid(String testName, Supplier<SubscriptionTestFlow> flowSupplier,
                                      Consumer<SubscriptionTestFlow> processMethod,
                                      TestExecutionContext context) {
        context.setTestName(testName);

        try {
            // Получаем экземпляр flow
            SubscriptionTestFlow flow = flowSupplier.get();

            // Выполняем экземпляр рекуррентной оплаты
            processMethod.accept(flow);

            // Обновляем страницу
            refreshPage();

            // Получаем ID родукта и настройки
            String productId = adminOrderDetails.getFirstProductId();
            context.setCurrentProductId(productId);
            ProductConfiguration.ProductSettings settings = ProductConfiguration.getSettings(productId);

            initializeTestContext(context);

            // Выполняем первый платежный цикл
            paymentCycleExecutor.performPaymentCycle("первый",
                    settings.getInitialExpiresDays(),
                    settings.getAfterFirstPaymentDays(),
                    context);

            // Логируем завершение теста
//            flow.logTestCompletion();
        } catch (Exception e) {
            captureFailureState(context);
            throw e;
        }
    }

    /**
     * Обновление страницы с ожиданиями.
     */
    private void refreshPage() {
        waitForPageLoad();
        waitForAnimationComplete();
        refresh();
        waitForPageLoad();
        waitForAnimationComplete();
    }

    /**
     * Инициализация контекста теста.
     */
    private void initializeTestContext(TestExecutionContext context) {
        context.setOriginalCreatedDate(orderDatesPage.extractCreationDate());
    }

    /**
     * Получение имени платежа по номеру.
     */
    private String getPaymentNameByNumber(int number) {
        switch (number) {
            case 1: return "первый";
            case 2: return "второй";
            default: return "платеж " + number;
        }
    }

    /**
     * Получение ожидаемых дней до платежа.
     */
    private int getExpectedDaysBefore(ProductConfiguration.ProductSettings settings, int paymentNumber) {
        switch (paymentNumber) {
            case 1: return settings.getInitialExpiresDays();
            case 2: return settings.getAfterFirstPaymentDays();
            default: return settings.getAfterSecondPaymentDays();
        }
    }

    /**
     * Получение ожидаемых дней после платежа.
     */
    private int getExpectedDaysAfter(ProductConfiguration.ProductSettings settings, int paymentNumber) {
        switch (paymentNumber) {
            case 1: return settings.getInitialExpiresDays();
            case 2: return settings.getAfterFirstPaymentDays();
            default: return settings.getAfterSecondPaymentDays();
        }
    }

    /**
     * Захват состояния при ошибке.
     */
    private void captureFailureState(TestExecutionContext context) {
        AllureAttachments.attachText("Состояние при ошибке",
                "Тест завершился с ошибкой\nКонтекст: " + context.toString());
    }
}