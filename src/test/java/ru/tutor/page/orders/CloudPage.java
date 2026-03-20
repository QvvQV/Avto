package ru.tutor.page.orders;

/*
 * Логика и кнопки связанные с Cloud/Тинькофф. Оплата
 */

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import org.openqa.selenium.By;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.FrameHelper;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.tutor.page.orders.SberPage.logModalType;

public class CloudPage {

    private static final TtPage ttPage = new TtPage();

    // Константы
    private static final Faker faker = new Faker(Locale.ENGLISH);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    //Тестовые данные
    private static final String TEST_CARD_NUMBER = "42424242424242420745555";
    private static final String TEST_CARD = "4242424242424242";
    private static final String TEST_DATE = "0745";
    private static final String TEST_CVV = "555";

    // Названия кнопок
    private static final String BUY_TEXT = "начать обучение за 1 ₽*";
    private static final String ERROR_MESSAGE_TEXT = "Произошла ошибка. Пожалуйста, попробуйте ещё раз.";
    private static final String SUCCESS_TEXT = "Успех";

    //Локаторы в модальном окне
    private static final SelenideElement BUY_BUTTON = $$("button").find(exactText(BUY_TEXT));
    private static final SelenideElement SUCCESS_BUTTON = $$("button").find(exactText(SUCCESS_TEXT));
    private static final SelenideElement ERROR_MESSAGE = $$("p").find(exactText(ERROR_MESSAGE_TEXT));
    private static final SelenideElement ACCESS_BUTTON = $(By.id("access-button"));
    private static final SelenideElement ACCESS_BUTTON_PROD = $(By.id("mainFormaSubmitBtn"));
    private static final SelenideElement EMAIL_INPUT = $("[name='email']");
    private static final SelenideElement EMAIL_INPUT_PROD = $("[name='order[email]']");
    private static final SelenideElement CARD_PAGE_BUTTON = $("[data-test='cardpay-page-button']");
    private static final SelenideElement CARD_INPUT = $("input[automation-id='tui-input-card-group__card'][placeholder*='0000']");
    private static final SelenideElement CARD_PAY_BUTTON = $("[data-test='cardpay-button']");
    public static final SelenideElement FULL_OPTION = $("[name='full']");
    private static final SelenideElement INSTALLMENTS_OPTION = $("[name='installments']");

    //Локаторы отдельного окна оплаты
    private static final SelenideElement CARD_INPUT_NEW_WINDOW = $(By.id("card"));
    private static final SelenideElement DATE_INPUT = $(By.id("date"));
    private static final SelenideElement CVV_INPUT = $(By.id("cvv"));
    private static final SelenideElement CONFIRM_BUTTON = $("button[data-v-59c9a1ad]");
    private static final SelenideElement BACK_TO_SHOP_BUTTON = $("button[data-v-c1ac6fa4]");

    // Внутренние состояния
    private String currentEmail;

    @Step("Заполнение данных модального окна Prod")
    public static void clickBtnStartProd() {
        step("Ввод email", () -> {
            String email = faker.internet().emailAddress(); // сохраняем email
            EMAIL_INPUT_PROD.sendKeys(email);
            System.out.println("Оплата с Прода за 1 руб: " + email);
            universalMethod.clickButton(ACCESS_BUTTON_PROD, "Кнопка подтверждения");
            ERROR_MESSAGE.shouldNot(visible);
        });
    }

    public static String atrEmail() {
        return EMAIL_INPUT.shouldBe(visible).getValue();
    }

    // ввод банковских данных в модалку Cloud
    @Step("Заполнение данных модального окна")
    public static void clickCardPageBtn() {
        step("Ввод валидных банковских данных", () -> {

            sleep(2000);

            PaymentFrameHandler.ensurePaymentFrame();

            CARD_INPUT.shouldBe(visible, DEFAULT_TIMEOUT)
                    .sendKeys(TEST_CARD_NUMBER);

            universalMethod.clickButton(CARD_PAY_BUTTON, "Кнопка Оплатить");

            PaymentFrameHandler.ensureSuccessFrame();

            universalMethod.safeClick(SUCCESS_BUTTON, SUCCESS_TEXT);

            WaitHelper.waitForAnimationComplete();
            FrameHelper.switchToDefaultContent();
        });
    }

    // ==================== МЕТОДЫ ДЛЯ PROD ОКРУЖЕНИЯ ====================

    @Step("Заполнение данных модального окна Prod")
    public static void clickCardPageBtnProd() {
        step("Ввод валидных банковских данных", () -> {
            sleep(2000);
            FrameHelper.switchToFrame(0);
            WaitHelper.waitForPageLoad();
            universalMethod.safeSetValue(CARD_INPUT, TEST_CARD_NUMBER, "Номер карты");
            universalMethod.safeClick(CARD_PAY_BUTTON, "Оплата картой");
            FrameHelper.switchToFrame(0);
            universalMethod.safeClick(SUCCESS_BUTTON,SUCCESS_TEXT);
        });
    }

    // ==================== МЕТОДЫ ДЛЯ РАБОТЫ С МОДАЛКАМИ ====================

