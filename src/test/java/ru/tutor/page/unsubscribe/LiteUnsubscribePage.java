package ru.tutor.page.unsubscribe;

import com.codeborne.selenide.SelenideElement;
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

/**
 * Описание кнопок и логики отписки и переподписки Премиума - Лайт
 */

public class LiteUnsubscribePage {

    // Название кнопок
    private static final String ACTIVE_TEXT = "активна";
    private static final String DISABLE_ACCESS_TEXT = "Отключить доступ";
    private static final String DEACTIVATE_ACCESS_TEXT = "Деактивировать доступ";
    private static final String DEACTIVATE_TEXT = "Деактивировать";
    private static final String MISS_CHANCE_TEXT = "Упустить свой шанс";
    private static final String SUBSCRIBE_TEXT = "Оформить подписку";

    // Локаторы Premium подписки
    private static final SelenideElement ACTIVE_BUTTON = $$("button").find(exactText(ACTIVE_TEXT));
    private static final SelenideElement SUBSCRIBE_BUTTON = $$("button").find(exactText(SUBSCRIBE_TEXT));
    private static final SelenideElement H3_TEXT = $("h3");

    // Локаторы Lite подписки
    private static final SelenideElement HREF = $("a[href*='payments']");
    private static final SelenideElement DISABLE_ACCESS_BUTTON = $$("button").find(exactText(DISABLE_ACCESS_TEXT));
    private static final SelenideElement DEACTIVE_ACCESS_BUTTON = $$("button").find(exactText(DEACTIVATE_ACCESS_TEXT));
    private static final SelenideElement DEACTIVE_BUTTON = $$("button").find(exactText(DEACTIVATE_TEXT));
    private static final SelenideElement MISS_CHANCE_BUTTON = $$("button").find(exactText(MISS_CHANCE_TEXT));


    // Универсальные методы
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    // Состояние подписки
    public enum TariffState {
        DISABLED("отключен"),
        ACTIVE("активен");

        private final String displayName;
        TariffState(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Получение и логирование текста заголовка
     */
    @Step("Получение текста заголовка")
    private static String getHeaderText() {
        return step("Заголовок страницы", () -> {
            String headerText = universalMethod.getElementText(H3_TEXT, "Заголовок старницы");
            AllureAttachments.attachText("Заголовок страницы", headerText);
            return headerText;
        });
    }

    @Step("Клик по кнопке 'Отключить доступ'")
    private static void clickDisableAccessButton() {
        universalMethod.clickButtonWithPageLoad(DISABLE_ACCESS_BUTTON, DISABLE_ACCESS_TEXT);

        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Отключить доступ' - " + headerText);
    }

    @Step("Клик по кнопке 'Деактивировать доступ'")
    private static void clickDeactivateAccessButton() {
        universalMethod.clickButtonWithPageLoad(DEACTIVE_ACCESS_BUTTON, DEACTIVATE_ACCESS_TEXT);

        // Получаем и логируем заголовок
        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Деактивировать доступ' - " + headerText);
    }

    @Step("Клик по кнопке 'деактивировать доступ'")
    public static void clickDeactivateButton() {
       universalMethod.clickButtonWithPageLoad(DEACTIVE_BUTTON, DEACTIVATE_TEXT);
    }

    @Step("Клик по кнопке 'Упустить свой шанс'")
    public static void clickMissChanceButton() {
        universalMethod.clickButtonWithPageLoad(MISS_CHANCE_BUTTON, MISS_CHANCE_TEXT);

        // Получаем и логируем заголовок
        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Упустить свой шанс' - " + headerText);
    }

    @Step("Проверка состояния тарифного плана: {state.displayName}")
    private static void verifyTariffPlanState(TariffState state) {
        step("Проверка состояния тарифного плана: " + state.getDisplayName(), () -> {
            String actualHeader = getHeaderText();

            assertTrue(actualHeader.contains(state.getDisplayName()),
                    "Ожидалось состоняие: " + state.getDisplayName() + " но было: " + actualHeader
                            );

            AllureAttachments.attachScreenshot("Состояние тарифа - " + state.getDisplayName());
            AllureAttachments.attachText("Проверка состояния", "✓ Тарифный план: " + actualHeader);
        });
    }

    @Step("Тарифный план отключен")
    private static void verifyTariffPlanDisabled() {
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
        verifyTariffPlanState(TariffState.DISABLED);
    }

    @Step("Тарифный план активен")
    private static void verifyTariffPlanActive() {
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
        verifyTariffPlanState(TariffState.ACTIVE);
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
        universalMethod.clickButton(ACTIVE_BUTTON,ACTIVE_TEXT);
        universalMethod.clickButton(DISABLE_ACCESS_BUTTON,DISABLE_ACCESS_TEXT);
        universalMethod.clickButton(DEACTIVE_ACCESS_BUTTON,DEACTIVATE_ACCESS_TEXT);
        universalMethod.clickButton(MISS_CHANCE_BUTTON,MISS_CHANCE_TEXT);

        verifyTariffPlanDisabled();
    }

    @Step("Клик по гиперссылке")
    private static void clickHyperlink() {
        step("Клик по гиперссылке", () -> {
            universalMethod.safeClick(HREF, "Клик по гиперссылке");

            WaitHelper.waitForPageLoad();
        });
    }

    @Step("Клик по кнопке Оформить подписку")
    public static void clickSubscribe() {
        universalMethod.clickButton(SUBSCRIBE_BUTTON, SUBSCRIBE_TEXT);
    }
}