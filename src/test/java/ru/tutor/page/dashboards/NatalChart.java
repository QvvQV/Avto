package ru.tutor.page.dashboards;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.page.personalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NatalChart {

    // Текст кнопок
    private static final String NATAL_CHAR_TEXT = "Натальная карта";
    private static final String PLANS_TEXT = "Планы";
    private static final String LOVE_TEXT = "Любовь";
    private static final String FINANCES_TEXT = "Финансы";
    private static final String NATALCHART_INFO_TEXT = "Подробнее о натальной карте";
    private static final String PLANET_TEXT = "Выберите планету";

    // Локаторы
    private final SelenideElement NATAL_CHAR = $$("p").find(exactText(NATAL_CHAR_TEXT));
    private final SelenideElement PLANS = $$("button").find(exactText(PLANS_TEXT));
    private final SelenideElement LOVE = $$("button").find(exactText(LOVE_TEXT));
    private final SelenideElement FINANCES = $$("button").find(exactText(FINANCES_TEXT));

    // Локаторы страницы Info
    private final SelenideElement REDIRECT_NATALCHART_INFO = $("a._natalChartButton_11m8o_19[href$='natal-chart-info']");
    private final SelenideElement REDIRECT_NATALCHART_INFO_DOWN = $$("a").find(exactText(NATALCHART_INFO_TEXT));
    private final SelenideElement REDIRECT_ASTROLOGY_DASHBOARD = $("a[href*='dashboard']");
    private final SelenideElement SELECT_A_PLANET = $$("p").find(exactText(PLANET_TEXT));


    @Step("Кликаем по Натальная карта")
    public void clickFromNatalChar() {
        UniversalMethod.safeClick(NATAL_CHAR, NATAL_CHAR_TEXT);
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
    }

    @Step("Персональный календарь")
    public void personaleCalendar() {
        Map<SelenideElement, String> categoryButtons = Map.of(
                PLANS, "Планы",
                LOVE, "Любовь",
                FINANCES, "Финансы"
        );

        for (Map.Entry<SelenideElement, String> entry : categoryButtons.entrySet()) {
            testCategory(entry.getKey(), entry.getValue());
        }
    }

    private void testCategory(SelenideElement button, String name) {
        step("Категория: " + name, () -> {
            button.click();
            button.shouldHave(cssClass("active"));

            AllureAttachments.attachScreenshot("Категория " + name + " активна");
        });
    }

    @Step("Проверяем редиеркт на страницу `Info`")
    public void redirectToTheInfoPage() {

        step("1. Редирект через стрелку", () -> {
            UniversalMethod.safeClick(REDIRECT_NATALCHART_INFO, NATAL_CHAR_TEXT);
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Старница 'Info'");
        });
        step("2. Проверка точного текста", () -> {
            String expectedText = PLANET_TEXT;
        String actualText = SELECT_A_PLANET.getText().trim();
            SELECT_A_PLANET.shouldBe(visible, Duration.ofSeconds(3));
            assertEquals(expectedText, actualText,
                    String.format("Текст не совпадает. Ожидалось: '%s', Фактически: '%s'",
                            expectedText, actualText));
            AllureAttachments.attachText("Проверка текста", "✓ Текст корректен: " + actualText);
        });
        step("3. Возвращаемся на страницу Астрологии", () -> {
            UniversalMethod.safeClick(REDIRECT_ASTROLOGY_DASHBOARD, "Переход в астрологический даш");
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Результат после клика");
        });
        step("4. Редирект через кнопку 'Подробнее о натальной карте'", () -> {
            UniversalMethod.safeClick(REDIRECT_NATALCHART_INFO_DOWN, NATALCHART_INFO_TEXT );
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Старница 'Info'");
        });
        step("5. Возвращаемся на страницу Астрологии", () -> {
            UniversalMethod.safeClick(REDIRECT_ASTROLOGY_DASHBOARD, "Переход к астрологическому даш");
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
        });
        step("6. Редирект на главную страницу",
                () -> new Home().clickMainlink()
        );
    }
}
