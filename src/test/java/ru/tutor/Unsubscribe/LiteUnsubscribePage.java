package ru.tutor.Unsubscribe;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LiteUnsubscribePage {

        // Константы для тестов
    private static final String ACTIVE_TEXT = "активна";
    private static final String DISABLE_ACCESS_TEXT = "Отключить доступ";
    private static final String DEACTIVATE_ACCESS_TEXT = "Деактивировать доступ";
    private static final String DEACTIVATE_TEXT = "Деактивировать";
    private static final String MISS_CHANCE_TEXT = "Упустить свой шанс";

        // Локаторы Premium подписки
    private static final SelenideElement ACTIVE_BUTTON = $$("button").find(exactText(ACTIVE_TEXT));
    private static final SelenideElement H3_TEXT = $("h3");

    // Локаторы Lite одписки
    private static final SelenideElement HREF = $("a[href*='payments']");
    private static final SelenideElement DISABLE_ACCESS_BUTTON = $$("button").find(exactText(DISABLE_ACCESS_TEXT));
    private static final SelenideElement DEACTIVE_ACCESS_BUTTON = $$("button").find(exactText(DEACTIVATE_ACCESS_TEXT));
    private static final SelenideElement DEACTIVE_BUTTON = $$("button").find(exactText(DEACTIVATE_TEXT));
    private static final SelenideElement MISS_CHANCE_BUTTON = $$("button").find(exactText(MISS_CHANCE_TEXT));


    // Универсальные методы
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Универсальный метод для клика по кнопке с проверками
     */
    @Step("Клик по кнопке: {buttonName}")
    private static void clickButton(SelenideElement button, String buttonName) {
        step("Клик по кнопке '" + buttonName + "'", () -> {
            button.scrollTo()
                    .shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldBe(interactable, DEFAULT_TIMEOUT)
                    .click();

            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachText("Кнопка нажата", "✅ " + buttonName);
        });
    }

    /**
     * Универсальный метод для клика по кнопке с ожиданием загрузки страницы
     */
    @Step("Клик по кнопке с переходом: {buttonName}")
    private static void clickButtonWithPageLoad(SelenideElement button, String buttonName) {
        step("Клик по кнопке '" + buttonName + "' с переходом", () -> {
            String originalUrl = WebDriverRunner.url();

            button.scrollTo()
                    .shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldBe(interactable, DEFAULT_TIMEOUT)
                    .click();

            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();

            if (!WebDriverRunner.url().equals(originalUrl)) {
                AllureAttachments.attachText("Переход",
                        "Успешный переход после клика на: " + buttonName);
            }

            AllureAttachments.attachText("Кнопка нажата", "✅ " + buttonName + " - переход выполнен");
        });
    }

    /**
     * Универсальная проверка что кнопка enabled
     */

    private static void verifyButtonIsEnabled(SelenideElement button, String buttonName) {
        step("Проверка что кнопка '" + buttonName + "' enabled", () -> {
            button.shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldNotHave(attribute("disabled"))
                    .shouldBe(interactable)
                    .shouldHave(exactText(buttonName));

            AllureAttachments.attachText("Валидация", "✅ Кнопка '" + buttonName + "' доступна для клика");
        });
    }

    /**
     * Получение и логирование текста заголовка
     */
    @Step("Получение текста заголовка")
    private static String getHeaderText() {
        return step("Заголовок страницы", () -> {
            String headerText = H3_TEXT.shouldBe(visible, DEFAULT_TIMEOUT)
                    .getText()
                    .trim();
            AllureAttachments.attachText("Заголовок страницы", headerText);
            return headerText;
        });
    }

    @Step("Клик по кнопке 'Активна'")
    public static void clickActiveButton() {
        clickButton(ACTIVE_BUTTON, ACTIVE_TEXT);

    }

    @Step("Клик по кнопке 'Отключить доступ'")
    public static void clickDisableAccessButton() {
        clickButtonWithPageLoad(DISABLE_ACCESS_BUTTON, DISABLE_ACCESS_TEXT);

        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Отключить доступ' - " + headerText);
    }

    @Step("Клик по кнопке 'Деактивировать доступ'")
    public static void clickDeactivateAccessButton() {
        clickButtonWithPageLoad(DEACTIVE_ACCESS_BUTTON, DEACTIVATE_ACCESS_TEXT);

        // Получаем и логируем заголовок
        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Деактивировать доступ' - " + headerText);
    }

    @Step("Клик по кнопке 'деактивировать доступ'")
    public static void clickDeactivateButton() {
        clickButtonWithPageLoad(DEACTIVE_BUTTON, DEACTIVATE_TEXT);
    }

    @Step("Клик по кнопке 'Упустить свой шанс'")
    public static void clickMissChanceButton() {
        clickButtonWithPageLoad(MISS_CHANCE_BUTTON, MISS_CHANCE_TEXT);

        // Получаем и логируем заголовок
        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Упустить свой шанс' - " + headerText);
    }

    @Step("Проверка состояния тарифного плана")
    public static void verifyTariffPlanState(String expectedState) {
        step("Проверка состояния тарифного плана: " + expectedState, () -> {
            String actualHeader = getHeaderText();

            assertTrue(actualHeader.contains(expectedState),
                    String.format("Заголовок не соответствует ожидаемому состоянию. Ожидалось: '%s', Фактически: '%s'",
                            expectedState, actualHeader));

            AllureAttachments.attachScreenshot("Состояние тарифа - " + expectedState);
            AllureAttachments.attachText("Проверка состояния", "✓ Тарифный план: " + actualHeader);
        });
    }

    @Step("Тарифный план отключен")
    public static void verifyTariffPlanDisabled() {
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
        verifyTariffPlanState("отключен");
    }

    // Валидационные методы
    @Step("Проверка что кнопка 'Активна' disabled")
    public static void verifyActiveButtonIsDisabled() {
        step("Проверка disabled состояния кнопки 'Активна'", () -> {
            ACTIVE_BUTTON.shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldHave(attribute("disabled"))
                    .shouldBe(interactable)
                    .shouldHave(exactText(ACTIVE_TEXT));

            AllureAttachments.attachText("Валидация", "✅ Кнопка 'Активна' в состоянии disabled");
        });
    }

    // Flow методы для комплексных сценариев
    @Step("Полный процесс отмены Lite подписки")
    public static void completeLiteUnsubscription() {
        clickHyperlink();
        clickActiveButton();
        clickDisableAccessButton();
        clickDeactivateAccessButton();

        clickMissChanceButton();
        verifyTariffPlanDisabled();
    }

    @Step("Клик по гиперссылке")
    public static void clickHyperlink() {
        step("Клик по гиперссылке", () -> {
            HREF.shouldBe(visible, DEFAULT_TIMEOUT)
                    .shouldBe(interactable)
                    .click();

            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
        });
    }
}
