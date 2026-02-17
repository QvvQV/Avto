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
    private final PaymentCycleExecutor paymentCycleExecutor;
    private final ComprehensiveCheckExecutor comprehensiveChechExecutor;

    public RecurrentPaymentTestExecutor(OrderDatesPage orderDatesPage,
                                        AdminOrderDetails adminOrderDetails,
                                        AdminOrdersPage adminOrdersPage,
                                        PaymentCycleExecutor paymentCycleExecutor) {
        this.orderDatesPage = orderDatesPage;
        this.adminOrderDetails = adminOrderDetails;
        this.paymentCycleExecutor = paymentCycleExecutor;
        this.comprehensiveChechExecutor = new ComprehensiveCheckExecutor(orderDatesPage);
    }

    /**
     * Выполнение теста с двумя рекуррентными платежами.
     */
    public void executeTwoPaymentTest(String testName, Supplier<SubscriptionTestFlow> flowSupplier,
                                      Consumer<SubscriptionTestFlow> processMethod,
                                      TestExecutionContext context, int cycles) {
        context.setTestName(testName);

        try {
            // Получаем экземпляр flow
            SubscriptionTestFlow flow = flowSupplier.get();

            // Выполняем экземпляр рекуррентной оплаты
            processMethod.accept(flow);

            // Обновляем страницу
            refreshPage();

            // Получаем ID продукта и настройки
            String productId = adminOrderDetails.getFirstProductId();
            context.setCurrentProductId(productId);
            ProductConfiguration.ProductSettings settings = ProductConfiguration.getSettings(productId);

            initializeTestContext(context);

            switch (cycles) {
                case 1:
                    paymentCycleExecutor.performPaymentCycle("первый",
                        settings.initialExpiresDays(),
                        settings.afterFirstPaymentDays(),
                        context);
                            break;
                    case 2:
                        // Выполняем первый платежный цикл
                paymentCycleExecutor.performPaymentCycle("первый",
                        settings.initialExpiresDays(),
                        settings.afterFirstPaymentDays(),
                        context);

                // Выполняем второй платежный цикл
                paymentCycleExecutor.performPaymentCycle("второй",
                        settings.afterFirstPaymentDays(),
                        settings.afterSecondPaymentDays(),
                        context);
                // Выполняем финальную проверку
                comprehensiveChechExecutor.performFinalComprehensiveCheck(productId);
                break;

                default:
                    throw new IllegalStateException(
                            String.format("Неподдерживаемое кол-во циклов: %d. оддерживаются значения: 1, 2", cycles)
                    );
            }

            // Логируем завершение теста
            flow.logTestCompletion();
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
        return switch (number) {
            case 1 -> "первый";
            case 2 -> "второй";
            default -> "платеж " + number;
        };
    }

    /**
     * Получение ожидаемых дней до платежа.
     */
    private int getExpectedDaysBefore(ProductConfiguration.ProductSettings settings, int paymentNumber) {
        return switch (paymentNumber) {
            case 1 -> settings.initialExpiresDays();
            case 2 -> settings.afterFirstPaymentDays();
            default -> settings.afterSecondPaymentDays();
        };
    }

    /**
     * Получение ожидаемых дней после платежа.
     */
    private int getExpectedDaysAfter(ProductConfiguration.ProductSettings settings, int paymentNumber) {
        return switch (paymentNumber) {
            case 1 -> settings.initialExpiresDays();
            case 2 -> settings.afterFirstPaymentDays();
            default -> settings.afterSecondPaymentDays();
        };
    }

    /**
     * Захват состояния при ошибке.
     */
    private void captureFailureState(TestExecutionContext context) {
        AllureAttachments.attachText("Состояние при ошибке",
                "Тест завершился с ошибкой\nКонтекст: " + context.toString());
    }
}