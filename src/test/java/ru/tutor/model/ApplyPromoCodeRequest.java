package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.datafaker.Faker;

import java.util.Objects;

public class ApplyPromoCodeRequest {

    @JsonProperty("promoCodeName")
    private String promoCodeName;

    @JsonProperty("orderNumber")
    private String orderNumber;

    // ==================== КОНСТРУКТОРЫ ====================

    public ApplyPromoCodeRequest() {
    }

    public ApplyPromoCodeRequest(String promoCodeName, String orderNumber) {
        this.promoCodeName = promoCodeName;
        this.orderNumber = orderNumber;
    }

    public String getPromoCodeName() {
        return promoCodeName;
    }

    public void setPromoCodeName(String promoCodeName) {
        this.promoCodeName = promoCodeName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /**
     * Создание запроса с указанным промокодом и номером заказа
     */
    public static ApplyPromoCodeRequest of(String promoCodeName, String orderNumber) {
        return new ApplyPromoCodeRequest(promoCodeName, orderNumber);
    }

    /**
     * Генерация случайного запроса
     */
    public static ApplyPromoCodeRequest generateRandom() {
        Faker faker = new Faker();
        return new ApplyPromoCodeRequest(
                faker.regexify("[A-Z0-9]{8}"),
                faker.regexify("[a-z0-9]{10}")
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(promoCodeName, orderNumber);
    }

    @Override
    public String toString() {
        return "ApplyPromoCodeRequest{" +
                "promoCodeName='" + promoCodeName + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                '}';
    }
}