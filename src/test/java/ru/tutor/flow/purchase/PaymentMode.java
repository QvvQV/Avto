package ru.tutor.flow.purchase;

/**
 * Режим платёжного флоу.
 * PAY    — dev: реальная оплата тестовой картой (clickCardPageBtn / payWithApprovedCard).
 * FIXATE — prod: фиксация факта открытия iframe/редиректа, без транзакции.
 */
public enum PaymentMode {
    PAY,
    FIXATE
}
