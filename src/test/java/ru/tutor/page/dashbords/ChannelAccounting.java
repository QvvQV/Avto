package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import org.openqa.selenium.Keys;
import ru.tutor.page.personalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;
import ru.tutor.utils.UniversalMethod;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class ChannelAccounting {

    private static final Faker faker = new Faker(Locale.ENGLISH);

    // Локтоары
    private final SelenideElement CHANNEL_ACCOUNTING = $$("p").find(exactText("Учет каналов"));
    private final SelenideElement URL_INPUT = $("input[name='url']");
    private final SelenideElement BISNESS_PLACEHOLDER = $("[id=\"react-select-2-placeholder\"]");
    private final SelenideElement BISNESS_INPUT = $("[id=\"react-select-2-input\"]");
    private final SelenideElement DATE_INPUT = $("input[placeholder='Дата создания']");
    private final SelenideElement NAME_INPUT = $("[name='name'");
    private final SelenideElement COVERAGE_INPUT = $("[name='coverage'");
    private final SelenideElement SUBSCRIBERS_INPUT = $("[name='subscribers'");
    private final SelenideElement ADD_BUTTON = $$("button").find(exactText("Добавить"));
    private final SelenideElement DELETE_BUTTON = $("._tool_1uxgp_1");


    @Step("Кликаем по Учет каналов")
    public void clickFromChannelAccounting() {
        CHANNEL_ACCOUNTING.shouldBe(visible, Duration.ofSeconds(5))
                .shouldBe(interactable)
                .click();
        WaitHelper.waitForAnimationComplete();
        AllureAttachments.attachScreenshot("Результат после клика");
    }

    @Step("Заполняем URL")
    private void valueURL() {
        step("Вводим параметр URL", () -> {
            URL_INPUT.shouldBe(visible, Duration.ofSeconds(3))
                    .setValue("https://client.ttplace.ru/user/telegram/dashboard");
        });
    }

    @Step("Выбираем тематику")
    private void addTematic() {
        step("Заполняем тематику", () -> {
            BISNESS_PLACEHOLDER.shouldBe(visible).click();
            BISNESS_INPUT.sendKeys(Keys.ENTER);
            AllureAttachments.attachText("Выбор тeматики: ", "Тематика выбрана");
            WaitHelper.waitForAnimationComplete();
        });
    }

    @Step("Выбираем дату")
    private void addDate() {
        step("Выбираем дату", () -> {
            DATE_INPUT.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .setValue("02.02.2000");
        });
    }

    @Step("Задаём Название")
    private void addName() {
        step("Задаём название группы", () -> {
            NAME_INPUT.shouldBe(visible, Duration.ofSeconds(3))
                    .click();
            NAME_INPUT.setValue(faker.name().name());
        });
    }

    @Step("Средний охват")
    private void average() {
        step("Задаём средний охват", () -> {
            COVERAGE_INPUT.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .setValue(faker.numerify("###"));
        });
    }

    @Step("Количество подписчиков")
    private void numberSudscribers() {
        step("Задаём количество подписчиков", () -> {
            SUBSCRIBERS_INPUT.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .setValue(faker.numerify("1###"));
        });
    }

    @Step("Кнопка добавить")
    private void clickButtonAdd() {
        step("Кликаем по кнопке", () -> {
            ADD_BUTTON.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(clickable)
                    .click();
            AllureAttachments.attachScreenshot("Заполненная форма");
            WaitHelper.waitForAnimationComplete();
            WaitHelper.waitForPageLoad();
        });
    }

    @Step("Удаление каналов")
    private void deleteCannel() {
        step("Удаляем канал", () ->
            UniversalMethod.safeClick(DELETE_BUTTON, "Удалить канал")
        );
    }


    @Step("Заполняем форму")
    public void fillForm() {
        step("Кнопка добавить не активна", () -> {
            ADD_BUTTON.shouldHave(disabled);
        });
        valueURL();
        addTematic();
        addDate();
        addName();
        average();
        numberSudscribers();
        step("Кнопка добавить активна", () -> {
            ADD_BUTTON.shouldHave(enabled);
        });
        clickButtonAdd();
        AllureAttachments.attachScreenshot("Аккаунты");
        deleteCannel();
        WaitHelper.waitForAnimationComplete();
        WaitHelper.waitForPageLoad();
        AllureAttachments.attachScreenshot("Дашборд после удаления");
        step("переход на главную", () -> new Home().clickMainlink());
    }
}
