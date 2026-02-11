package ru.tutor.Unsubscribe;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PremiumUnsubscribePage {

    // Константы для тестов
    private static final String ACTIVE_TEXT = "активна";
    private static final String CHANGE_TEXT = "Изменить";
    private static final String CANCEL_TEXT = "Отменить";
    private static final String REVOKE_FULL_ACCESS_TEXT = "Отменить полный доступ";
    private static final String DISABLE_ACCESS_TEXT = "Отключить доступ";
    private static final String DEACTIVATE_ACCESS_TEXT = "Деактивировать доступ";
    private static final String DEACTIVATE_TEXT = "Деактивировать";
    private static final String MISS_CHANCE_TEXT = "Упустить свой шанс";
    private static final String RESTORE_ACCESS_TEXT = "Возобновить доступ";


    // Локаторы Premium подписки
    private static final SelenideElement ACTIVE_BUTTON = $$("button").find(exactText(ACTIVE_TEXT));
    private static final SelenideElement CHANGE_BUTTON = $$("button").find(exactText(CHANGE_TEXT));
    private static final SelenideElement CANCEL_BUTTON = $$("button").find(exactText(CANCEL_TEXT));
    private static final SelenideElement REVOKE_FULL_ACCESS_BUTTON = $$("button").find(exactText(REVOKE_FULL_ACCESS_TEXT));
    private static final SelenideElement H3_TEXT = $("h3");
    private static final SelenideElement RESTORE_ACCESS_BUTTON = $$("button").find(exactText(RESTORE_ACCESS_TEXT));

    // Универсальные методы
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

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

    // Premium подписка методы
    @Step("Клик по кнопке 'Активна'")
    public static void clickActiveButton() {
        universalMethod.clickButton(ACTIVE_BUTTON, ACTIVE_TEXT);

        // Дополнительная проверка для disabled состояния
//        step("Проверка состояния кнопки 'Активна'", () -> {
//            ACTIVE_BUTTON.shouldHave(attribute("disabled"));
//            AllureAttachments.attachText("Состояние кнопки", "Кнопка 'Активна' теперь disabled");
//        });
    }

    @Step("Клик по кнопке 'Изменить'")
    public static void clickChangeButton() {
        universalMethod.clickButtonWithPageLoad(CHANGE_BUTTON, CHANGE_TEXT);

        // Получаем и логируем заголовок
        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Изменить' - " + headerText);
    }

    @Step("Клик по кнопке 'Отменить'")
    public static void clickCancelButton() {
        universalMethod.clickButtonWithPageLoad(CANCEL_BUTTON, CANCEL_TEXT);

        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Отменить' - " + headerText);
    }

    @Step("Клик по кнопке 'Отменить полный доступ'")
    public static void clickRevokeFullAccessButton() {
        universalMethod.clickButtonWithPageLoad(REVOKE_FULL_ACCESS_BUTTON, REVOKE_FULL_ACCESS_TEXT);
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
        RESTORE_ACCESS_BUTTON.shouldBe(visible);
        verifyTariffPlanState("отключен");
    }

    @Step("Тарифный план активен")
    public static void verifyTariffPlanActive() {
        verifyTariffPlanState("активен");
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

    @Step("Проверка что кнопка 'Изменить' доступна")
    public static void verifyChangeButtonIsEnabled() {
        universalMethod.verifyButtonIsEnabled(CHANGE_BUTTON, CHANGE_TEXT);
    }

    @Step("Проверка что кнопка 'Отменить' доступна")
    public static void verifyCancelButtonIsEnabled() {
        universalMethod.verifyButtonIsEnabled(CANCEL_BUTTON, CANCEL_TEXT);
    }

    // Flow методы для комплексных сценариев
    public static void completePremiumUnsubscription() {
        clickActiveButton();
        verifyActiveButtonIsDisabled();
        clickChangeButton();
        clickCancelButton();
        clickRevokeFullAccessButton();
        verifyTariffPlanDisabled();
    }

    // Методы для проверки видимости элементов
    @Step("Проверка видимости всех элементов Premium подписки")
    public static void verifyPremiumElementsVisible() {
        step("Проверка видимости элементов Premium подписки", () -> {
            ACTIVE_BUTTON.shouldBe(visible, DEFAULT_TIMEOUT);
            CHANGE_BUTTON.shouldBe(visible, DEFAULT_TIMEOUT);
            CANCEL_BUTTON.shouldBe(visible, DEFAULT_TIMEOUT);
            REVOKE_FULL_ACCESS_BUTTON.shouldBe(visible, DEFAULT_TIMEOUT);

            AllureAttachments.attachText("Проверка элементов",
                    "✓ Все элементы Premium подписки видны");
            AllureAttachments.attachScreenshot("Элементы Premium подписки");
        });
    }
}