package ru.tutor.page.dashbords;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.page.personalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Arrays;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class MemoryTrainer {

    // Локаторы
        private final SelenideElement MEMORY_TRAINER = $$("p").find(exactText("Тренажер памяти"));

        //Локатор внутри приложения
    private final ElementsCollection START_BUTTONS = $$("._button_1vvql_1");
    private final SelenideElement NAME_TRAINER = $("._title_9skre_66");

    private static final SelenideElement BACK_BUTTON = $("a[href*='dashboard']");

    @Step("Кликаем по тренажёру памяти")
    public void clickFromMemoryTrainer() {
        step("Кликаем по тренажёру памяти", ()->{
             MEMORY_TRAINER.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
        });
    }

        public void gameNavigationWithCollection(){
        START_BUTTONS.shouldHave(CollectionCondition.size(6), Duration.ofSeconds(7));
        for (int i = 0; i < START_BUTTONS.size(); i++){
            startButtonByIndex(i);
        }
        step("Переход на главную страницу",
                () -> new Home().clickMainlink()
        );
    }

        private void startButtonByIndex(int index) {
        String[] trainerNames = {"Плитка", "Стена", "Бусина", "Потеряшка", "Код", "Следопыт",};
        String trainerName = trainerNames[index];

        step("Тестирование кнопки: " + trainerName, ()->{
            SelenideElement startButton = START_BUTTONS.get(index);

            step("Проверка видимости - " + Arrays.toString(trainerNames), ()->{
                startButton.shouldBe(visible, Duration.ofSeconds(5))
                        .shouldBe(interactable, Duration.ofSeconds(3));
            });

            step("Проверка редиректа", ()->{
                String originalUrl = WebDriverRunner.url();
                startButton.click();

                WaitHelper.waitForUrlContains(originalUrl, Duration.ofSeconds(3));
                WaitHelper.waitForPageLoad();

                NAME_TRAINER.shouldBe(visible, Duration.ofSeconds(5));
                AllureAttachments.attachScreenshot("Игра " + trainerName);
            });

            step("Возвращаемся на страницу с 'Играми'", ()->{
                BACK_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                        .click();

                WaitHelper.waitForPageLoad();

                START_BUTTONS.shouldHave(CollectionCondition.size(6));
                AllureAttachments.attachText("Тест завершен",
                    "✅ " + trainerName + " - навигация работает корректно");
            });
        });
    }
}
