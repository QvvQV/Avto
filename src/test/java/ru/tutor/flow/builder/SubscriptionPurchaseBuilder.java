package ru.tutor.flow.builder;

import ru.tutor.config.TestConfig;
import ru.tutor.page.CloudPage;
import ru.tutor.page.SberPage;
import ru.tutor.page.addressPage;
import ru.tutor.page.ttPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.TabManager;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionPurchaseBuilder {
    private String subscriptionType;
    private PaymentSystem paymentSystem;
    private PaymentMethod paymentMethod;
    private boolean captureEmail = true;
    private boolean verifyError = false;
    private String expectedError;

    private final ttPage ttPage;
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

    private SubscriptionPurchaseBuilder(ttPage ttPage, CloudPage cloudPage, SberPage sberPage) {
        this.ttPage = ttPage;
        this.cloudPage = cloudPage;
        this.sberPage = sberPage;
    }

    public static SubscriptionPurchaseBuilder create(ttPage ttPage, CloudPage cloudPage, SberPage sberPage) {
        return new SubscriptionPurchaseBuilder(ttPage, cloudPage, sberPage);
    }

    public SubscriptionPurchaseBuilder forPremium() {
        this.subscriptionType = "Premium";
        return this;
    }

    public SubscriptionPurchaseBuilder forLife() {
        this.subscriptionType = "Life";
        return this;
    }

    public SubscriptionPurchaseBuilder forLite() {
        this.subscriptionType = "Lite";
        return this;
    }

    public SubscriptionPurchaseBuilder viaCloud() {
        this.paymentSystem = PaymentSystem.CLOUD;
        return this;
    }

    public SubscriptionPurchaseBuilder viaSber() {
        this.paymentSystem = PaymentSystem.SBER;
        return this;
    }

    public SubscriptionPurchaseBuilder withStandardPayment() {
        this.paymentMethod = PaymentMethod.STANDARD;
        return this;
    }

    public SubscriptionPurchaseBuilder withInstallments() {
        this.paymentMethod = PaymentMethod.INSTALLMENTS;
        return this;
    }

    public SubscriptionPurchaseBuilder withApprovedCard() {
        this.paymentMethod = PaymentMethod.APPROVED_CARD;
        return this;
    }

    public SubscriptionPurchaseBuilder withFixedBalanceCard() {
        this.paymentMethod = PaymentMethod.FIXED_BALANCE_CARD;
        return this;
    }

    public SubscriptionPurchaseBuilder withInsufficientFundsCard() {
        this.paymentMethod = PaymentMethod.INSUFFICIENT_FUNDS_CARD;
        this.verifyError = true;
        return this;
    }

    public SubscriptionPurchaseBuilder withExpiredCard() {
        this.paymentMethod = PaymentMethod.EXPIRED_CARD;
        this.verifyError = true;
        return this;
    }

    public SubscriptionPurchaseBuilder withoutEmailCapture() {
        this.captureEmail = false;
        return this;
    }

    public SubscriptionPurchaseBuilder withExpectedError(String errorMessage) {
        this.expectedError = errorMessage;
        return this;
    }

    public SubscriptionPurchaseResult execute() {
        String stepName = buildStepName();

        step(stepName, () -> {
            openMainPageAndCheckTitle();
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

    public SubscriptionPurchaseResult executeAffRid() {
        String stepName = buildStepName();

        step(stepName, () -> {
            openMainPageAndCheckTitleAffRid();
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

    private void openMainPageAndCheckTitle() {
        step("Открытие страницы", () -> {
            open(addressPage.urlMain);
        });
        step("Проверка заголовка главной страницы", () -> {
            assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
            AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
        });
    }

    // Метод для оплаты с главной страницы, но с aff_rid ссылкой

    private void openMainPageAndCheckTitleAffRid() {
        step("Открытие страницы", () -> {
            open(addressPage.urlMainRev);
        });
        step("Проверка заголовка главной страницы", () -> {
            assertEquals(TestConfig.Texts.MAIN_PAGE_TITLE, title());
            AllureAttachments.attachText("Проверка страницы", "Главная страница загружена корректно");
        });
    }

    private void selectSubscriptionType() {
        switch (subscriptionType) {
            case "Premium":
                ttPage.clickPremium();
                break;
            case "Life":
                ttPage.clickLife();
                break;
            case "Lite":
                ttPage.clickLite99();
                break;
        }
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
            case APPROVED_CARD:
                sberPage.payWithApprovedCard();
                break;
            case FIXED_BALANCE_CARD:
                sberPage.payWithFixedBalanceCard();
                break;
            case INSUFFICIENT_FUNDS_CARD:
                sberPage.payWithInsufficientFundsCard();
                break;
            case EXPIRED_CARD:
                sberPage.payWithExpiredCard();
                break;
            default:
                sberPage.payWithApprovedCard();
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