package ru.tutor.test.executor;

import io.qameta.allure.Allure;
import ru.tutor.ProductConfiguration;
import ru.tutor.page.OrderDatesPage;
import ru.tutor.page.admin.AdminOrderDetails;
import ru.tutor.page.dto.OrderDates;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.DateValidator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс для комплексной проверки заказа Рекуррента
 */

public class ComprehensiveCheckExecutor {

    private final OrderDatesPage orderDatesPage;
    private final AdminOrderDetails  adminOrderDetails;
    private final DateValidator  dateValidator;

    public ComprehensiveCheckExecutor(OrderDatesPage  orderDatesPage,
                                      AdminOrderDetails adminOrderDetails) {
        this.orderDatesPage = orderDatesPage;
        this.adminOrderDetails = adminOrderDetails;
        this.dateValidator = new DateValidator(1); // 1 минута допустимой разницы
    }

    public void performFinalComprehensiveCheck(String productId) {
        Allure.step("ФИНАЛЬНАЯ КОМПЛЕКСНАЯ ПРОВЕРКА", () -> {
            // Получаем ID продукта для определения финальных настроек
//            String productId = adminOrderDetails.getFirstProductId();
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

}