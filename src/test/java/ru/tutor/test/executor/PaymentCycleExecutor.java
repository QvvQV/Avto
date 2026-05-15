package ru.tutor.test.executor;

import io.qameta.allure.Allure;
import ru.tutor.constants.ProductConfiguration;
import ru.tutor.page.OrderDatesPage;
import ru.tutor.page.admin.AdminOrderDetails;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.dto.OrderDates;
import ru.tutor.test.context.TestExecutionContext;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.DateValidator;

import java.time.LocalDateTime;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс для проведения оплаты по заказу Рекуррента
 */
public class PaymentCycleExecutor {

    private final OrderDatesPage orderDatesPage;
    private final DateValidator dateValidator;
    private final AdminOrdersPage adminOrdersPage;
    private final AdminOrderDetails  adminOrderDetails;

    public PaymentCycleExecutor(OrderDatesPage orderDatesPage, DateValidator dateValidator,
                                AdminOrdersPage  adminOrdersPage, AdminOrderDetails adminOrderDetails) {
        this.orderDatesPage = orderDatesPage;
        this.dateValidator = dateValidator;
        this.adminOrdersPage = adminOrdersPage;
        this.adminOrderDetails = adminOrderDetails;
    }

    public void performPaymentCycle(String paymentName, int expectedDaysBefore,
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
            verifyCreationDateUnchanged(context.getOriginalCreatedDate());
        }
    }

    private void verifyInitialOrderState(int expectedDays) {
        step("Проверка начального состояния заказа", () -> {
            OrderDates dates = orderDatesPage.extractAllOrderDates();
            dateValidator.verifyDateDifference(dates.created(), dates.expires(), expectedDays, "начальное состояние Истекает");
            dateValidator.verifyTimeOfDayDifference(dates.created(), dates.expires(), "начальное состояние времени Истекает");
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
            dateValidator.verifyDateDifference(dates.created(), dates.expires(), expectedDays, "истекает после " + paymentName + " оплаты");
            dateValidator.verifyTimeOfDayDifference(dates.created(), dates.expires(), "Истекает после " + paymentName + " оплаты");

            if ("второй".equals(paymentName)) {
                // Используем разницу между настройками для второго платежа
                int betweenPaymentsDays = settings.afterSecondPaymentDays() - settings.afterFirstPaymentDays();
                dateValidator.verifyDateDifference(context.getExpiresBeforePayment(), dates.expires(), betweenPaymentsDays, "Увеличение Истекает после второй оплаты");
            }
            orderDatesPage.logOrderState("Состояние после " + paymentName + " оплаты", dates);
        });
    }

    private void verifyNextPaymentEqualsExpires(String paymentName) {
        step("Проверка соответствия 'След. платеж' и 'Истекает' после " + paymentName + " оплаты", () -> {
            OrderDates dates = orderDatesPage.extractAllOrderDates();
            assertEquals(dates.expires().toLocalDate(), dates.nextPayment().toLocalDate(),
                    "После " + paymentName + " оплаты дата 'След. платеж' должна быть равна дате 'Истекает'");
            dateValidator.verifyTimeOfDayDifference(dates.expires(), dates.nextPayment(), "След. платеж от Истекает после " + paymentName + " оплаты");
        });
    }
}