    @Step("Выбор опции 'Рассрочка' в модальном окне")
    public static void setDateModal() {
        step("Ввод email и проверка отсутствия ошибок", () -> {
            String email = faker.internet().emailAddress();

            // Заполнение email
            universalMethod.safeSetValue(EMAIL_INPUT, email, "Email");

            // Логирование
            String modalText = ttPage.getTextModal();
            logModalType(modalText, email);

            // Опция
            universalMethod.safeClick(FULL_OPTION, "Оплата");

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
            universalMethod.safeSetValue(EMAIL_INPUT, currentEmail, "Email (полная оплата)");

            // Логирование типа модалки
            String modalText = ttPage.getTextModal();
            logModalType(modalText, currentEmail);

            // Выбор опции полной оплаты
            universalMethod.safeClick(INSTALLMENTS_OPTION, "Полная оплата");

            // Проверка отсутствия ошибок
            ERROR_MESSAGE.shouldNot(visible);

            // Логирование
            AllureAttachments.attachText("Email в модалке", currentEmail);
            System.out.println(modalText + ": " + currentEmail);
        });
    }

    // ==================== МЕТОДЫ ДЛЯ НОВОГО ОКНА ОПЛАТЫ ====================

    @Step("Наполнение тестовыми данными в новом окне")
    public static void fillingWithTestData() {
        step("Ввод номера карты", () ->
                universalMethod.safeSetValue(CARD_INPUT_NEW_WINDOW, TEST_CARD, "Номер карты (новое окно)"));
        step("Ввод срока действия:", () ->
                universalMethod.safeSetValue(DATE_INPUT, TEST_DATE, "Срок действия карты"));
        step("Ввод CVV:", () ->
                universalMethod.safeSetValue(CVV_INPUT, TEST_CVV, "CVV код"));

        step("Отправка формы оплаты", () ->
                universalMethod.safeClick(CONFIRM_BUTTON, "Подтвердить оплату"));

        AllureAttachments.attachText("Введённые банковские данные: ", "\nНомер: " + TEST_CARD +
                "\nСрок действия: " + TEST_DATE + "\nCVV: " + TEST_CVV);

        step("Подтверждение заказа и проверка результата", () -> {

            FrameHelper.switchToFrame(0);
            universalMethod.safeClick(CONFIRM_BUTTON, "Подтвердить заказ");

            WaitHelper.waitForPageLoad();

            FrameHelper.switchToDefaultContent();

            // Проверка успешного завершения
            verifyPaymentSuccess();
        });
    }

    @Step("Проверка успешной оплаты")
    private static void verifyPaymentSuccess() {
        String expectedText = "Вернуться в магазин";

        String actualText = universalMethod.getElementText(BACK_TO_SHOP_BUTTON, "Кнопка вернутся в магазин");

        assertEquals(expectedText, actualText,
                String.format("Текст кнопки не совпадает. Ожидалось: '%s', Фактически: '%s'",
                        expectedText, actualText));

        AllureAttachments.attachText("Проверка текста кнопки",
                "✓ Текст кнопки корректен: " + actualText);
        AllureAttachments.attachScreenshot("Страница после оплаты");
    }

    // ==================== ВНУТРЕННИЙ КЛАСС ДЛЯ РАБОТЫ С IFrame ====================

    private static class PaymentFrameHandler {

        @Step("Поиск iframe с формой оплаты")
        static void ensurePaymentFrame() {
            boolean found = PaymentFrameChecker.findPaymentFrame();
            if (!found) {
                AllureAttachments.attachScreenshot("Ошибка поиска iframe");
                throw new AssertionError("Не удалось найти iframe с формой оплаты");
            }
            AllureAttachments.attachText("Статус", "✓ Форма оплаты найдена");
        }

        @Step("Поиск iframe с подтверждением")
        static void ensureSuccessFrame() {
            WaitHelper.waitFor(() -> !$$("iframe").isEmpty(), DEFAULT_TIMEOUT,
                    "Ожидание iframe после оплаты");

            boolean found = PaymentFrameChecker.findSuccessFrame();
            if (!found) {
                AllureAttachments.attachScreenshot("Ошибка поиска подтверждения");
                throw new AssertionError("Не найден iframe с подтверждением");
            }
        }
    }

    // ==================== ВНУТРЕННИЙ КЛАСС ДЛЯ ПРОВЕРКИ IFrame ====================

    private static class PaymentFrameChecker {

        static boolean findPaymentFrame() {
            return checkFrame(0, CloudPage::isPaymentFrame) ||
                    checkFrame(1, CloudPage::isPaymentFrame) ||
                    checkFrame(2, CloudPage::isPaymentFrame) ||
                    FrameHelper.switchToFrameByCondition(PaymentFrameChecker::isPaymentUrl);
        }

        static boolean findSuccessFrame() {
            return checkFrame(0, CloudPage::isSuccessFrame) ||
                    checkFrame(1, CloudPage::isSuccessFrame) ||
                    checkFrame(2, CloudPage::isSuccessFrame);
        }

        private static boolean checkFrame(int index, java.util.function.BooleanSupplier checker) {
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

        private static boolean isPaymentUrl(String url) {
            return url.contains("payment") ||
                    url.contains("cloudpayments") ||
                    url.contains("pay");
        }
    }

    // ==================== МЕТОДЫ ПРОВЕРКИ СОСТОЯНИЯ ====================

    private static boolean isPaymentFrame() {
        try {
            return CARD_INPUT.exists() && CARD_INPUT.is(visible) ||
                    isPaymentUrl(WebDriverRunner.getWebDriver().getCurrentUrl());
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isSuccessFrame() {
        try {
            return SUCCESS_BUTTON.exists() && SUCCESS_BUTTON.is(visible);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isPaymentUrl(String url) {
        return url.contains("cloudpayments") ||
                url.contains("payment") ||
                url.contains("pay");
    }
}