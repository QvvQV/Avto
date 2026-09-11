package ru.tutor.flow.builder;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.FrameHelper;
import ru.tutor.utils.TabManager;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private boolean modalOnly = false;
    private String customUrl;

    public enum PaymentSystem {
        CLOUD, SBER, AUTO
    }

    public enum PaymentMethod {
        STANDARD,
        INSTALLMENTS,
        APPROVED_CARD,
        FIXED_BALANCE_CARD,
        INSUFFICIENT_FUNDS_CARD,
        EXPIRED_CARD,
        PARTS
    }

    public enum PageVariant {
        DEFAULT(false),
        WITH_AFF_RID(true);

        private final boolean useAffRid;

        PageVariant(boolean useAffRid) {
            this.useAffRid = useAffRid;
        }

        public boolean useAffRid() {
            return useAffRid;
        }
    }

    private PageVariant pageVariant = PageVariant.DEFAULT;

    public SubscriptionPurchaseBuilder withAffRid() {
        this.pageVariant = PageVariant.WITH_AFF_RID;
        return this;
    }

    public boolean hasAffRid() {
        return pageVariant == PageVariant.WITH_AFF_RID;
    }

    @Step("Включить режим проверки только открытия модального окна")
    public SubscriptionPurchaseBuilder modalOnly() {
        this.modalOnly = true;
        return this;
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
            selectSubscriptionType(); // открытие модалки
            if (modalOnly) {
                verifyModalOpened(); // только проверить и закрыть
                openPaymentModalOnly(); // email + клик оплатить + проверка открытия окна
            } else {
                processPaymentSystem(); // иначе продолжить оплату

                if (captureEmail)
                    captureUserEmail();


                if (verifyError && expectedError != null)
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
            if (paymentMethod == PaymentMethod.INSTALLMENTS) {
                name.append(" в рассрочку");
            }
        } else if (paymentSystem == PaymentSystem.SBER) {
            name.append("Sber");

            if (paymentMethod == PaymentMethod.FIXED_BALANCE_CARD) {
                name.append(" с фиксированным остатком");
            } else if (paymentMethod == PaymentMethod.INSUFFICIENT_FUNDS_CARD) {
                name.append(" с недостаточно средств");
            } else if (paymentMethod == PaymentMethod.EXPIRED_CARD) {
                name.append(" с просроченной карты");
            }
        }

        return name.toString();
    }

    // Метод для оплаты без aff_rid ссылки

    // Универсальный метод с параметром для aff_rid
    private void openMainPageAndCheckTitle(boolean useAffRid) {
//        String url = useAffRid ? AddressPage.URL_MAIN_REV : AddressPage.URL_MAIN;
        String url;
        if (customUrl != null) {
            url = customUrl;
        } else {
            url = useAffRid ? AddressPage.URL_MAIN_REV : AddressPage.URL_MAIN;
        }
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

        switch (paymentSystem) {
            case CLOUD -> processCloudPayment();
            case SBER -> processSberPayment();
            case AUTO -> processAutoPayment();
        }
    }

    private void processCloudPayment() {
        if (paymentMethod == PaymentMethod.INSTALLMENTS) {
            cloudPage.setDateModalInstallments();
            cloudPage.dolyameFormDemo();
        } else {
            cloudPage.setDateModal();
            cloudPage.clickCardPageBtn();
        }

        if (captureEmail) {
            userEmail = (paymentMethod == PaymentMethod.INSTALLMENTS)
                    ? cloudPage.getCurrentEmail()
                    : cloudPage.atrEmail();
        }
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

    // Только открытие мод. окон платёжных систем Прод
    private void verifyModalOpened() {
        step("Проверка открытия модального окна для " + subscriptionType.getDisplayName(), () -> {
            assertEquals(subscriptionType.getExpectedModalText(), ttPage.getTextModal(),
                    "Текст модального окна не совпадает для " + subscriptionType.getDisplayName());
            AllureAttachments.attachText("Модальное окно открыто",
                    subscriptionType.getDisplayName() + " - " + subscriptionType.getExpectedModalText());
            AllureAttachments.attachScreenshot("Модальное окно: " + subscriptionType.getDisplayName());
        });
    }

    private void openPaymentModalOnly() {
        step("Ввод почты и открытия окна оплаты", () -> {
            boolean installments = paymentMethod == PaymentMethod.INSTALLMENTS;
            boolean installmentsSber = paymentMethod == PaymentMethod.PARTS;

            if (TestConfig.isProd() && installments) {
                cloudPage.setDateModalInstallments(); //оплатить долями
                cloudPage.verifyInstallmentsRedirect();
//                cloudPage.verifyInstallmentsPhoneForm(); // ждём телефона (10с) + скриншот
                return;
            }
            if (TestConfig.isProd() && installmentsSber) {
                cloudPage.setDateModalInstallmentsParts(); //Sber
                sberPage.verifyPartsPhoneForm();
                return;
            }
            userEmail = cloudPage.setDateModal(); // клик оплатить

            PaymentSystem detected = detectActiveGateway();
            AllureAttachments.attachText("Платёжный шлюз", detected.name());

            if (detected == PaymentSystem.CLOUD) {
               step("Фиксация iframe Cloud", () -> {
                   verifyCloudPaymentWindow();         // iframe cloudpayments открыт
                   AllureAttachments.attachScreenshot("Окно оплаты Cloud"); // фото
               });
            } else {
                step("Фиксация редиректа на стр Сбер", () -> {
                    TabManager.switchToNewWindow();
                    verifySberPaymentWindow();          // URL = sber/ -> факт открытия
                    AllureAttachments.attachScreenshot("Окно оплаты Sber");
                });
            }
        });
    }

    private void verifyCloudPaymentWindow() {
        boolean found = $$("iframe").stream().anyMatch(frame -> {
            String src = frame.getAttribute("src");
            return src != null && src.contains("cloudpayments");
        });
        assertTrue(found, "Окно оплаты Cloud не открылось (iframe cloudpayments не найден)");
        AllureAttachments.attachText("Окно оплаты Cloud", "✓ iframe cloudpayments открыт");
    }

    private void verifySberPaymentWindow() {
        TabManager.switchToPaymentPageAndVerify(); // ждёт окно, проверяет URL, скриншот внутри
    }

    // Универсальный метод оплаты Cloud/Sber

    private PaymentSystem detectActiveGateway() {
        sleep(2000);
        for (SelenideElement iframe : $$("iframe")) {
            String src = iframe.getAttribute("src");
            if (src != null && src.contains("cloudpayments")) {
                return PaymentSystem.CLOUD;
            }
        }
        return PaymentSystem.SBER;
    }

    private void processAutoPayment() {
        if (paymentMethod == PaymentMethod.INSTALLMENTS) {
            cloudPage.setDateModalInstallments(); // клик оплатить Долями CLOUD
            cloudPage.dolyameFormDemo();
            userEmail = cloudPage.getCurrentEmail();
            return;
        }
        if (paymentMethod == PaymentMethod.PARTS) {
            cloudPage.setDateModalInstallmentsParts(); // email + "Плати частями"
            sberPage.executeFullFlow();
            userEmail = cloudPage.getCurrentEmail();
            return;
        }
        userEmail = cloudPage.setDateModal();

        PaymentSystem detected = detectActiveGateway();
        AllureAttachments.attachText("Платёжный шлюз", detected.name());

        if (detected == PaymentSystem.CLOUD) {
            cloudPage.clickCardPageBtn();
        } else {
            TabManager.switchToNewWindow();
            sberPage.payWithApprovedCard();
        }
    }

    public SubscriptionPurchaseBuilder withCustomUrl(String url) {
        this.customUrl = url;
        return this;
    }
    }