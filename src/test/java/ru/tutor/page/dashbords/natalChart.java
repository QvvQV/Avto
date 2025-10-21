package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.page.PersonalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class natalChart {

    // Локаторы
    private static final SelenideElement NATAL_CHAR = $$("p").find(exactText("Натальная карта"));
    private static final SelenideElement PLANS = $$("button").find(exactText("Планы"));
    private static final SelenideElement LOVE = $$("button").find(exactText("Любовь"));
    private static final SelenideElement FINANCES = $$("button").find(exactText("Финансы"));

    // Локаторы страницы Info
    private static final SelenideElement REDIRECT_NATALCHART_INFO = $("a._natalChartButton_11m8o_19[href$='natal-chart-info']");
    private static final SelenideElement REDIRECT_NATALCHART_INFO_DOWN = $$("a").find(exactText("Подробнее о натальной карте"));
    private static final SelenideElement REDIRECT_ASTROLOGY_DASHBOARD = $("a[href*='dashboard']");
    private static final SelenideElement SELECT_A_PLANET = $$("p").find(exactText("Выберите планету"));


    @Step("Кликаем по Натальная карта")
    public static void clickFromNatalChar() {
        NATAL_CHAR.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
    }

    @Step("Персональный календарь")
    public static void personaleCalendar() {
        Map<SelenideElement, String> categoryButtons = Map.of(
                PLANS, "Планы",
                LOVE, "Любовь",
                FINANCES, "Финансы"
        );

        for (Map.Entry<SelenideElement, String> entry : categoryButtons.entrySet()) {
            testCategory(entry.getKey(), entry.getValue());
        }
    }

    private static void testCategory(SelenideElement button, String name) {
        step("Категория: " + name, () -> {
            button.click();
            button.shouldHave(cssClass("active"));

            AllureAttachments.attachScreenshot("Категория " + name + " активна");
        });
    }

    @Step("Проверяем редиеркт на страницу `Info`")
    public static void redirectToTheInfoPage() {

        step("1. Редирект через стрелку", () -> {
            REDIRECT_NATALCHART_INFO.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .click();
            WaitHelper.waitForPageLoad();
            AllureAttachments.attachScreenshot("Старница 'Info'");
        });
        step("2. Проверка точного текста", () -> {
            String expectedText = "Выберите планету";
        String actualText = SELECT_A_PLANET.getText().trim();
            SELECT_A_PLANET.shouldBe(visible, Duration.ofSeconds(3));
            assertEquals(expectedText, actualText,
                    String.format("Текст не совпадает. Ожидалось: '%s', Фактически: '%s'",
                            expectedText, actualText));
            AllureAttachments.attachText("Проверка текста", "✓ Текст корректен: " + actualText);
        });
        step("3. Возвращаемся на страницу Астрологии", () -> {
            REDIRECT_ASTROLOGY_DASHBOARD.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForPageLoad();
            AllureAttachments.attachScreenshot("Результат после клика");
        });
        step("4. Редирект через кнопку 'Подробнее о натальной карте'", () -> {
            REDIRECT_NATALCHART_INFO_DOWN.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .click();
            WaitHelper.waitForPageLoad();
            AllureAttachments.attachScreenshot("Старница 'Info'");
        });
        step("5. Возвращаемся на страницу Астрологии", () -> {
            REDIRECT_ASTROLOGY_DASHBOARD.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForPageLoad();
        });
        step("6. Редирект на главную страницу", () -> {
            Home.clickMainlink();
        });
    }
}
