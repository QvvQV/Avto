package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import ru.tutor.page.personalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixCalculation {

    // Локаторы
    private final SelenideElement MATRIX_CALCULATION = $$("p").find(exactText("Расчет матрицы"));
    private final SelenideElement MATRIX_FORECAST_FOR_DAY_BUTTON = $$("button").find(exactText("Прогноз по матрице на день"));
    private final SelenideElement COMPATIBILITY_MATRIX_BUTTON = $$("button").find(exactText("Матрица совместимости"));
    private final SelenideElement CROSS_BUTTON = $("._close_1l2kq_40");
    private final SelenideElement TAG_H2 = $("h2");
    private final SelenideElement CALCULATE_BUTTON = $$("button").find(exactText("Рассчитать"));
    private final SelenideElement OPEN_ALL_BUTTON = $$("button").find(exactText("Открыть полностью"));
    private final SelenideElement COLLAPSE_BUTTON = $$("button").find(exactText("Свернуть"));


    @Step("Кликаем по Расчет матрицы")
    public void clickFromMatrixCalculation() {
        MATRIX_CALCULATION.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
    }

    @Step("Открытие модальных окон в дашборде")
    public void openModalWindowInTheDashboard() {
        step("Скролл до конца страницы", () -> {
            OPEN_ALL_BUTTON.scrollTo()
                    .shouldBe(visible, Duration.ofSeconds(3));
            AllureAttachments.attachScreenshot("Страница после скролла");
        });
        step("Кликаем по кнопке", new MatrixCalculation()::personaleCalendar
        );
        step("Открываем скрытое меню", () -> {
            openSecretMenu();
            OPEN_ALL_BUTTON.sendKeys(Keys.HOME);
            WaitHelper.waitForAnimationComplete();
        });
        step("Переходим на главную страницу",
                () -> new Home().clickMainlink()
        );
    }

    @Step("Закрытие модального окна")
    private void closeModalWindow() {
        CROSS_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                .shouldBe(clickable)
                .click();
    }

    @Step("Персональный календарь")
    private void personaleCalendar() {
        Map<SelenideElement, String> categoryButtons = Map.of(
                MATRIX_FORECAST_FOR_DAY_BUTTON, "Прогноз по матрице на день",
                COMPATIBILITY_MATRIX_BUTTON, "Матрица совместимости",
                CALCULATE_BUTTON, "Рассчитать"
        );

        for (Map.Entry<SelenideElement, String> entry : categoryButtons.entrySet()) {
            testCategory(entry.getKey(), entry.getValue());
            step("Закрываем окно", () -> {
                closeModalWindow();
                WaitHelper.waitForAnimationComplete();
            });


        }
    }

    private void testCategory(SelenideElement button, String name) {
        step("Кнопка: " + name, () -> {
            button.click();

            WaitHelper.waitForAnimationComplete();

            AllureAttachments.attachScreenshot("Кнопка " + name + " активна");
        });
    }

    private void verifyModalText(String expectedText) {
        step("Проверка текста: '" + expectedText + "'", () -> {
            String actualText = TAG_H2.shouldBe(visible, Duration.ofSeconds(3))
                    .getText()
                    .trim();

            assertEquals(expectedText, actualText,
                    String.format("Текст не совпадает. Ожидалось: '%s', Фактически: '%s'",
                            expectedText, actualText));

            AllureAttachments.attachText("Проверка текста",
                    "✓ Текст корректен: " + actualText);
        });
    }

    @Step("открытие скрытого меню")
    private void openSecretMenu() {
        OPEN_ALL_BUTTON.click();
        WaitHelper.waitForAnimationComplete();
        COLLAPSE_BUTTON.scrollTo()
                .shouldBe(visible, Duration.ofSeconds(3))
                .shouldBe(clickable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Исходное состояние");
    }
}
