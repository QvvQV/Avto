package ru.tutor.test.Ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ru.tutor.base.BaseUiTest;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.*;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.page.paymentModalWindow.PaymentModal;
import ru.tutor.page.personalAccount.Catalog;
import ru.tutor.page.personalAccount.Home;
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

    private final TtPage ttPage = new TtPage();
    private final Home home = new Home();
    private final CloudPage cloudPage = new CloudPage();
    private final HelloDash helloDash = new HelloDash();
    private final Registration registration = new Registration();
    private final AllDashbords allDashbords = new AllDashbords();
    private final Taro taro = new Taro();
    private final ProductCardEditor productCardEditor = new ProductCardEditor();
    private final NutritionTracker nutritionTracker = new NutritionTracker();
    private final NatalChart natalChart = new NatalChart();
    private final MemoryTrainer memoryTrainer = new MemoryTrainer();

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
            open(AddressPage.login);
            AllureAttachments.attachText("Логин", "Страница логина открыта");
        });
        step("Заполнение валидными данными",
            registration::validPerson
        );
        allDashbords.testSingleProduct(productName, dashboardName);
    }

    private void chekingInsideTheDashboard(String productName, String dashboard) {
        step("1. Открытие страницы логина", () -> {
            open(AddressPage.login);
            AllureAttachments.attachText("Логин", "Страница логина открыта");
        });
        step("2. Заполнение валидными данными",
                registration::validPerson
        );
        step("3. Проверяем модальное окно ежедневной награды",
            new RewardHelper()::safeClickCollectReward
        );
        step("4. Добавляем продукт", () ->
            home.walkthroughtAddingACourse(productName)
        );
        step("5. Обновляем страницу", () ->
            allDashbords.testSingleProduct(productName, dashboard)
        );
    }

//    @Test
//    @RepeatedTest(5)
    @Severity(SeverityLevel.CRITICAL)
    @Order(1)
    @DisplayName("✅ Успешное оформление заказа с полным onboarding процессом с главной")
    void successfulOrderWithRedirectCorrection() {

        step("1. Начало оформления заказа", () -> {
            open(AddressPage.URL_MAIN);


            ttPage.clickBtnOpenFor();
            ttPage.selectSubscription(TtPage.SubscriptionType.PREMIUM);
        });
        step("2. Заполнение данных и оплата", () -> {
            cloudPage.setDateModal();
            AllureAttachments.attachText("Email пользователя", cloudPage.atrEmail());

            cloudPage.clickCardPageBtn();
        });
        step("3. Обработка редиректа после оплаты",

            new RedirectPage()::redirectPage
        );
        step("4. Прохождение onboarding процесса", () -> {
            helloDash.fillingOutTheWelcomeWindow();
            AllureAttachments.attachText("Onbording", "Приветсвенные модули завершены");
            AllureAttachments.attachScreenshot("Скриншот главного экрана");
        });
        step("5. Проверка видимости всех дашбордов", () -> {
            allDashbords.verifyDashboardsVisibility();
            AllureAttachments.attachText("Дашборды", "Все дашборды видны");
        });
        step("6. Фиксация финального состояния", () ->
            AllureAttachments.attachScreenshot("Финальное состояние после onboarding")
        );
    }

//    @Test
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Tag("registration")
    @Tag("onboarding")
    @DisplayName("✅ Регистрация пользователя с полным onboarding процессом со старницы регистрации")
    void userRegistrationWithCompleteOnbordingProcess() {

        step("1. Открытие страницы регистрации", () -> {
            open(AddressPage.register);
            AllureAttachments.attachText("Регистрация", "Страница регистрации открыта");
        });
        step("2. Заполнение регистрационных данных", () -> {

            registration.inputName();
            registration.clickBtnRegistr();

            registration.randomEmail();
            registration.validPass();
            registration.validRepeatPass();
            AllureAttachments.attachScreenshot("Форма регистрации");

            registration.clickBtnRegister();
            WaitHelper.waitForPageLoad();
        });
        step("3. Заполнение приветсвенного окна", () -> {
            helloDash.fillingOutTheWelcomeWindowANewRegistration();
            new Catalog().verifyCatalogHeader();
            AllureAttachments.attachText("Onboarding", "Приветсвенный процесс завершён");
        });
        step("4. Навигация по платформе", () -> {
            home.navigateToMainPage();
            WaitHelper.waitForPageLoad();
        });
        step("5. Проверка видимости всех дашбордов", () -> {
            allDashbords.verifyDashboardsVisibility();
            AllureAttachments.attachText("Дашборды", "Все дашборды видны");
        });
        step("6. Фиксация финального состояния", () ->
            AllureAttachments.attachScreenshot("Финальное состояние после onboarding")
        );
    }

