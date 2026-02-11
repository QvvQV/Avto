package ru.tutor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WithdrawalRequest {

    @JsonProperty("amount")
    private Integer amount;

    // Конструктор
//    public WithdrawalRequest() {}

    public WithdrawalRequest(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

//    public void setAmount(Integer amount) {
//        this.amount = amount;
//    }
//
//    // Дополнительные методы
//    public boolean isValidAmount() {
//        return amount != null && amount > 0;
//    }
//
//    public boolean isAmountInRange(Integer min, Integer max) {
//        return amount != null && amount >= min && amount <= max;
//    }
}
