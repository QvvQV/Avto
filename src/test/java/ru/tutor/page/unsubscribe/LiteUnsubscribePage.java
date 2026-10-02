package ru.tutor.page.unsubscribe;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

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
    private final SelenideElement ACTIVE_BUTTON = $$("button").find(exactText(ACTIVE_TEXT));
    private final SelenideElement SUBSCRIBE_BUTTON = $$("button").find(exactText(SUBSCRIBE_TEXT));
    private final SelenideElement H3_TEXT = $("h3");

    // Локаторы Lite подписки
    private final SelenideElement HREF = $("a[href*='payments']");
    private final SelenideElement DISABLE_ACCESS_BUTTON = $$("button").find(exactText(DISABLE_ACCESS_TEXT));
    private final SelenideElement DEACTIVE_ACCESS_BUTTON = $$("button").find(exactText(DEACTIVATE_ACCESS_TEXT));
    private final SelenideElement DEACTIVE_BUTTON = $$("button").find(exactText(DEACTIVATE_TEXT));
    private final SelenideElement MISS_CHANCE_BUTTON = $$("button").find(exactText(MISS_CHANCE_TEXT));


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
    private String getHeaderText() {
        return step("Заголовок страницы", () -> {
            String headerText = UniversalMethod.getElementText(H3_TEXT, "Заголовок страницы");
            AllureAttachments.attachText("Заголовок страницы", headerText);
            return headerText;
        });
    }

    @Step("Клик по кнопке 'Отключить доступ'")
    private void clickDisableAccessButton() {
        UniversalMethod.clickButtonWithPageLoad(DISABLE_ACCESS_BUTTON, DISABLE_ACCESS_TEXT);

        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Отключить доступ' - " + headerText);
    }

    @Step("Клик по кнопке 'Деактивировать доступ'")
    private void clickDeactivateAccessButton() {
        UniversalMethod.clickButtonWithPageLoad(DEACTIVE_ACCESS_BUTTON, DEACTIVATE_ACCESS_TEXT);

        // Получаем и логируем заголовок
        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Деактивировать доступ' - " + headerText);
    }

    @Step("Клик по кнопке 'деактивировать доступ'")
    public void clickDeactivateButton() {
       UniversalMethod.clickButtonWithPageLoad(DEACTIVE_BUTTON, DEACTIVATE_TEXT);
    }

    @Step("Клик по кнопке 'Упустить свой шанс'")
    public void clickMissChanceButton() {
        UniversalMethod.clickButtonWithPageLoad(MISS_CHANCE_BUTTON, MISS_CHANCE_TEXT);

        // Получаем и логируем заголовок
        String headerText = getHeaderText();
        AllureAttachments.attachScreenshot("После клика на 'Упустить свой шанс' - " + headerText);
    }

    @Step("Проверка состояния тарифного плана: {state.displayName}")
    private void verifyTariffPlanState(TariffState state) {
        step("Проверка состояния тарифного плана: " + state.getDisplayName(), () -> {
            String actualHeader = getHeaderText();

            assertTrue(actualHeader.contains(state.getDisplayName()),
                    "Ожидалось состояние: " + state.getDisplayName() + " но было: " + actualHeader
                            );

            AllureAttachments.attachScreenshot("Состояние тарифа - " + state.getDisplayName());
            AllureAttachments.attachText("Проверка состояния", "✓ Тарифный план: " + actualHeader);
        });
    }

    @Step("Тарифный план отключен")
    private void verifyTariffPlanDisabled() {
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
        verifyTariffPlanState(TariffState.DISABLED);
    }

    @Step("Тарифный план активен")
    private void verifyTariffPlanActive() {
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
        verifyTariffPlanState(TariffState.ACTIVE);
    }

    // Валидационные методы
    @Step("Проверка что кнопка 'Активна' disabled")
    public void verifyActiveButtonIsDisabled() {
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
    public void completeLiteUnsubscription() {
        clickHyperlink();
        UniversalMethod.clickButton(ACTIVE_BUTTON,ACTIVE_TEXT);
        UniversalMethod.clickButton(DISABLE_ACCESS_BUTTON,DISABLE_ACCESS_TEXT);
        UniversalMethod.clickButton(DEACTIVE_ACCESS_BUTTON,DEACTIVATE_ACCESS_TEXT);
        UniversalMethod.clickButton(MISS_CHANCE_BUTTON,MISS_CHANCE_TEXT);

        verifyTariffPlanDisabled();
    }

    @Step("Клик по гиперссылке")
    private void clickHyperlink() {
        step("Клик по гиперссылке", () -> {
            UniversalMethod.safeClick(HREF, "Клик по гиперссылке");

            WaitHelper.waitForPageLoad();
        });
    }

    @Step("Клик по кнопке Оформить подписку")
    public void clickSubscribe() {
        UniversalMethod.clickButton(SUBSCRIBE_BUTTON, SUBSCRIBE_TEXT);
    }
}