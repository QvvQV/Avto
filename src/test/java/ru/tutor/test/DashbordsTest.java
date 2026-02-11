package ru.tutor.test;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ru.tutor.base.BaseUiTest;
import ru.tutor.page.*;
import ru.tutor.page.PaymentModalWindow.paymentModal;
import ru.tutor.page.PersonalAccount.Catalog;
import ru.tutor.page.PersonalAccount.Home;
import ru.tutor.page.dashbords.*;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.RewardHelper;
import ru.tutor.utils.WaitHelper;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

@Epic("Дашборды и onboarding")
@Feature("Процесс onboarding нового пользователя")
@Story("Заполнение приветственных модулей и проверка дашбордов")
@DisplayName("📊 Тесты onboarding процесса и дашбордов")
@Execution(ExecutionMode.SAME_THREAD)


public class DashbordsTest extends BaseUiTest {

    @AfterEach
    protected void tearDown() {
        closeWebDriver();
    }

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

    private void executeCarouselTest(String productName, String dashboardName) {
        step("Открытие страницы логина", () -> {
            open(addressPage.login);
            AllureAttachments.attachText("Логин", "Страница логина открыта");
        });
        step("Заполнение валидными данными", () -> {
            Registration.validPerson();
        });
        allDashbords.testSingleProduct(productName, dashboardName);
    }

    private void chekingInsideTheDashboard(String productName, String dashboard) {
        step("1. Открытие страницы логина", () -> {
            open(addressPage.login);
            AllureAttachments.attachText("Логин", "Страница логина открыта");
        });
        step("2. Заполнение валидными данными", () -> {
            Registration.validPerson();
        });
        step("3. Проверяем модальное окно ежедневной награды", ()->{
            RewardHelper.safeClickCollectReward();
        });
        step("4. Добавляем продукт", () -> {
            Home.walkthroughtAddingACourse(productName);
        });
        step("5. Обновляем страницу", () -> {
//            refresh();
//            WaitHelper.waitForPageLoad();
            allDashbords.testSingleProduct(productName, dashboard);
        });
    }

//    @Test
//    @RepeatedTest(5)
    @Severity(SeverityLevel.CRITICAL)
    @Order(1)
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

//    @Test
    @Order(2)
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

//    @Test
    @Order(3)
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
            WaitHelper.waitForAnimationComplete();

            AllureAttachments.attachScreenshot("Финальное состояние после onboarding");
        });
    }

//    @Test
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Дневник питания'")
    void testingRedirectsForNutrionDashboard() {
        String productName = "Дневник питания";
        String dashboard = "Трекер питания";
        chekingInsideTheDashboard(productName, dashboard);

        step("6. переходим в дашборд питания", () -> {
            nutritionTracker.clickFromNutritionTracker();
        });
        step("7. Начинаем программу диет", () -> {
            nutritionTracker.startProgramm();
            refresh();
        });
        step("8. Проверяем редирект по страницам приёма пищи", () -> {
            nutritionTracker.mealNavigationWithCollection();
        });
        step("9. Удаляем курс", () -> {
            Home.deleteProduct(productName);
        });
    }

//    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Натальная карта'")
    void testingRedirectsForNatalChart() {
        String productName = "астрология";
        String dashboard = "Натальная карта";
        chekingInsideTheDashboard(productName, dashboard);

        step("6. Переходим в дашборд 'Натальная карта'", () -> {
            natalChart.clickFromNatalChar();
        });
        step("7. Проверяем кнопки персонального календаря", () -> {
            natalChart.personaleCalendar();
        });
        step("8. проверяем страницу 'Info'", () -> {
            natalChart.redirectToTheInfoPage();
        });
        step("9. Удаляем курс", () -> {
            Home.deleteProduct(productName);
        });
    }

//    @Test
    @Order(6)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'SpyHeart'")
    void testingRedirectsForSpyHeart() {
        String productName = "spyHeart";
        String dashboard = "SpyHeart";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'SpyHeart'", () -> {
            spyHeart.clickFromSpyHeart();
        });
        step("7. Проверка функционала", () -> {
            spyHeart.ckeckVisionElement();
        });
        step("8. Редирект на главную страницу", () -> {
            Home.clickMainlink();
        });
        step("9. Удаляем курс", () -> {
            Home.deleteProduct(productName);
        });
    }

//    @Test
    @Order(7)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Тренажер памяти'")
    void testingRedirectsForMemoryTrainer() {
        String productName = "тренировка памяти";
        String dashboard = "Тренажер памяти";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'Тренажер памяти'", () -> {
            memoryTrainer.clickFromMemoryTrainer();
        });
        step("7. Проверяем редирект по страницам Игр", () -> {
            memoryTrainer.gameNavigationWithCollection();
        });
        step("8. Удаляем курс", () -> {
            Home.deleteProduct(productName);
        });
    }


//    @Test
    @Order(8)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Расчёт матрицы'")
    void testingRedirectsForMatrixCalculation() {
        String productName = "матрица судьбы";
        String dashboard = "Расчет матрицы";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'Расчёт матрицы'", () -> {
            matrixCalculation.clickFromMatrixCalculation();
        });
        step("7. Проверяем работу внутри дашборда", () -> {
            matrixCalculation.openModalWindowInTheDashboard();
        });
        step("8. Удаляем курс", () -> {
            Home.deleteProduct(productName);
        });
    }

//    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Учет каналов'")
    void testingRedirectsForChannelAccounting() {
        String productName = "таргетолог";
        String dashboard = "Учет каналов";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'Учет каналов'", () -> {
            channelAccounting.clickFromChannelAccounting();
        });
        step("7. Проверяем работу внутри дашборда", () -> {
            channelAccounting.fillForm();
        });
        step("8. Удаляем курс", () -> {
            Home.deleteProduct(productName);
        });
    }

//    @Test
    @Order(10)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Таро'")
    void testingRedirectsForTaro() {
        String productName = "таро";
        String dashboard = "Таро";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'Таро'", () -> {
            taro.clickFromTaro();
        });
        step("7. Проверяем работу внутри дашборда", () -> {
            taro.mealNavigationWithCollection();
        });
        step("8. Удаляем курс", () -> {
            Home.deleteProduct(productName);
        });
    }

//    @Test
    @Order(11)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Редактор карточек товара'")
    void testingRedirectsForProductCardEdition() {
        String productName = "авито";
        String dashboard = "Редактор карточек товара";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'Редактор карточек товара'", () -> {
            productCardEditor.clickFromProductCardEdition();
        });
        step("7. Проверяем работу внутри дашборда", () -> {
            productCardEditor.workDashboard();
        });
        step("8. Удаляем курс", () -> {
            Home.deleteProduct(productName);
        });
    }

//    @ParameterizedTest
//    @Order(12)
//    @CsvSource({
//            "авито, Редактор карточек товара",
//            "таргетолог, Учет каналов",
//            "таро, Таро",
//            "дневник питания, Трекер питания",
//            "spyHeart, SpyHeart",
//            "матрица судьбы, Расчет матрицы",
//            "астрология, Натальная карта",
//            "тренировка памяти, Тренажер памяти"
//    })
//    @Severity(SeverityLevel.MINOR)
//    @DisplayName("Тестирование карусели для: {0}")
//    void testCarouselForProduct(String productName, String dashboardName) {
//        executeCarouselTest(productName, dashboardName);
//    }
}
