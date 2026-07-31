package ru.tutor.page.admin;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.ProductConfiguration;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.TabManager;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminOrdersPage {

    // Константа UTM-метки
    private static final String DEFAULT_UTM = TestConfig.getDefUtm();
    private static final String SYSTEM_UTM = TestConfig.getSystemUtm();

    // Названия кнопок
    private static final String APPLY_TEXT = "Применить";
    private static final String MAKE_A_PAYMENT_TEXT = "Провести платеж";
    private static final String SAVE_TEXT = "Сохранить";
    private static final String LOGIN_TEXT = "Войти";
    private static final String CANCEL_YOUR_SUBSCRIPTION_TEXT = "Отмена подписки";
    private static final String YES_TEXT = "Да";
    private static final String UNDERSTAND_TEXT = "Понятно";

    // Локаторы
    private final SelenideElement ORDERS_BUTTON = $("a[href*='orders']");
    private final SelenideElement APPLY_BUTTON = $$("button").find(exactText(APPLY_TEXT));
    private final SelenideElement MAKE_A_PAYMENT = $(".button-modal");
    private final SelenideElement SAVE_BUTTON = $$("button").find(exactText(SAVE_TEXT));
    private final SelenideElement LOGIN_BUTTON = $$("button").find(exactText(LOGIN_TEXT));
    private final SelenideElement CANSEL_SUBSCRIPTION_BUTTON = $$("button").find(exactText(CANCEL_YOUR_SUBSCRIPTION_TEXT));
    private final SelenideElement YES_BUTTON = $$("button").find(exactText(YES_TEXT));
    private final SelenideElement UNDERSTAND_BUTTON = $$("button").find(exactText(UNDERSTAND_TEXT));
    private final SelenideElement EMAIL_INPUT_SEARCH = $("input[placeholder='Email']");
    private final SelenideElement ORDER_NUMBER = $("a[data-discover='true'][href*='/order/']");
    private final SelenideElement SUM_INPUT = $$("input[type=text]").get(0);

    private String lastSearchedEmail; // Добавляем поле для хранения email
    private String lastExtractedOrderNumber;

    // Селектор для поиска Лайт продуктов
    private final ElementsCollection ALL_ORDER_LINKS = $$("a[data-discover='true'][href*='/order/']");

    private SelenideElement getLiteOrderLink() {
        return ALL_ORDER_LINKS.findBy(text("Лайт").or(text("Lite")));
    }

    // Селектор для поиска UTM метки
    private final SelenideElement UTM_TEXT = $x("//div[contains(@class, 'order-info-entry')][.//div[text()='UTM']]/div[2]");


    // Таймаут
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    // Поле для хранения флага aff_rid
    private boolean shouldCheckUtm = false;

    /**
     * Устанавливаем флаг необходимости проверки UTM
     */

    public AdminOrdersPage withUtmCheck(boolean check) {
        this.shouldCheckUtm = check;
        Allure.addAttachment("Настройка UTM проверки",
                String.format("Проверка UTM: %s", check ? "включена":"отключена"));
        return this;
    }

    /**
     * Включаем проверку UTM
     */
    public AdminOrdersPage enableUtmCheck() {
        return withUtmCheck(true);
    }

    /**
     * Выключаем проверку UTM
     */
    public AdminOrdersPage disableUtmCheck() {
        return withUtmCheck(false);
    }

    @Step("Переход в раздел заказов")
    public AdminOrdersPage navigateToOrders() {
        return step("Переход в раздел заказов", () -> {
            ORDERS_BUTTON.shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            return this;
        });
    }

    /**
     * Извлечение номера заказа из элемента
     */
    @Step("Извлечение номера заказ")
    private void extractOrderNumber(){
            String orderLinkText = ORDER_NUMBER.getText();
            lastExtractedOrderNumber = orderLinkText != null ? orderLinkText : null;
    }

    public String getLastOrderNumber() {
        if (lastExtractedOrderNumber == null) {
            throw new IllegalStateException("Номер заказа не найден. Сначала выполните поиск заказа");
        }
        return lastExtractedOrderNumber;
    }


    @Step("Поиск заказа по email: {email}")
    public AdminOrdersPage findOrderByEmail(String email) {
        return step("Поиск заказа по email", () -> {

            this.lastSearchedEmail = email;

            EMAIL_INPUT_SEARCH.shouldBe(visible, DEFAULT_TIMEOUT)
                    .setValue(email);
            UniversalMethod.clickButton(APPLY_BUTTON, APPLY_TEXT);
            WaitHelper.waitForAnimationComplete();

            ORDER_NUMBER.shouldBe(visible, DEFAULT_TIMEOUT);
            extractOrderNumber();
            AllureAttachments.attachText("Поиск заказа", "Заказ для email " + email + " найден");

            return this;
        });
    }

    public String getLastSearchedEmail() {
        return lastSearchedEmail;
    }

    @Step("Переход в заказ для рекуррентного платежа")
    public void processRecurrentPayment() {
        int originalWindowCount = WebDriverRunner.getWebDriver().getWindowHandles().size();

        step("Кликаем по заказу: ", () ->
                UniversalMethod.safeClick(ORDER_NUMBER, "Номер заказа")
        );

        Selenide.Wait().until(driver -> driver.getWindowHandles().size() > originalWindowCount);

        switchTo().window(originalWindowCount);
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();

// переход на др страницу
        step("Проверяем что находимся на странице заказа", () -> {
            String currentUrl = WebDriverRunner.url();
            assert Objects.requireNonNull(currentUrl).contains("/order/") :
                    "Не удалось перейти на страницу заказа. Текущий URL: " + currentUrl;
            AllureAttachments.attachText("URL заказа: ", currentUrl);
        });

    }

    @Step("Проводим оплату")
    public void makePayment(String productId) {
        UniversalMethod.clickButton(MAKE_A_PAYMENT, MAKE_A_PAYMENT_TEXT);

        String price = ProductConfiguration.getSettings(productId).price();

        SUM_INPUT.shouldBe(visible, DEFAULT_TIMEOUT).setValue(price);
        UniversalMethod.clickButton(SAVE_BUTTON, SAVE_TEXT);

        AllureAttachments.attachText("Рекуррент",
                "Рекуррентный платеж успешно проведен. ID продукта: " + productId + ", цена: " + price);
    }

    @Step("Поиск Лайт заказа внутри текущего заказа")
    private SelenideElement findLiteOrderInCurrentOrder() {
        return $$("a[href*='/order/']")
                .findBy(text("Лайт").or(text("Lite")).or(text("лайт")).or(text("lite")));
    }

    @Step("Переходим в Личный кабинет пользователя")
    public void goToUserPersonalAccount() {

        // Запоминаем текущую вкладку

        String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();

        // Сохранённый флаг для проверки utm
        if (shouldCheckUtm) {
            step("Проверка UTM метки (заказ с aff_rid)", () -> {
                processUtm(DEFAULT_UTM);
                Allure.addAttachment("UTM проверка",
                        "✅ Успешно: utm_medium соответствует '" + DEFAULT_UTM + "'");
            });
        } else {
            Allure.addAttachment("UTM проверка",
                    "⏭️ Пропущена - заказ без aff_rid метки" );
        }

        // Кликаем на кнопку Войти
        UniversalMethod.clickButton(LOGIN_BUTTON, LOGIN_TEXT);

        // Переход на другую страницу
        List<String> handles = new ArrayList<>(WebDriverRunner.getWebDriver().getWindowHandles());
        WebDriverRunner.getWebDriver().switchTo().window(handles.get(3));

        // Проверяем, что переключились успешно
        String newWindow = WebDriverRunner.getWebDriver().getWindowHandle();
        if (!newWindow.equals(originalWindow)) {
            WebDriverRunner.getWebDriver().getWindowHandles();
            String originalWindow1 = WebDriverRunner.getWebDriver().getWindowHandle();
            Allure.addAttachment("✅ Успешно переключились на новую вкладку: ", originalWindow1);
        } else {
            Allure.addAttachment("Не удалось переключиться", originalWindow);
        }
    }

    @Step("Проверяем наличие UTM метки определённого значения")
    private void processUtm(String expectedValue) {
        String utmText = UTM_TEXT.shouldBe(visible, DEFAULT_TIMEOUT)
                .getText();

        assertTrue(utmText.contains("utm_medium:"), "Параметр utm_medium не найден");

        // Извлекаем значение
        Pattern pattern = Pattern.compile("utm_medium:\\s*([^,\\s]+)");
        Matcher matcher = pattern.matcher(utmText);

        if (matcher.find()) {
            String actualValue = matcher.group(1);
            assertEquals(expectedValue, actualValue,
                    String.format("UTM medium не совпадает. Ожидалось: %s, Фактически: %s", expectedValue, actualValue));
        } else {
            throw new AssertionError("Не удалось извлечь значение utm_medium");
        }
    }

    /**
     * Переключиться на последнюю вкладку (самую правую)
     */

    @Step("Поиск заказа Renewal, работа с ним")
    public void renewal(String expectedUtm) {
        step("Закрываем ненужные вкладки и переходим на вкладку Orders: ", TabManager::goToTheOrderTab
        );

        UniversalMethod.clickButton(APPLY_BUTTON, APPLY_TEXT);
        WaitHelper.waitForAnimationComplete();
        processRecurrentPayment();
        step(String.format("Сравнием utm_medium с эталоном - '%s'", expectedUtm),
                () -> processUtm(expectedUtm)
        );
        step("Проведение рекурента:", () -> makePayment("755"));
    }

    @Step("Отменяем подписку пользователю через админку")
    public void cancelOrderWithAdmin() {
        UniversalMethod.clickButton(CANSEL_SUBSCRIPTION_BUTTON, CANCEL_YOUR_SUBSCRIPTION_TEXT);
        UniversalMethod.clickButton(YES_BUTTON, YES_TEXT);
        UniversalMethod.clickButton(UNDERSTAND_BUTTON, UNDERSTAND_TEXT);
        WaitHelper.waitForAnimationComplete();
        goToUserPersonalAccount();
    }
}