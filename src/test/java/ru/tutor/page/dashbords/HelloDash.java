package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import org.openqa.selenium.Keys;
import ru.tutor.config.TestConfig;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.personalAccount.Catalog;
import ru.tutor.page.personalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.qameta.allure.Allure.step;

public class HelloDash {

    private final Faker faker = new Faker(Locale.ENGLISH);
    private final Home home = new Home();

    // Локаторы модального окна приветствия
    private final SelenideElement ACCESS_DATA_TITLE_FIRST = $$("h2").find(exactText("Добро пожаловать на TutorPlace"));
    private final SelenideElement ACCESS_DATA_TITLE = $$("h2").find(exactText("Укажите данные для доступа к платформе"));
    private final SelenideElement NAME_INPUT = $("[name='name']");
    private final SelenideElement PASSWORD_INPUT = $("[name='newPassword']");
    private final SelenideElement REPEAT_PASSWORD_INPUT = $("[name='repeatPassword']");
    private final SelenideElement GENDER_PLACEHOLDER = $("[id=\"react-select-2-placeholder\"]");
    private final SelenideElement GENDER_INPUT = $("[id=\"react-select-2-input\"]");
    private final SelenideElement BUTTON_NEXT_STEP = $$("button").find(exactText("Следующий шаг"));
    private final SelenideElement BUTTON_CLOSE_MODAL = $("._close_1l2kq_40");

    // Локаторы модального окна знакомства с платформой
    private static final SelenideElement BUTTON_OUT_MYSELF = $$("button").find(exactText("Нет, я хочу разобраться сам"));

    /** Пароль */
    String password = TestConfig.getTestPassword();

    @Step("Заполнение приветственного модального окна")
    private void completeWelcomeModal() {
        step("Ожидание загрузки модального окна", () -> {

            ACCESS_DATA_TITLE_FIRST.shouldBe(visible, Duration.ofSeconds(10));
            step("переход к следующему шагу", () -> {
                BUTTON_NEXT_STEP.shouldBe(interactable, Duration.ofSeconds(5)).click();
                WaitHelper.waitForAnimationComplete();
            });
            ACCESS_DATA_TITLE.shouldBe(visible, Duration.ofSeconds(10));
            AllureAttachments.attachScreenshot("Модальное окно приветствия");
        });

        step("Заполнение персональных данных", () -> {
            String userName = faker.name().firstName();
            NAME_INPUT.setValue(userName);

            PASSWORD_INPUT.setValue(password);
            REPEAT_PASSWORD_INPUT.setValue(password);

            selectGender();

            AllureAttachments.attachText("Данные пользователя",
                    "Имя: " + userName + "\nПароль: " + password);
        });

        step("переход к следующему шагу", () -> {
            BUTTON_NEXT_STEP.shouldBe(interactable, Duration.ofSeconds(5)).click();
            WaitHelper.waitForAnimationComplete();
        });
    }

    @Step("Заполнение приветсвенного модального окна после регистрации")
    private void completeWelcomeModalAfterRegistration() {
        step("Ожидание и валидация модального окна", () -> {
            ACCESS_DATA_TITLE.shouldBe(visible, Duration.ofSeconds(10))
                    .shouldHave(exactText("Укажите данные для доступа к платформе"));
            AllureAttachments.attachScreenshot("Модальное окно приветствия");
        });

        step("Заполнение обязательных полей", () -> {
            selectGender();
            BUTTON_NEXT_STEP.shouldBe(interactable, Duration.ofSeconds(5)).click();
            WaitHelper.waitForAnimationComplete();
        });

        step("Проверка завершения шага", () -> {
            ACCESS_DATA_TITLE.shouldNotBe(visible, Duration.ofSeconds(5));
        });
    }

    @Step("Выбор пола пользователя")
    private void selectGender() {
        step("Выбор из выпадающего списка", () -> {
            GENDER_PLACEHOLDER.shouldBe(visible).click();
            GENDER_INPUT.sendKeys(Keys.ENTER);
            AllureAttachments.attachText("Выбор пола", "Пол выбран из выпадающего списка");
        });
    }

    @Step("Обработка окна знакомства с платформой")
    private void hadlePlatformIntroduction() {
        step("закрытие приветственного модального окна", () -> {
            BUTTON_CLOSE_MODAL.shouldBe(visible, Duration.ofSeconds(3))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForModalToClose();
        });

        step("Выбор самостоятельного изучения платформы", () -> {
            BUTTON_OUT_MYSELF.shouldBe(visible, Duration.ofSeconds(5))
                    .shouldBe(interactable)
                    .click();
            WaitHelper.waitForAnimationComplete();
        });
    }

    @Step("Проверка завершения onboarding процесса")
    private void verifyOnboardingCompletion() {
        step("Проверка отсутствия модальных окон", () -> {
            ACCESS_DATA_TITLE.shouldNotBe(visible, Duration.ofSeconds(3));
            BUTTON_OUT_MYSELF.shouldNotBe(visible, Duration.ofSeconds(3));
            AllureAttachments.attachText("Onboarding статус", "Onboarding процесс успешно завершён");
        });
    }

    @Step("Заполнение окна приветствия для вновь зарегистрированных пользователей")
    public void fillingOutTheWelcomeWindow() {
        completeWelcomeModal();
        hadlePlatformIntroduction();
        verifyOnboardingCompletion();
    }

    @Step("Заполнение окна приветствия для новых зарегистрированных пользователей")
    public void fillingOutTheWelcomeWindowANewRegistration() {
        completeWelcomeModalAfterRegistration();
        verifyPlatformNavigation();
        AllureAttachments.attachText("Onboarding статус", "Onboarding процесс успешно завершён");
    }

    @Step("Проверка навигации по платформе")
    private void verifyPlatformNavigation() {
        new Catalog().verifyCatalogPresence();
        home.verifyHomeLinkPresence();
        AllureAttachments.attachText("Навигация", "Основные элементы навигации доступны");
    }
}
