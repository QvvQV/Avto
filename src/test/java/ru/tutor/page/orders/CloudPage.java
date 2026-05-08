package ru.tutor.page.orders;

/*
 * Логика и кнопки связанные с Cloud/Тинькофф. Оплата
 */

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import org.openqa.selenium.By;
import ru.tutor.config.TestConfig;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.FrameHelper;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.tutor.page.orders.SberPage.logModalType;

public class CloudPage {

    private final TtPage ttPage = new TtPage();

    // Константы
    private final Faker faker = new Faker(Locale.ENGLISH);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    //Тестовые данные
    private static final String TEST_CARD_NUMBER = TestConfig.Cards.getCloudCardNumber();
    private static final String TEST_CARD = TestConfig.Cards.getCloudCardShort();
    private static final String TEST_DATE = TestConfig.Cards.getCloudCardExpiry();
    private static final String TEST_CVV = TestConfig.Cards.getCloudCardCvc();

    // Названия кнопок
    private static final String BUY_TEXT = "начать обучение за 1 ₽*";
    private static final String ERROR_MESSAGE_TEXT = "Произошла ошибка. Пожалуйста, попробуйте ещё раз.";
    private static final String SUCCESS_TEXT = "Успех";

    //Локаторы в модальном окне
    private final SelenideElement BUY_BUTTON = $$("button").find(exactText(BUY_TEXT));
    private final SelenideElement SUCCESS_BUTTON = $$("button").find(exactText(SUCCESS_TEXT));
    private final SelenideElement ERROR_MESSAGE = $$("p").find(exactText(ERROR_MESSAGE_TEXT));
    private final SelenideElement ACCESS_BUTTON = $(By.id("access-button"));
    private final SelenideElement ACCESS_BUTTON_PROD = $(By.id("mainFormaSubmitBtn"));
    private final SelenideElement EMAIL_INPUT = $("[name='email']");
    private final SelenideElement EMAIL_INPUT_PROD = $("[name='order[email]']");
    private final SelenideElement CARD_PAGE_BUTTON = $("[data-test='cardpay-page-button']");
    private final SelenideElement CARD_INPUT = $("input[automation-id='tui-input-card-group__card'][placeholder*='0000']");
    private final SelenideElement CARD_PAY_BUTTON = $("[data-test='cardpay-button']");
    public final SelenideElement FULL_OPTION = $("[name='full']");
    private final SelenideElement INSTALLMENTS_OPTION = $("[name='installments']");

    //Локаторы отдельного окна оплаты
    private final SelenideElement CARD_INPUT_NEW_WINDOW = $(By.id("card"));
    private final SelenideElement DATE_INPUT = $(By.id("date"));
    private final SelenideElement CVV_INPUT = $(By.id("cvv"));
    private final SelenideElement CONFIRM_BUTTON = $("button[data-v-59c9a1ad]");
    private final SelenideElement BACK_TO_SHOP_BUTTON = $("button[data-v-c1ac6fa4]");

    // Внутренние состояния
    private String currentEmail;
    private final PaymentFrameHandler frameHandler = new PaymentFrameHandler();
    private final PaymentFrameChecker frameChecker = new PaymentFrameChecker();

    @Step("Заполнение данных модального окна Prod")
    public void clickBtnStartProd() {
        step("Ввод email", () -> {
            String email = faker.internet().emailAddress(); // сохраняем email
            EMAIL_INPUT_PROD.sendKeys(email);
            System.out.println("Оплата с Прода за 1 руб: " + email);
            UniversalMethod.clickButton(ACCESS_BUTTON_PROD, "Кнопка подтверждения");
            ERROR_MESSAGE.shouldNot(visible);
        });
    }

    public String atrEmail() {
        return EMAIL_INPUT.shouldBe(visible).getValue();
    }

