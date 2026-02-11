package ru.tutor.page.modals;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

/**
 * Класс для работы с продуктом. Добавить/Удалить/Перейти по внутренней ссылке продукта
 */

public class product {

    // Названия кнопок
    private static final String BEGIN_TEXT = "Начать";

    //Локаторы
    private static final SelenideElement BEGIN_BUTTON = $$("button").find(exactText(BEGIN_TEXT));
    private static final SelenideElement WEEK_BUTTON = $("a[href*='user/product']");
    private static final SelenideElement OPEN_WEEK_TWO = $x("//button[contains(@class, '_week_') and .//p[contains(text(), 'Неделя 2')]]");

    @Step("Переход в продукт")
    public static void beginProduct() {
        step("Кликаем по кнопке 'Начать'", () -> universalMethod.safeClick(BEGIN_BUTTON, BEGIN_TEXT)
        );
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
        step("Переход по кнопке Неделя 1, Урок 1", () -> universalMethod.safeClick(WEEK_BUTTON, "'Неделя 1,Урок 1'")
        );
        step("Ткрываем вторую неделю курса", () ->universalMethod.safeClick(OPEN_WEEK_TWO, "Неделя 2")
        );

    }
}
