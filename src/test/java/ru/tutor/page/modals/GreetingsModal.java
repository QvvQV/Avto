package ru.tutor.page.modals;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.universalMethod;

import java.util.Locale;
import java.util.Random;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

/*
 * Кнопки и логика модалки приветствия
 */

public class GreetingsModal {

    // Константы
    private static final Faker faker = new Faker(Locale.ENGLISH);

    // Названия кнопок
    private static final String NEXT_STEP_TEXT = "Следующий шаг";
    private static final String SNAP_TEXT = "Привязать";
    private static final String SKIP_TEXT = "Пропустить";
    private static final String START_LEARN_TEXT = "Начать обучение";

    //Локаторы
    private static final SelenideElement NEXT_STEP_BUTTON = $$("button").find(exactText(NEXT_STEP_TEXT));
    private static final SelenideElement SNAP_BUTTON = $$("button").find(exactText(SNAP_TEXT));
    private static final SelenideElement SKIP_BUTTON = $$("button").find(exactText(SKIP_TEXT));
    private static final SelenideElement START_LEARN_BUTTON = $$("button").find(exactText(START_LEARN_TEXT));
    private static final SelenideElement NAME_INPUT = $("[name=\"name\"]");
    private static final SelenideElement PASSWORD_INPUT = $("[name=\"newPassword\"]");
    private static final SelenideElement REPEAT_PASSWORD_INPUT = $("[name=\"repeatPassword\"]");
    private static final SelenideElement MALE_INPUT = $("#react-select-2-option-0"); // Мужчина
    private static final SelenideElement MALE_INPUT_DROPDOWN = $(".css-zgx56i-control");
    private static final SelenideElement NUMBER_PHONE = $("._inputContainer_v88q7_18 input");
    private static final SelenideElement START_TIME_CONTAINER = $("._selectContainer_mo70f_58:has(#react-select-3-input)");
    private static final SelenideElement END_TIME_CONTAINER = $("._selectContainer_mo70f_58:has(#react-select-4-input)");
    private static final SelenideElement START_TIME = START_TIME_CONTAINER.$(".css-3nli2w-singleValue");
    private static final SelenideElement END_TIME = END_TIME_CONTAINER.$(".css-b06pby-placeholder");
    private static final SelenideElement CLOSE_BUTTON = $x("//button[contains(@class, '_close_')]");

    @Step("Заполнение данными для доступа быстрая форма")
    public static void fillAccessInput() {
        step("Заполнение данными для доступа", () -> {
            String name = faker.name().firstName();
            String password = "12345678";

            universalMethod.safeClick(NEXT_STEP_BUTTON, NEXT_STEP_TEXT);
            WaitHelper.waitForAnimationComplete();
            step("Ввод имени пользователя", () -> universalMethod.safeSetValue(NAME_INPUT, name, "Имя пользователя")
            );
            step("Задать пароль", () -> universalMethod.safeSetValue(PASSWORD_INPUT, password, "Пароль")
            );
            step("Повторение пароля", () -> universalMethod.safeSetValue(REPEAT_PASSWORD_INPUT, password, "Повторите новый пароль")
            );
            verifyActiveOption();
            universalMethod.safeClick(NEXT_STEP_BUTTON, NEXT_STEP_TEXT);

            step("Закрытие модального окна", () -> {
                universalMethod.safeClick(CLOSE_BUTTON, "Крестик");
                WaitHelper.waitForAnimationComplete();
            });
        });
    }