    // ввод банковских данных в модалку Cloud
    @Step("Заполнение данных модального окна")
    public void clickCardPageBtn() {
        step("Ввод валидных банковских данных", () -> {

            sleep(2000);

            frameHandler.ensurePaymentFrame();

            if (CARD_PAGE_BUTTON.is(visible)) {
                UniversalMethod.safeClick(CARD_PAGE_BUTTON, "Банковской картой");
                sleep(2000);
            }

            CARD_INPUT.shouldBe(visible, DEFAULT_TIMEOUT)
                    .sendKeys(TEST_CARD_NUMBER);

            UniversalMethod.clickButton(CARD_PAY_BUTTON, "Кнопка Оплатить");

            frameHandler.ensureSuccessFrame();

            UniversalMethod.safeClick(SUCCESS_BUTTON, SUCCESS_TEXT);

            WaitHelper.waitForAnimationComplete();
            FrameHelper.switchToDefaultContent();
        });
    }

    // ==================== МЕТОДЫ ДЛЯ PROD ОКРУЖЕНИЯ ====================

    @Step("Заполнение данных модального окна Prod")
    public void clickCardPageBtnProd() {
        step("Ввод валидных банковских данных", () -> {
            sleep(2000);
            FrameHelper.switchToFrame(0);
            WaitHelper.waitForPageLoad();
            UniversalMethod.safeSetValue(CARD_INPUT, TEST_CARD_NUMBER, "Номер карты");
            UniversalMethod.safeClick(CARD_PAY_BUTTON, "Оплата картой");
            FrameHelper.switchToFrame(0);
            UniversalMethod.safeClick(SUCCESS_BUTTON,SUCCESS_TEXT);
        });
    }

    // ==================== МЕТОДЫ ДЛЯ РАБОТЫ С МОДАЛКАМИ ====================

    @Step("Выбор опции 'Рассрочка' в модальном окне")
    public void setDateModal() {
        step("Ввод email и проверка отсутствия ошибок", () -> {
            String email = faker.internet().emailAddress();

            // Заполнение email
            UniversalMethod.safeSetValue(EMAIL_INPUT, email, "Email");

            // Логирование
            String modalText = ttPage.getTextModal();
            logModalType(modalText, email);

            // Опция
            UniversalMethod.safeClick(FULL_OPTION, "Оплата");

            // Проверка отсутсвия ошибок
            ERROR_MESSAGE.shouldNot(visible);

            // Логирование
            AllureAttachments.attachText("Email в модалке", email);
            System.out.println(modalText + ": " + email);
        });
    }

    @Step("Выбор опции 'Полная оплата' в модальном окне")
    public void setDateModalInstallments(){
        step("Выбор полной оплаты и заполнение email", () -> {
            currentEmail = faker.internet().emailAddress();

            // Заполнение email
            UniversalMethod.safeSetValue(EMAIL_INPUT, currentEmail, "Email (полная оплата)");

            // Логирование типа модалки
            String modalText = ttPage.getTextModal();
            logModalType(modalText, currentEmail);

            // Выбор опции полной оплаты
            UniversalMethod.safeClick(INSTALLMENTS_OPTION, "Полная оплата");

            // Проверка отсутствия ошибок
            ERROR_MESSAGE.shouldNot(visible);

            // Логирование
            AllureAttachments.attachText("Email в модалке", currentEmail);
            System.out.println(modalText + ": " + currentEmail);
        });
    }

    // ==================== МЕТОДЫ ДЛЯ НОВОГО ОКНА ОПЛАТЫ ====================

    @Step("Наполнение тестовыми данными в новом окне")
    public void fillingWithTestData() {
        step("Ввод номера карты", () ->
                UniversalMethod.safeSetValue(CARD_INPUT_NEW_WINDOW, TEST_CARD, "Номер карты (новое окно)"));
        step("Ввод срока действия:", () ->
                UniversalMethod.safeSetValue(DATE_INPUT, TEST_DATE, "Срок действия карты"));
        step("Ввод CVV:", () ->
                UniversalMethod.safeSetValue(CVV_INPUT, TEST_CVV, "CVV код"));

        step("Отправка формы оплаты", () ->
                UniversalMethod.safeClick(CONFIRM_BUTTON, "Подтвердить оплату"));

        AllureAttachments.attachText("Введённые банковские данные: ", "\nНомер: " + TEST_CARD +
                "\nСрок действия: " + TEST_DATE + "\nCVV: " + TEST_CVV);

        step("Подтверждение заказа и проверка результата", () -> {

            FrameHelper.switchToFrame(0);
            UniversalMethod.safeClick(CONFIRM_BUTTON, "Подтвердить заказ");

            WaitHelper.waitForPageLoad();

            FrameHelper.switchToDefaultContent();

            // Проверка успешного завершения
            verifyPaymentSuccess();
        });
    }

