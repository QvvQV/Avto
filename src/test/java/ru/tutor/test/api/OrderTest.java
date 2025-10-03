package ru.tutor.test.api;


import io.qameta.allure.junit5.AllureJunit5;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.api.Steps.OrderSteps;
import ru.tutor.page.addressPage;

import java.util.List;

@ExtendWith(AllureJunit5.class)
public class OrderTest {

    private final OrderSteps orderSteps = new OrderSteps(addressPage.apiDevAdress);
    private final Faker faker = new Faker();

    @Test
    @DisplayName("Полный цикл создания и оплаты заказа")
    void createOrderAndPayTest() {
        String email = faker.internet().emailAddress();
        List<String> products = List.of("519");

        // Создать заказ
        String orderId = orderSteps.createOrder(email, products);

        // Intent
        orderSteps.createIntent(orderId);

        // BIN check
        orderSteps.checkBin("424242");

        // Оплата (тестовая карта VISA)
        orderSteps.payOrder(orderId, "4242424242424242", "12/29", "123");
    }
}
