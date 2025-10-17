package ru.tutor.page.dashbords;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import net.datafaker.Faker;
import org.openqa.selenium.Keys;
import ru.tutor.page.PersonalAccount.Catalog;
import ru.tutor.page.PersonalAccount.Home;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class helloDash {

    private static final Faker faker = new Faker(new Locale("en"));

    // Локаторы модального окна приветствия
    private static final SelenideElement ACCESS_DATA_TITLE = $$("h2").find(exactText("Укажите данные для доступа к платформе"));
    private static final SelenideElement NAME_INPUT = $("[name=\"name\"]");
    private static final SelenideElement PASSWORD_INPUT = $("[name=\"newPassword\"]");
    private static final SelenideElement REPEAT_PASSWORD_INPUT = $("[name=\"repeatPassword\"]");
    private static final SelenideElement GENDER_PLACEHOLDER = $("[id=\"react-select-2-placeholder\"]");
    private static final SelenideElement GENDER_INPUT = $("[id=\"react-select-2-input\"]");
    private static final SelenideElement BUTTON_NEXT_STEP = $$("button").find(exactText("Следующий шаг"));
    private static final SelenideElement BUTTON_CLOSE_MODAL = $("._close_1l2kq_40");

    // Локаторы модального окна знакомства с платформой
    private static final SelenideElement BUTTON_OUT_MYSELF = $$("button").find(exactText("Нет, я хочу разобраться сам"));

    @Step("Заполенение приветственного модального окна")
    private static void completeWelcomeModal() {
        step("Ожидание загрузки модального окна", () -> {
            ACCESS_DATA_TITLE.shouldBe(visible, Duration.ofSeconds(10));
            AllureAttachments.attachScreenshot("Модальное окно приветствия");
        });

        step("Заполнение персональных данных", () -> {
            String userName = faker.name().firstName();
            String password = "12345678";
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
    private static void completeWelcomeModalAfterRegistration() {
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
    private static void selectGender() {
        step("Выбор из выпадающего списка", () -> {
            GENDER_PLACEHOLDER.shouldBe(visible).click();
            GENDER_INPUT.sendKeys(Keys.ENTER);
            AllureAttachments.attachText("Выбор пола", "Пол выбран из выпадающего списка");
        });
    }

    @Step("Обработка окна знакомства с платформой")
    private static void hadlePlatformIntroduction() {
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
    private static void verifyOnboardingCompletion() {
        step("Проверка отсутствия модальных окон", () -> {
            ACCESS_DATA_TITLE.shouldNotBe(visible, Duration.ofSeconds(3));
            BUTTON_OUT_MYSELF.shouldNotBe(visible, Duration.ofSeconds(3));
            AllureAttachments.attachText("Onboarding статус", "Onboarding процесс успешно завершён");
        });
    }

    @Step("Заполнение окна приветствия для вновь зарегистрированных пользователей")
    public static void fillingOutTheWelcomeWindow() {
        completeWelcomeModal();
        hadlePlatformIntroduction();
        verifyOnboardingCompletion();
    }

    @Step("Заполнение окна приветствия для новых зарегистрированных пользователей")
    public static void fillingOutTheWelcomeWindowANewRegistration() {
        completeWelcomeModalAfterRegistration();
        verifyPlatformNavigation();
        AllureAttachments.attachText("Onboarding статус", "Onboarding процесс успешно завершён");
    }

    @Step("Проверка навигации по платформе")
    private static void verifyPlatformNavigation(){
        Catalog.verifyCatalogPresence();
        Home.verifyHomeLinkPresence();
        AllureAttachments.attachText("Навигация", "Основные элементы навигации доступны");
    }
}
