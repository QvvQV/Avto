package ru.tutor.page.dashbords;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.page.PersonalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class nutritionTracker {

    // Локаторы
    private static final SelenideElement NUTRITION_TRACKER = $$("p").find(exactText("Трекер питания"));
    private static final SelenideElement START_BUTTON = $$("button").find(exactText("Начать программу"));
    private static final SelenideElement START_A_FASTING_PERIOD_BUTTON = $$("button").find(exactText("Начать период голодания"));
    private static final SelenideElement START_THE_EATING_PERIOD_BUTTON = $$("button").find(exactText("Начать период приема пищи"));
    private static final SelenideElement ABORT_THE_PROGRAM_BUTTON = $$("button").find(exactText("Прервать программу"));
    private static final SelenideElement INTERRUPT_BUTTON = $$("button").find(exactText("Да, прервать"));

    // Локаторы для проверки страниц
    private static final SelenideElement MY_RECIPES = $$("h2").find(exactText("Мои рецепты"));
    private static final SelenideElement RESULTS_BUTTON = $$("a").find(exactText("Результаты"));

    // Локаторы приёма пищи
    private static final ElementsCollection MEAL_BUTTONS = $$("a._addButton_1y9xd_102");


    @Step("Кликаем по трекеру питания")
    public static void clickFromNutritionTracker() {
        NUTRITION_TRACKER.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
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
        step("Кликаем по кнопке 'Начать период голодания'", () -> {

            START_A_FASTING_PERIOD_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .click();
        });
    }

    @Step("Кликаем по кнопке 'Начать программу'")
    private static void clickStartProgram() {
        step("Кликаем по кнопке 'Начать программу'", () ->
        {
            START_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
    }

    @Step("Кликаем по кнопке 'Прервать программу'")
    private static void clickAbortProgramm() {
        step("Кликаем по кнопке 'Прервать программу'", () ->
        {
            ABORT_THE_PROGRAM_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
        step("Прерываем программу", () -> {
            INTERRUPT_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .click();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachText("✅ ", "Программа прервана");
        });
    }

    @Step("Кликаем по кнопке 'Начать период приёма пищи'")
    private static void clickStartEatingPeriod() {
        step("Кликаем по кнопке 'Начать период приёма пищи'", () -> {

            START_THE_EATING_PERIOD_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .click();
        });
    }

    public static void mealNavigationWithCollection(){
        MEAL_BUTTONS.shouldHave(CollectionCondition.size(4), Duration.ofSeconds(7));
        for (int i = 0; i < MEAL_BUTTONS.size(); i++){
            mealButtonByIndex(i);
        }
        step("Переход на главную страницу", ()->{
            Home.clickMainlink();
        });
    }

        private static void mealButtonByIndex(int index) {
        String[] mealNames = {"BREAKFAST", "DINNER", "LAUNCH", "SUPPER", };
        String mealName = mealNames[index];

        step("Тестирование кнопки: " + mealName, ()->{
            SelenideElement mealButton = MEAL_BUTTONS.get(index);

            step("Проверка видимости - " + mealName, ()->{
                mealButton.shouldBe(visible, Duration.ofSeconds(5))
                        .shouldBe(interactable, Duration.ofSeconds(3));
            });

            step("Проверка редиректа", ()->{
                String originalUrl = WebDriverRunner.url();
                mealButton.click();

                WaitHelper.waitForUrlContains(originalUrl, Duration.ofSeconds(3));
                WaitHelper.waitForPageLoad();

                MY_RECIPES.shouldBe(visible, Duration.ofSeconds(5));
                AllureAttachments.attachScreenshot("После " + mealName);
            });

            step("Возвращаемся на страницу 'Результаты'", ()->{
                RESULTS_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                        .click();

                WaitHelper.waitForPageLoad();

                MEAL_BUTTONS.shouldHave(CollectionCondition.size(4));
                AllureAttachments.attachText("Тест завершен",
                    "✅ " + mealName + " - навигация работает корректно");
            });
        });
    }
}
