package ru.tutor.flow;

import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.builder.SubscriptionPurchaseBuilder;
import ru.tutor.flow.builder.SubscriptionPurchaseResult;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;

import java.util.function.Consumer;

public class PurchaseFlow {

    private final TtPage ttPage;
    private final CloudPage cloudPage;
    private final SberPage sberPage;

    public PurchaseFlow(TtPage ttPage, CloudPage cloudPage, SberPage sberPage) {
        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.sberPage = sberPage;
    }

    private PurchaseResult purchase(Consumer<SubscriptionPurchaseBuilder> configurator) {
        SubscriptionPurchaseBuilder builder = SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage);
        configurator.accept(builder);
        SubscriptionPurchaseResult result = builder.execute();
        return new PurchaseResult(result.userEmail());
    }

    @Step("Оплата Премиума по Клауду")
    public PurchaseResult premium() {
        return purchase(b -> b.forSubscription(TtPage.SubscriptionType.PREMIUM)
                .via(SubscriptionPurchaseBuilder.PaymentSystem.AUTO)
                .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.STANDARD));
    }

    @Step("Оплата Премиума по aff_rid ссылке Клауд")
    public PurchaseResult premiumAffRid() {
        return purchase(b -> b.forSubscription(TtPage.SubscriptionType.PREMIUM)
                .via(SubscriptionPurchaseBuilder.PaymentSystem.AUTO)
                .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.STANDARD)
                .withAffRid());
    }

    @Step("Оплата Пожизненного доступа по Клауду")
    public PurchaseResult life() {
        return purchase(b -> b.forSubscription(TtPage.SubscriptionType.LIFE)
                .via(SubscriptionPurchaseBuilder.PaymentSystem.AUTO)
                .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.STANDARD));
    }

    @Step("Оплата долями по Клауду")
    public PurchaseResult lifeInstallments() {
        return purchase(b -> b.forSubscription(TtPage.SubscriptionType.LIFE)
                .via(SubscriptionPurchaseBuilder.PaymentSystem.AUTO)
                .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.INSTALLMENTS));
    }

    @Step("Оплата Лайт по Клауду")
    public PurchaseResult lite() {
        return purchase(b -> b.forSubscription(TtPage.SubscriptionType.LITE)
                .via(SubscriptionPurchaseBuilder.PaymentSystem.AUTO)
                .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.STANDARD));
    }

    @Step("Оплата Премиум по карте Сбер с фиксированным остатком")
    public PurchaseResult premiumFixSber() {
        return purchase(b -> b.forSubscription(TtPage.SubscriptionType.PREMIUM)
                .via(SubscriptionPurchaseBuilder.PaymentSystem.SBER)
                .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.FIXED_BALANCE_CARD));
    }

    @Step("Ошибка 'Недостаточно средств' по карте Сбер")
    public PurchaseResult premiumInsufficientFundsSber() {
        return purchase(b -> b.forSubscription(TtPage.SubscriptionType.PREMIUM)
                .via(SubscriptionPurchaseBuilder.PaymentSystem.SBER)
                .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.INSUFFICIENT_FUNDS_CARD)
                .withExpectedError(TestConfig.Texts.ERROR_MESSAGE));
    }

    @Step("Ошибка 'Невалидная карта' по карте Сбер")
    public PurchaseResult premiumExpiredSber() {
        return purchase(b -> b.forSubscription(TtPage.SubscriptionType.PREMIUM)
                .via(SubscriptionPurchaseBuilder.PaymentSystem.SBER)
                .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.EXPIRED_CARD)
                .withExpectedError(TestConfig.Texts.ERROR_MESSAGE));
    }

    @Step("Проверка открытия модального окна {type}")
    public void verifyModalOpens(TtPage.SubscriptionType type) {
        SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                .forSubscription(type)
                .modalOnly()
                .execute();
    }

    @Step("Проверка открытия модального окна {type}")
    public void verifyInstallmentOpens(TtPage.SubscriptionType type) {
        SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                .forSubscription(type)
                .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.INSTALLMENTS)
                .modalOnly()
                .execute();
    }


    @Step("Проверка открытия 'Плати частями' для {type}")
    public void verifyPartPaymentOpens(TtPage.SubscriptionType type) {
        SubscriptionPurchaseBuilder.create(ttPage, cloudPage, sberPage)
                .forSubscription(type)
                .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.PARTS)
                .modalOnly()
                .execute();
    }

    @Step("Оплата 'лати частями' по Sber")
    public PurchaseResult partPayment() {
        return purchase(b -> b.forSubscription(TtPage.SubscriptionType.LIFE)
        .via(SubscriptionPurchaseBuilder.PaymentSystem.AUTO)
        .withPaymentMethod(SubscriptionPurchaseBuilder.PaymentMethod.PARTS)
        );
    }
}
