package ru.tutor.page;

import io.qameta.allure.Allure;
import ru.tutor.page.dto.OrderDates;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.page.admin.AdminOrderDetails;
import ru.tutor.utils.WaitHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.qameta.allure.Allure.step;

public class OrderDatesPage {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final AdminOrderDetails adminOrderDetails;

    public OrderDatesPage(AdminOrderDetails adminOrderDetails) {
        this.adminOrderDetails = adminOrderDetails;
    }

    public LocalDateTime extractCreationDate() {
        return step("Извлечение даты создания заказа", () -> {
            String createdDateTimeStr = adminOrderDetails.getCreationDate();
            LocalDateTime createdDateTime = parseDateTime(createdDateTimeStr);
            AllureAttachments.attachText("Дата создания", createdDateTimeStr);
            return createdDateTime;
        });
    }

    public LocalDateTime extractExpirationDate() {
        String expiresDateTimeStr = adminOrderDetails.getExpirationDate();
        return parseDateTime(expiresDateTimeStr);
    }

    public OrderDates extractAllOrderDates() {
        return new OrderDates(
                parseDateTime(adminOrderDetails.getCreationDate()),
                parseDateTime(adminOrderDetails.getExpirationDate()),
                parseDateTime(adminOrderDetails.getNextPayment())
        );
    }

    public void refreshOrderData() {
        step("Обновление данных заказа", () -> {
            adminOrderDetails.refreshPage();
            WaitHelper.waitForPageLoad();
        });
    }

    public void logOrderState(String description, OrderDates dates) {
        AllureAttachments.attachText(description,
                String.format("Создан: %s\nИстекает: %s\nСлед. платеж: %s",
                        formatDateTime(dates.getCreated()),
                        formatDateTime(dates.getExpires()),
                        formatDateTime(dates.getNextPayment())));
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        try {
            if (dateTimeString == null || dateTimeString.isEmpty() ||
                    dateTimeString.equals("-") || dateTimeString.equals("Не установлено")) {
                return null;
            }
            return LocalDateTime.parse(dateTimeString.trim(), DATE_TIME_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось распарсить дату/время: " + dateTimeString, e);
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "null";
    }
}