//    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("✅ Переход на страницу оплаты зарегистрированного пользователя с модуля Приложения")
    void transitionToThePaymentPageForRegisteredUser() {
        step("1. Открытие страницы регистрации", () -> {
            open(AddressPage.register);
            AllureAttachments.attachText("Регистрация", "Страница регистрации открыта");
        });
        step("2. Заполнение регистрационных данных", () -> {

            registration.inputName();
            registration.clickBtnRegistr();

            registration.randomEmail();
            registration.validPass();
            registration.validRepeatPass();
            AllureAttachments.attachScreenshot("Форма регистрации");

            registration.clickBtnRegister();
            WaitHelper.waitForPageLoad();
        });
        step("3. Заполнение приветсвенного окна", () -> {
            helloDash.fillingOutTheWelcomeWindowANewRegistration();
            new Catalog().verifyCatalogHeader();
            AllureAttachments.attachText("Onboarding", "Приветсвенный процесс завершён");
        });
        step("4. Навигация по платформе", () -> {
            home.navigateToMainPage();
            WaitHelper.waitForPageLoad();
        });
        step("5. Проверка видимости всех дашбордов", () -> {
            allDashbords.verifyDashboardsVisibility();
            AllureAttachments.attachText("Дашборды", "Все дашборды видны");
        });
        step("6. Вызываем модальное окно оплаты",
            memoryTrainer::clickFromMemoryTrainer
        );
        step("7. Проводим оплату подписки",
            () -> new PaymentModal().verifyPriceIsOneRuble()
        );
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

        step("6. переходим в дашборд питания",
            nutritionTracker::clickFromNutritionTracker
        );
        step("7. Начинаем программу диет", () -> {
            nutritionTracker.startProgramm();
            refresh();
        });
        step("8. Проверяем редирект по страницам приёма пищи",
            nutritionTracker::mealNavigationWithCollection
        );
        step("9. Удаляем курс", () ->
            home.deleteProduct(productName)
        );
    }

//    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Натальная карта'")
    void testingRedirectsForNatalChart() {
        String productName = "астрология";
        String dashboard = "Натальная карта";
        chekingInsideTheDashboard(productName, dashboard);

        step("6. Переходим в дашборд 'Натальная карта'",
            natalChart::clickFromNatalChar
        );
        step("7. Проверяем кнопки персонального календаря",
            natalChart::personaleCalendar
        );
        step("8. проверяем страницу 'Info'",
            natalChart::redirectToTheInfoPage
        );
        step("9. Удаляем курс", () ->
            home.deleteProduct(productName)
        );
    }

//    @Test
    @Order(7)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Тренажер памяти'")
    void testingRedirectsForMemoryTrainer() {
        String productName = "тренировка памяти";
        String dashboard = "Тренажер памяти";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'Тренажер памяти'", memoryTrainer::clickFromMemoryTrainer
        );
        step("7. Проверяем редирект по страницам Игр",  memoryTrainer::gameNavigationWithCollection
        );
        step("8. Удаляем курс", () ->
            home.deleteProduct(productName)
        );
    }


//    @Test
    @Order(8)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Расчёт матрицы'")
    void testingRedirectsForMatrixCalculation() {
        String productName = "матрица судьбы";
        String dashboard = "Расчет матрицы";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'Расчёт матрицы'", new MatrixCalculation()::clickFromMatrixCalculation
        );
        step("7. Проверяем работу внутри дашборда", new MatrixCalculation()::openModalWindowInTheDashboard
        );
        step("8. Удаляем курс", () ->
            home.deleteProduct(productName)
        );
    }

//    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Учет каналов'")
    void testingRedirectsForChannelAccounting() {
        String productName = "таргетолог";
        String dashboard = "Учет каналов";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'Учет каналов'", new ChannelAccounting()::clickFromChannelAccounting
        );
        step("7. Проверяем работу внутри дашборда", new ChannelAccounting()::fillForm
        );
        step("8. Удаляем курс", () ->
            home.deleteProduct(productName)
        );
    }

//    @Test
    @Order(10)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Таро'")
    void testingRedirectsForTaro() {
        String productName = "таро";
        String dashboard = "Таро";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'Таро'", taro::clickFromTaro
        );
        step("7. Проверяем работу внутри дашборда", taro::mealNavigationWithCollection
        );
        step("8. Удаляем курс", () ->
            home.deleteProduct(productName)
        );
    }

//    @Test
    @Order(11)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Тестирование редиректов страниц дашборда 'Редактор карточек товара'")
    void testingRedirectsForProductCardEdition() {
        String productName = "авито";
        String dashboard = "Редактор карточек товара";
        chekingInsideTheDashboard(productName, dashboard);
        step("6. Переходим в дашборд 'Редактор карточек товара'", productCardEditor::clickFromProductCardEdition
        );
        step("7. Проверяем работу внутри дашборда", productCardEditor::workDashboard
        );
        step("8. Удаляем курс", () ->
            home.deleteProduct(productName)
        );
    }
}