    @Step("Проверка успешной оплаты")
    private void verifyPaymentSuccess() {
        String expectedText = "Вернуться в магазин";

        String actualText = UniversalMethod.getElementText(BACK_TO_SHOP_BUTTON, "Кнопка вернутся в магазин");

        assertEquals(expectedText, actualText,
                String.format("Текст кнопки не совпадает. Ожидалось: '%s', Фактически: '%s'",
                        expectedText, actualText));

        AllureAttachments.attachText("Проверка текста кнопки",
                "✓ Текст кнопки корректен: " + actualText);
        AllureAttachments.attachScreenshot("Страница после оплаты");
    }

    // ==================== ВНУТРЕННИЙ КЛАСС ДЛЯ РАБОТЫ С IFrame ====================

    private class PaymentFrameHandler {

        @Step("Поиск iframe с формой оплаты")
        void ensurePaymentFrame() {
            boolean found = frameChecker.findPaymentFrame();
            if (!found) {
                AllureAttachments.attachScreenshot("Ошибка поиска iframe");
                throw new AssertionError("Не удалось найти iframe с формой оплаты");
            }
            AllureAttachments.attachText("Статус", "✓ Форма оплаты найдена");
        }

        @Step("Поиск iframe с подтверждением")
        void ensureSuccessFrame() {
            WaitHelper.waitFor(() -> !$$("iframe").isEmpty(), DEFAULT_TIMEOUT,
                    "Ожидание iframe после оплаты");

            boolean found = frameChecker.findSuccessFrame();
            if (!found) {
                AllureAttachments.attachScreenshot("Ошибка поиска подтверждения");
                throw new AssertionError("Не найден iframe с подтверждением");
            }
        }
    }

    // ==================== ВНУТРЕННИЙ КЛАСС ДЛЯ ПРОВЕРКИ IFrame ====================

    private class PaymentFrameChecker {

        boolean findPaymentFrame() {
            return checkFrame(0, CloudPage.this::hasPaymentForm) ||
                    checkFrame(1, CloudPage.this::hasPaymentForm) ||
                    checkFrame(2, CloudPage.this::hasPaymentForm) ||
                    FrameHelper.switchToFrameByCondition(PaymentFrameChecker.this::isPaymentUrl);
        }

        boolean findSuccessFrame() {
            return checkFrame(0, CloudPage.this::isSuccessFrame) ||
                    checkFrame(1, CloudPage.this::isSuccessFrame) ||
                    checkFrame(2, CloudPage.this::isSuccessFrame);
        }

        private boolean checkFrame(int index, java.util.function.BooleanSupplier checker) {
            try {
                FrameHelper.switchToFrame(index);
                if (checker.getAsBoolean()) {
                    return true;
                }

                FrameHelper.switchToDefaultContent();
            } catch (Exception e) {
                FrameHelper.switchToDefaultContent();
            }
            return false;
        }

        private boolean isPaymentUrl(String url) {
            return url.contains("payment") ||
                    url.contains("cloudpayments") ||
                    url.contains("pay");
        }
    }

    // ==================== МЕТОДЫ ПРОВЕРКИ СОСТОЯНИЯ ====================

    private boolean hasPaymentForm() {
        try {
            return (CARD_INPUT.exists() && CARD_INPUT.is(visible)) ||
                    (CARD_PAGE_BUTTON.exists() && CARD_PAGE_BUTTON.is(visible));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSuccessFrame() {
        try {
            return SUCCESS_BUTTON.exists() && SUCCESS_BUTTON.is(visible);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPaymentUrl(String url) {
        return url.contains("cloudpayments") ||
                url.contains("payment") ||
                url.contains("pay");
    }
}