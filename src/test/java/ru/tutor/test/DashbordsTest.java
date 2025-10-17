package ru.tutor.test;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.base.BaseUiTest;
import ru.tutor.page.*;
import ru.tutor.page.PaymentModalWindow.paymentModal;
import ru.tutor.page.PersonalAccount.Catalog;
import ru.tutor.page.PersonalAccount.Home;
import ru.tutor.page.dashbords.allDashbords;
import ru.tutor.page.dashbords.helloDash;
import ru.tutor.page.dashbords.memoryTrainer;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.util.Map;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;
import static io.qameta.allure.Allure.step;

@Epic("Дашборды и onboarding")
@Feature("Процесс onboarding нового пользователя")
@Story("Заполнение приветственных модулей и проверка дашбордов")
@DisplayName("📊 Тесты onboarding процесса и дашбордов")
public class DashbordsTest extends BaseUiTest {

    private static final Map<String, String> PRODUCTS_DASHBOARDS = Map.of(
            "таро", "Таро",
        "дневник питания", "Трекер питания",
        "spyHeart", "SpyHeart",
        "матрица судьбы", "Расчет матрицы",
        "астрология", "Натальная карта",
        "тренировка памяти", "Тренажер памяти",
        "авито", "Редактор карточек товара",
        "таргетолог", "Учет каналов"
    );

    @Test
//    @RepeatedTest(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Успешное оформление заказа с полным onboarding процессом с главной")
    void successfulOrderWithRedirectCorrection() {

        step("1. Начало оформления заказа", () -> {
            open(addressPage.urlMain);


            ttPage.clickBtnOpenFor();
            ttPage.clickPremium();
        });
        step("2. Заполнение данных и оплата", () -> {
            CloudPage.setDateModal();
            AllureAttachments.attachText("Email пользователя", CloudPage.atrEmail());

            CloudPage.clickCardPageBtn();
        });
        step("3. Обработка редиректа после оплаты", () -> {

            RedirectPage.redirectPage();
        });
        step("4. Прохождение onboarding процесса", () -> {
            helloDash.fillingOutTheWelcomeWindow();
            AllureAttachments.attachText("Onbording", "Приветсвенные модули завершены");
            AllureAttachments.attachScreenshot("Скриншот главного экрана");
        });
        step("5. Проверка видимости всех дашбордов", () -> {
            allDashbords.verifyDashboardsVisibility();
            AllureAttachments.attachText("Дашборды", "Все дашборды видны");
        });
        step("6. Фиксация финального состояния", () -> {
            AllureAttachments.attachScreenshot("Финальное состояние после onboarding");
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Tag("registration")
    @Tag("onboarding")
    @DisplayName("✅ Регистрация пользователя с полным onboarding процессом со старницы регистрации")
    void userRegistrationWithCompleteOnbordingProcess() {

        step("1. Открытие страницы регистрации", () -> {
            open(addressPage.register);
            AllureAttachments.attachText("Регистрация", "Страница регистрации открыта");
        });
        step("2. Заполнение регистрационных данных", () -> {

            Registration.inputName();
            Registration.clickBtnRegistr();

            Registration.randomEmail();
            Registration.validPass();
            Registration.validRepeatPass();
            AllureAttachments.attachScreenshot("Форма регистрации");

            Registration.clickBtnRegister();
            WaitHelper.waitForPageLoad();
        });
        step("3. Заполнение приветсвенного окна", () -> {
            helloDash.fillingOutTheWelcomeWindowANewRegistration();
            Catalog.verifyCatalogHeader();
            AllureAttachments.attachText("Onboarding", "Приветсвенный процесс завершён");
        });
        step("4. Навигация по платформе", () -> {
            Home.navigateToMainPage();
            WaitHelper.waitForPageLoad();
        });
        step("5. Проверка видимости всех дашбордов", () -> {
            allDashbords.verifyDashboardsVisibility();
            AllureAttachments.attachText("Дашборды", "Все дашборды видны");
        });
        step("6. Фиксация финального состояния", () -> {
            AllureAttachments.attachScreenshot("Финальное состояние после onboarding");
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Переход на страницу оплаты зарегистрированного пользователя с модуля Приложения")
    void transitionToThePaymentPageForRegisteredUser() {
        step("1. Открытие страницы регистрации", () -> {
            open(addressPage.register);
            AllureAttachments.attachText("Регистрация", "Страница регистрации открыта");
        });
        step("2. Заполнение регистрационных данных", () -> {

            Registration.inputName();
            Registration.clickBtnRegistr();

            Registration.randomEmail();
            Registration.validPass();
            Registration.validRepeatPass();
            AllureAttachments.attachScreenshot("Форма регистрации");

            Registration.clickBtnRegister();
            WaitHelper.waitForPageLoad();
        });
        step("3. Заполнение приветсвенного окна", () -> {
            helloDash.fillingOutTheWelcomeWindowANewRegistration();
            Catalog.verifyCatalogHeader();
            AllureAttachments.attachText("Onboarding", "Приветсвенный процесс завершён");
        });
        step("4. Навигация по платформе", () -> {
            Home.navigateToMainPage();
            WaitHelper.waitForPageLoad();
        });
        step("5. Проверка видимости всех дашбордов", () -> {
            allDashbords.verifyDashboardsVisibility();
            AllureAttachments.attachText("Дашборды", "Все дашборды видны");
        });
        step("6. Вызываем модальное окно оплаты", () -> {
            memoryTrainer.clickFromMemoryTrainer();
        });
        step("7. Проводим оплату подписки", () -> {
            paymentModal.verifyPriceIsOneRuble();
        });
        step("8. Фиксация финального состояния", () -> {
            refresh();
            WaitHelper.waitForPageLoad();

            AllureAttachments.attachScreenshot("Финальное состояние после onboarding");
        });
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Изменение первого продукта в карусели Приложения")
    void changingTheFirstProductInTheAppsCarousel() {
        step("Открытие страницы логина", () -> {
            open(addressPage.login);
            AllureAttachments.attachText("Логин", "Страница логина открыта");
        });
        step("Заполнение валидными данными", () -> {
            Registration.validPerson();
        });

        for (Map.Entry<String, String> entry : PRODUCTS_DASHBOARDS.entrySet()) {
            String productName = entry.getKey();
            String dashboardName = entry.getValue();

            allDashbords.testSingleProduct(productName, dashboardName);
        }




//        step("3. Добавление курса с дашбордом", () -> {
//            Home.enterCourseNames();
//        });
//        allDashbords.verifyDashboardsVisibilityOnFirstSlide();
//        step("4. Удаляем курс", () -> {
//            Home.deleteProduct();
//        });
//        step("5. Фиксация финального состояния", () -> {
//            WaitHelper.waitForPageLoad();
//
//            AllureAttachments.attachScreenshot("Финальное состояние после onboarding");
//        });
    }
}