    @Step("Заполнение данными для доступа Вся форма")
    public static void fillAccessInputAllForm() {
        String name = faker.name().firstName();
        String password = "12345678";

        universalMethod.safeClick(NEXT_STEP_BUTTON, NEXT_STEP_TEXT);
        WaitHelper.waitForAnimationComplete();
        step("Ввод имени пользователя", () -> universalMethod.safeSetValue(NAME_INPUT, name, "Имя пользователя")
        );
        step("Задать пароль", () -> universalMethod.safeSetValue(PASSWORD_INPUT, password, "Пароль")
        );
        step("Повторение пароля", () -> universalMethod.safeSetValue(REPEAT_PASSWORD_INPUT, password, "Повторите новый пароль")
        );
        verifyActiveOption();
        universalMethod.safeClick(NEXT_STEP_BUTTON, NEXT_STEP_TEXT);
        universalMethod.safeClick(NEXT_STEP_BUTTON, NEXT_STEP_TEXT);
        universalMethod.safeClick(NEXT_STEP_BUTTON, NEXT_STEP_TEXT);
        WaitHelper.waitForAnimationComplete();
        step("Выбрать интересы", GreetingsModal::randomInterest
        );
        universalMethod.safeClick(NEXT_STEP_BUTTON, NEXT_STEP_TEXT);
        step("Ввод номер телефона", () -> enterRussianPhone(faker.phoneNumber().cellPhone())
        );
        step("Задать время 'C'", () -> selectStartTime("01:00")
        );
        step("Задать время 'До'", () -> selectEndTime("12:00")
        );
        step("Привязываем телефон", () -> universalMethod.safeClick(SNAP_BUTTON, SNAP_TEXT)
        );
        step("Начинаем учиться", () -> universalMethod.safeClick(START_LEARN_BUTTON, START_LEARN_TEXT)
        );
    }

    @Step("Заполнение двух пунктов в приветственной модалке")
    public static void fillOutTwoFieldsWelcomeModal() {
        step("Заполнение двух пунктов в приветсвенной модалке", () -> {
            String name = faker.name().firstName();

            universalMethod.safeClick(NEXT_STEP_BUTTON, NEXT_STEP_TEXT);
            WaitHelper.waitForAnimationComplete();
            step("Ввод имени пользователя", () -> universalMethod.safeSetValue(NAME_INPUT, name, "Имя пользователя")
            );
            verifyActiveOption();
            universalMethod.safeClick(NEXT_STEP_BUTTON, NEXT_STEP_TEXT);

            step("Закрытие модального окна", () -> {
                universalMethod.safeClick(CLOSE_BUTTON, "Крестик");
                WaitHelper.waitForAnimationComplete();
            });
        });
    }

    @Step("Метод для выбора пола")
    private static void verifyActiveOption() {

        universalMethod.safeClick(MALE_INPUT_DROPDOWN, "Ваш пол");
        universalMethod.safeClick(MALE_INPUT, "Мужчина");
    }

    @Step("Выбрать два случайных пункта из интересов")
    private static void randomInterest() {
        ElementsCollection categories = $$("._Categorie_1tq9g_1");
        int total = categories.size();

        Random random = new Random();
        int index1 = random.nextInt(total);
        int index2;
        do {
            index2 = random.nextInt(total);
        } while (index2 == index1);

        categories.get(index1).click();
        categories.get(index2).click();
    }

    @Step("Выбор времени С")
    private static void selectStartTime(String time) {
        MALE_INPUT_DROPDOWN.click();
        $$("div[class*='option']").findBy(text(time)).click();
        START_TIME.shouldHave(text(time));
    }

    @Step("Выбор времени До")
    private static void selectEndTime(String time) {
        END_TIME.click();
        $$("div[class*='option']").findBy(text(time)).click();
        // После выбора появится css-3nli2w-singleValue вместо placeholder
        END_TIME_CONTAINER.$("div.css-3nli2w-singleValue").shouldHave(text(time));
    }

    @Step("Вводим номер телефона")
    private static void enterRussianPhone(String phoneDigits) {
        universalMethod.safeClick(NUMBER_PHONE, "Номер телефона");

        // Вводим посимвольно
        for (char digit : phoneDigits.toCharArray()) {
            NUMBER_PHONE.sendKeys(String.valueOf(digit));
            sleep(100); // небольшая пауза для маски
        }
    }

    private String getPhoneValue() {
        return NUMBER_PHONE.getValue();
    }

    public boolean isPhoneValid() {
        String value = getPhoneValue();
        // Проверка формата
        return value.matches("\\+7 \\(\\d{3}\\) \\d{3}-\\d{2}-\\d{2}");
    }
}