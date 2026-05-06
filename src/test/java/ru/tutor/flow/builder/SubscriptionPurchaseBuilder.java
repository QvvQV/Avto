package ru.tutor.flow.builder;

import ru.tutor.config.TestConfig;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.TabManager;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionPurchaseBuilder {
    private TtPage.SubscriptionType subscriptionType;
    private PaymentSystem paymentSystem;
    private PaymentMethod paymentMethod;
    private final boolean captureEmail = true;
    private boolean verifyError = false;
    private String expectedError;

    private final TtPage ttPage;
    private final CloudPage cloudPage;
    private final SberPage sberPage;
    private String userEmail;

    public enum PaymentSystem {
        CLOUD, SBER
    }

    public enum PaymentMethod {
        STANDARD,
        INSTALLMENTS,
        APPROVED_CARD,
        FIXED_BALANCE_CARD,
        INSUFFICIENT_FUNDS_CARD,
        EXPIRED_CARD
    }

    public enum PageVariant {
        DEFAULT(false),
        WITH_AFF_RID(true);

        private final boolean useAffRid;
        PageVariant (boolean useAffRid) {this.useAffRid = useAffRid;}
        public boolean useAffRid() { return useAffRid; }
    }

    private PageVariant pageVariant = PageVariant.DEFAULT;

    public SubscriptionPurchaseBuilder withAffRid(){
        this.pageVariant = PageVariant.WITH_AFF_RID;
        return this;
    }

    public boolean hasAffRid() {
        return pageVariant == PageVariant.WITH_AFF_RID;
    }

    private SubscriptionPurchaseBuilder(TtPage ttPage, CloudPage cloudPage, SberPage sberPage) {
        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.sberPage = sberPage;
    }

    public static SubscriptionPurchaseBuilder create(TtPage ttPage, CloudPage cloudPage, SberPage sberPage) {
        return new SubscriptionPurchaseBuilder(ttPage, cloudPage, sberPage);
    }

    // Универсальный метод Типа
    public SubscriptionPurchaseBuilder forSubscription(TtPage.SubscriptionType type) {
        this.subscriptionType = type;
        return this;
    }

    // Универсальный метод для платёжной системы
    public SubscriptionPurchaseBuilder via(PaymentSystem system) {
        this.paymentSystem = system;
        return this;
    }

    // Универсальный метод для способа оплаты
    public SubscriptionPurchaseBuilder withPaymentMethod(PaymentMethod method) {
        this.paymentMethod = method;
        if (method == PaymentMethod.INSUFFICIENT_FUNDS_CARD ||
        method == PaymentMethod.EXPIRED_CARD) {
            this.verifyError = true;
        }
        return this;
    }

    public void withExpectedError(String errorMessage) {
        this.expectedError = errorMessage;
    }

    public SubscriptionPurchaseResult execute() {
        String stepName = buildStepName();

        step(stepName, () -> {
            openMainPageAndCheckTitle(pageVariant.useAffRid());
            ttPage.clickBtnOpenFor();
            selectSubscriptionType();
            processPaymentSystem();

            if (captureEmail) {
                captureUserEmail();
            }

            if (verifyError && expectedError != null) {
                verifyErrorMessage();
            }
        });

        return new SubscriptionPurchaseResult(userEmail);
    }

    private String buildStepName() {
        StringBuilder name = new StringBuilder("Оформление и оплата ");
        name.append(subscriptionType).append(" подписки ");

        if (pageVariant == PageVariant.WITH_AFF_RID) {
            name.append("(c aff_rid) ");
        }

        if (paymentSystem == PaymentSystem.CLOUD) {
            name.append("Cloud");
        } else if (paymentSystem == PaymentSystem.SBER) {
            name.append("Sber");

            if (paymentMethod == PaymentMethod.FIXED_BALANCE_CARD) {
                name.append(" с фиксированным остатком");
            } else if (paymentMethod == PaymentMethod.INSUFFICIENT_FUNDS_CARD) {
                name.append(" с недостаточно средств");
            } else if (paymentMethod == PaymentMethod.EXPIRED_CARD) {
                name.append(" с просроченной карты");
            }
        } else if (paymentMethod == PaymentMethod.INSTALLMENTS) {
            name.append(" в рассрочку");
        }

        return name.toString();
    }

    // Метод для оплаты без aff_rid ссылки

    // Универсальный метод с параметром для aff_rid
    private void openMainPageAndCheckTitle(boolean useAffRid) {
        String url = useAffRid ? AddressPage.URL_MAINRev : AddressPage.URL_MAIN;
        String stepName = useAffRid ? "с aff_rid параметром" : "без aff_rid параметра";

        step(String.format("Открытие главной страницы %s", stepName), () ->
                open(url)
        );

        step("Проверка заголовка главной страницы", () -> {
            assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title(),
                    "Заголовок главной страницы не совпадает");
            AllureAttachments.attachText("Проверка страницы",
                    String.format("Главная страница (%s) загружена корректно", stepName));
        });
    }

    private void selectSubscriptionType() {
        if (subscriptionType == null) {
            throw new IllegalStateException("Тип подписки не выбран!");
        }
        ttPage.selectSubscription(subscriptionType);
    }

    private void processPaymentSystem() {
        if (paymentSystem == PaymentSystem.CLOUD) {
            processCloudPayment();
        } else if (paymentSystem == PaymentSystem.SBER) {
            processSberPayment();
        }
    }

    private void processCloudPayment() {
        if (paymentMethod == PaymentMethod.INSTALLMENTS) {
            cloudPage.setDateModalInstallments();
        } else {
            cloudPage.setDateModal();
        }

        if (captureEmail) {
            userEmail = cloudPage.atrEmail();
        }

        cloudPage.clickCardPageBtn();
    }

    private void processSberPayment() {
        sberPage.fillEmailAndProcessModal();

        if (captureEmail) {
            userEmail = sberPage.getLastUsedEmail();
        }

        TabManager.switchToNewWindow();

        switch (paymentMethod) {
            case FIXED_BALANCE_CARD:
                sberPage.payWithFixedBalanceCard();
                break;
            case INSUFFICIENT_FUNDS_CARD:
                sberPage.payWithInsufficientFundsCard();
                break;
            case EXPIRED_CARD:
                sberPage.payWithExpiredCard();
                break;
            case APPROVED_CARD:
            default:
                sberPage.payWithApprovedCard();
                break;
        }
    }

    private void captureUserEmail() {
        if (userEmail != null) {
            AllureAttachments.attachText("Email пользователя", userEmail);
        }
    }

    private void verifyErrorMessage() {
        String actualError = sberPage.getErrorText();
        assertEquals(expectedError, actualError);
        AllureAttachments.attachText("Проверка ошибки",
                "Ожидалось: " + expectedError + "\nФактически: " + actualError);
    }
}