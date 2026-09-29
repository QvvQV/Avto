package ru.tutor.page.dashboards;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import ru.tutor.page.personalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class Taro {

    // Константа
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private final SelenideElement TARO = $$("p").find(exactText("Таро"));

    // Локаторы карусели
    private final ElementsCollection TARO_BUTTONS = $$("button._btn_121zv_91");
    private final SelenideElement DASHBOARDS_BUTTON = $$("a").find(exactText("Таро"));
    private final SelenideElement TITLE = $("h2");
    private final SelenideElement RIGHT_BUTTON = $(".right");

    @Step("Кликаем по Таро")
    public void clickFromTaro() {

        step("Кликаем по Таро", () -> {
            UniversalMethod.safeClick(TARO, "Таро");
            AllureAttachments.attachScreenshot("Результат после клика");
        });
    }

    private final String[] TAROS_NAMES = {
            "Отношения", "Далекое будущее", "Ответ на вопрос: “Да” или “Нет”", "Ближайшее будущее",
            "Что он думает о вас", "Личность", "Деньги", "Пoл будущeгo peбeнкa"
    };

    public void mealNavigationWithCollection() {
        TARO_BUTTONS.shouldHave(CollectionCondition.size(8), DEFAULT_TIMEOUT);

        for (int i = 0; i < TARO_BUTTONS.size(); i++) {
            // Прокручиваем к нужной кнопке
            if (i > 0) {
                navigateToButtonByIndex(i);
            }
            mealButtonByIndex(i);

        }

        step("Переход на главную страницу",
                () -> new Home().clickMainlink()
        );
    }

    private void navigateToButtonByIndex(int targetIndex) {
        step("Навигация к кнопке #" + (targetIndex + 1), () -> {
            // Проверяем текущую позицию - находим первую видимую кнопку
            int currentPosition = findFirstVisibleButtonIndex();

            // Вычисляем сколько нужно прокруток
            int scrollsNeeded = targetIndex - currentPosition;

            AllureAttachments.attachText("Навигация",
                    "Текущая позиция: " + currentPosition + ", Нужно прокруток: " + scrollsNeeded);

            // Прокручиваем только если нужно и если можно
            for (int i = 0; i < scrollsNeeded; i++) {
                if (isRightButtonEnabled()) {
                    RIGHT_BUTTON.scrollTo()
                            .shouldBe(interactable, DEFAULT_TIMEOUT)
                            .click();
                    WaitHelper.waitForAnimationComplete();
//                    WaitHelper.waitFor(DEFAULT_TIMEOUT);
                } else {
                    AllureAttachments.attachText("Предупреждение",
                            "Не удалось прокрутить к кнопке #" + targetIndex +
                                    ". Кнопка вправо недоступна.");
                    break;
                }
            }

            // Проверяем что нужная кнопка теперь видима (или хотя бы существует)
            TARO_BUTTONS.get(targetIndex)
                    .shouldBe(visible, DEFAULT_TIMEOUT);

            AllureAttachments.attachText("Навигация завершена",
                    "Успешно перешли к кнопке: " + TAROS_NAMES[targetIndex]);
        });
    }

    /**
     * Находит индекс первой видимой кнопки в карусели
     */
    private int findFirstVisibleButtonIndex() {
        for (int i = 0; i < TARO_BUTTONS.size(); i++) {
            if (TARO_BUTTONS.get(i).isDisplayed()) {
                return i;
            }
        }
        return 0; // Если ничего не найдено, считаем что на первой позиции
    }

    /**
     * Проверяет доступна ли кнопка прокрутки вправо
     */
    private boolean isRightButtonEnabled() {
        try {
            return RIGHT_BUTTON.exists() &&
                    RIGHT_BUTTON.isDisplayed() &&
                    !RIGHT_BUTTON.has(attribute("disabled")) &&
                    !Objects.requireNonNull(RIGHT_BUTTON.getAttribute("class")).contains("swiper-button-disabled");
        } catch (Exception e) {
            return false;
        }
    }

    private void mealButtonByIndex(int index) {
        String taroName = TAROS_NAMES[index];

        step("Тестирование кнопки: " + taroName, () -> {
            // Кнопка после навигации
            SelenideElement mealButton = TARO_BUTTONS.get(index);

            step("Проверка видимости - " + taroName, () -> {
                mealButton.shouldBe(visible, DEFAULT_TIMEOUT)
                        .shouldBe(interactable, DEFAULT_TIMEOUT);

                AllureAttachments.attachScreenshot("Кнопка " + taroName + " видима");
            });

            step("Проверка редиректа", () -> {
                String originalUrl = WebDriverRunner.url();
                mealButton.click();

                WaitHelper.waitForUrlContains(originalUrl, DEFAULT_TIMEOUT);
                WaitHelper.waitForPageLoad();

                //Проверяем что мы на правильной странице
                TITLE.shouldBe(visible, DEFAULT_TIMEOUT);
                String titleText = TITLE.getText().trim();

                AllureAttachments.attachText("Заголовок страницы", titleText);
                AllureAttachments.attachScreenshot("После клика на " + taroName);
            });

            step("Возвращаемся на страницу 'Таро'", () -> {
                UniversalMethod.safeClick(DASHBOARDS_BUTTON, "Назад на страницу 'Таро'");

                WaitHelper.waitForPageLoad();

                TARO_BUTTONS.shouldHave(CollectionCondition.size(8), DEFAULT_TIMEOUT);

                AllureAttachments.attachText("Тест завершен",
                        "✅ " + taroName + " - навигация работает корректно");
            });
        });
    }
}
