package ru.tutor.model.promoCode;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.datafaker.Faker;

public record ApplyPromoCodeRequest(
    @JsonProperty("promoCodeName") String promoCodeName,
    @JsonProperty("orderNumber") String orderNumber
) {
    public static ApplyPromoCodeRequest of(String promoCodeName, String orderNumber) {
        return new ApplyPromoCodeRequest(promoCodeName, orderNumber);
    }

    public static ApplyPromoCodeRequest generateRandom() {
        Faker faker = new Faker();
        return new ApplyPromoCodeRequest(
                faker.regexify("[A-Z0-9]{8}"),
                faker.regexify("[a-z0-9]{10}")
        );
    }
}
