package ru.tutor.test.executor;

import io.qameta.allure.Allure;
import ru.tutor.constants.ProductConfiguration;
import ru.tutor.page.admin.OrderDatesPage;
import ru.tutor.page.dto.OrderDates;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.DateValidator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс для комплексной проверки заказа Рекуррента
 */

public class ComprehensiveCheckExecutor {

    private final OrderDatesPage orderDatesPage;
    private final DateValidator  dateValidator;

    public ComprehensiveCheckExecutor(OrderDatesPage  orderDatesPage) {
        this.orderDatesPage = orderDatesPage;
        this.dateValidator = new DateValidator(1); // 1 минута допустимой разницы
    }

    public void performFinalComprehensiveCheck(String productId) {
        Allure.step("ФИНАЛЬНАЯ КОМПЛЕКСНАЯ ПРОВЕРКА", () -> {
            // Получаем ID продукта для определения финальных настроек
//            String productId = adminOrderDetails.getFirstProductId();
            ProductConfiguration.ProductSettings settings = ProductConfiguration.getSettings(productId);
            int finalExpectedDays = settings.afterSecondPaymentDays();

            OrderDates dates = orderDatesPage.extractAllOrderDates();
            assertAll("Финальная проверка всех условий",
                    () -> assertNotNull(dates.created(), "Дата создания не должна быть null"),
                    () -> assertNotNull(dates.expires(), "Дата истечения не должна быть null"),
                    () -> assertNotNull(dates.nextPayment(), "Дата следующего платежа не должна быть null"),
                    () -> dateValidator.verifyDateDifference(dates.created(), dates.expires(), finalExpectedDays,
                            "финальная проверка Истекает"),
                    () -> dateValidator.verifyTimeOfDayDifference(dates.created(), dates.expires(),
                            "финальная проверка времени Истекает"),
                    () -> assertEquals(dates.expires().toLocalDate(), dates.nextPayment().toLocalDate(),
                            "Финальная проверка: даты Истекает и След. платеж должны совпадать"),
                    () -> dateValidator.verifyTimeOfDayDifference(dates.expires(), dates.nextPayment(),
                            "финальная проверка времени След. платеж"));
            orderDatesPage.logOrderState("Финальное состояние заказа", dates);

            AllureAttachments.attachText("Финальная проверка", "Проверка завершена для продукта " +
                    productId + " с ожиданием " + finalExpectedDays + " дней");
        });
    }

}