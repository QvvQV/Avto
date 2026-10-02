package ru.tutor.page.modals;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

/**
 * Класс для работы с продуктом. Добавить/Удалить/Перейти по внутренней ссылке продукта
 */

public class Product {

    // Названия кнопок
    private static final String BEGIN_TEXT = "Начать";
    private static final String CONTINUE_TEXT = "Продолжить";
    private static final String WATCH_TEXT = "Смотреть";
    private static final String CASHBACK_TEXT = "Забрать бонус";

    //Локаторы
    private final SelenideElement BEGIN_BUTTON = $$("button").find(exactText(BEGIN_TEXT));
    private final SelenideElement CONTINUE_BUTTON = $$("button").find(exactText(CONTINUE_TEXT));
    private final SelenideElement WATCH_BUTTON = $$("button").find(exactText(WATCH_TEXT));
    private final SelenideElement WEEK_BUTTON = $("a[href*='user/product']");
    private final SelenideElement OPEN_WEEK_TWO = $x("//button[contains(@class, '_week_') and .//p[contains(text(), 'Неделя 2')]]");
    private final SelenideElement PICK_UP_BONUS_BUTTON = $$("button").find(exactText(CASHBACK_TEXT));

    @Step("Переход в продукт")
    public void beginProduct() {
        step("Редирект на продукт Figma", () -> open(TestConfig.getCurrentClientDomain() + "/user/search?query=Figma"));
        step("Кликаем по кнопке 'Начать'", () -> {
            WaitHelper.waitForPageLoad();
            sleep(4000);
            if (BEGIN_BUTTON.isDisplayed() && BEGIN_BUTTON.isEnabled()) {
                UniversalMethod.safeClick(BEGIN_BUTTON, BEGIN_TEXT);
            }
            else {
                UniversalMethod.safeClick(WATCH_BUTTON, WATCH_TEXT);
            }
                }
        );
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
        step("Переход по кнопке Неделя 1, Урок 1", () -> UniversalMethod.safeClick(WEEK_BUTTON, "'Неделя 1,Урок 1'")
        );
        step("Открываем вторую неделю курса", () -> UniversalMethod.safeClick(OPEN_WEEK_TWO, "Неделя 2")
        );
    }

    @Step("Переход в продукт Prod")
    public void beginProductProd() {
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
        step("Переход по кнопке Неделя 1, Урок 1", () -> UniversalMethod.safeClick(WEEK_BUTTON, "'Неделя 1,Урок 1'")
        );
        step("Открываем вторую неделю курса", () -> UniversalMethod.safeClick(OPEN_WEEK_TWO, "Неделя 2")
        );
    }

    @Step("Забрать Бонус - Кешбэк")
    public void pickUpBonus() {
        step("Кликаем по кнопке забрать бонус - Кешбэк", () -> {
                WaitHelper.waitForAnimationComplete();
                WaitHelper.waitForPageLoad();
                UniversalMethod.safeClick(PICK_UP_BONUS_BUTTON, CASHBACK_TEXT);
        });
    }
}
