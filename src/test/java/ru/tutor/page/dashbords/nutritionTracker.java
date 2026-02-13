package ru.tutor.page.dashbords;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.page.personalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class nutritionTracker {

    // Константа
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    // Текст кнопок
    private static final String NUTRITION_TEXT = "Трекер питания";
    private static final String START_TEXT = "Начать программу";
    private static final String START_A_FASTING_PERIOD_TEXT = "Начать период голодания";
    private static final String START_THE_EATING_PERIOD_TEXT = "Начать период приема пищи";
    private static final String ABORT_THE_PROGRAM_TEXT = "Прервать программу";
    private static final String INTERRUPT_TEXT = "Да, прервать";
    private static final String MY_RECIPES_TEXT = "Мои рецепты";
    private static final String RESULTS_TEXT = "Результаты";

    // Локаторы кнопок
    private static final SelenideElement NUTRITION_TRACKER = $$("p").find(exactText(NUTRITION_TEXT));
    private static final SelenideElement START_BUTTON = $$("button").find(exactText(START_TEXT));
    private static final SelenideElement START_A_FASTING_PERIOD_BUTTON = $$("button").find(exactText(START_A_FASTING_PERIOD_TEXT));
    private static final SelenideElement START_THE_EATING_PERIOD_BUTTON = $$("button").find(exactText(START_THE_EATING_PERIOD_TEXT));
    private static final SelenideElement ABORT_THE_PROGRAM_BUTTON = $$("button").find(exactText(ABORT_THE_PROGRAM_TEXT));
    private static final SelenideElement INTERRUPT_BUTTON = $$("button").find(exactText(INTERRUPT_TEXT));

    // Локаторы для проверки страниц
    private static final SelenideElement MY_RECIPES = $$("h2").find(exactText(MY_RECIPES_TEXT));
    private static final SelenideElement RESULTS_BUTTON = $$("a").find(exactText(RESULTS_TEXT));

    // Локаторы приёма пищи
    private static final ElementsCollection MEAL_BUTTONS = $$("a._addButton_1y9xd_102");


    @Step("Кликаем по трекеру питания")
    public static void clickFromNutritionTracker() {
        universalMethod.safeClick(NUTRITION_TRACKER, NUTRITION_TEXT);
        AllureAttachments.attachScreenshot("Дашборд питания");
    }

    @Step("Проверяем модальное окно 'Начать программу'")
    public static void startProgramm() {
        clickStartProgram();
        clickStartFastingPeriod();
        clickAbortProgramm();
        clickStartEatingPeriod();
        clickAbortProgramm();
    }

    @Step("Кликаем по кнопке 'Начать период голодания'")
    private static void clickStartFastingPeriod() {
        step("Кликаем по кнопке 'Начать период голодания'", () ->
                universalMethod.safeClick(START_A_FASTING_PERIOD_BUTTON, START_TEXT)
        );
    }

    @Step("Кликаем по кнопке 'Начать программу'")
    private static void clickStartProgram() {
        step("Кликаем по кнопке 'Начать программу'", () ->
        universalMethod.safeClick(START_BUTTON, START_TEXT)
        );
    }

    @Step("Кликаем по кнопке 'Прервать программу'")
    private static void clickAbortProgramm() {
        step("Кликаем по кнопке 'Прервать программу'", () ->
        universalMethod.safeClick(ABORT_THE_PROGRAM_BUTTON, ABORT_THE_PROGRAM_TEXT)
        );
        step("Прерываем программу", () -> {
            universalMethod.safeClick(INTERRUPT_BUTTON, INTERRUPT_TEXT);
            AllureAttachments.attachText("✅ ", "Программа прервана");
        });
    }

    @Step("Кликаем по кнопке 'Начать период приёма пищи'")
    private static void clickStartEatingPeriod() {
        step("Кликаем по кнопке 'Начать период приёма пищи'", () ->
                universalMethod.safeClick(START_THE_EATING_PERIOD_BUTTON , START_THE_EATING_PERIOD_TEXT)
        );
    }

    public static void mealNavigationWithCollection(){
        MEAL_BUTTONS.shouldHave(CollectionCondition.size(4), DEFAULT_TIMEOUT);
        for (int i = 0; i < MEAL_BUTTONS.size(); i++){
            mealButtonByIndex(i);
        }
        step("Переход на главную страницу",
            Home::clickMainlink
        );
    }

        private static void mealButtonByIndex(int index) {
        String[] mealNames = {"BREAKFAST", "DINNER", "LAUNCH", "SUPPER", };
        String mealName = mealNames[index];

        step("Тестирование кнопки: " + mealName, ()->{
            SelenideElement mealButton = MEAL_BUTTONS.get(index);

            step("Проверка видимости - " + mealName, ()->{
                mealButton.shouldBe(visible, DEFAULT_TIMEOUT)
                        .shouldBe(interactable, DEFAULT_TIMEOUT);
            });

            step("Проверка редиректа", ()->{
                String originalUrl = WebDriverRunner.url();
                mealButton.click();

                WaitHelper.waitForUrlContains(originalUrl, DEFAULT_TIMEOUT);
                WaitHelper.waitForPageLoad();

                MY_RECIPES.shouldBe(visible, DEFAULT_TIMEOUT);
                AllureAttachments.attachScreenshot("После " + mealName);
            });

            step("Возвращаемся на страницу 'Результаты'", ()->{
                universalMethod.safeClick(RESULTS_BUTTON, RESULTS_TEXT);

                WaitHelper.waitForPageLoad();

                MEAL_BUTTONS.shouldHave(CollectionCondition.size(4));
                AllureAttachments.attachText("Тест завершен",
                    "✅ " + mealName + " - навигация работает корректно");
            });
        });
    }
}