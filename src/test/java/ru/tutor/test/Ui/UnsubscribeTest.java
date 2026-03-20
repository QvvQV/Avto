package ru.tutor.test.Ui;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import ru.tutor.constants.AddressPage;
import ru.tutor.page.*;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.modals.GreetingsModal;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.page.unsubscribe.LiteUnsubscribePage;
import ru.tutor.page.unsubscribe.PremiumUnsubscribePage;
import ru.tutor.base.BaseUiTest;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

@Tag("UI")
@Story("Отписка пользователя от платформы")
public class UnsubscribeTest extends BaseUiTest {

    private static final TtPage ttPage = new TtPage();
    private final AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    private final CloudPage cloudPage = new CloudPage();
    private final SberPage sberPage = new SberPage();
    private final ru.tutor.flow.SubscriptionTestFlow flow = new ru.tutor.flow.SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);
    SubscriptionTestFlow flow1 = new SubscriptionTestFlow();

    @BeforeAll
    public static void setup() {
        setupBrowserWithoutIncognito();
    }

    @AfterEach
    protected void tearDown() {
        closeWebDriver();
    }


    @Test
//    @RepeatedTest(3)
    @Severity(SeverityLevel.CRITICAL)
    @Order(1)
    @DisplayName("✅ Успешное отписка пользователя от Премиума и Лайта")
    void successfulUnsubscriptionOfAUserFromPremiumAndLite() {

        flow.purchasePremiumSubscription();

        step("3. Обработка редиректа после оплаты",
                RedirectPage::redirectPage
        );
        step("4. Прохождение приветсвтенного onboarding", () -> {
            flow1.completeOnbording();
        });
        navigateToPage(PageType.PREMIUM_UNSUBSCRIBE, 5);

        step("6. Полный процесс отмены Premium подписки",
                PremiumUnsubscribePage::completePremiumUnsubscription
        );

        navigateToPage(PageType.LITE_UNSUBSCRIBE, 7);

        step("8. Полный процесс отмены Lite подписки",
                LiteUnsubscribePage::completeLiteUnsubscription
        );
    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Order(2)
    @DisplayName("✅ Успешная отписка Рекурентного пользователя от Премиума и Лайта")
    void successfulUnsubscriptionRecurringOfAUserFromPremiumAndLite() {

        flow.purchasePremiumSubscription();

        step("3. Обработка редиректа после оплаты",
                RedirectPage::redirectPage
        );

        flow1.completeOnbording()
                .processRecurrentInAdmin()
                .cancelPremiumSubscription();
    }


    private static class SubscriptionTestFlow {

        public SubscriptionTestFlow completeOnbording() {
            step("Завершение onbording процесса", () -> {
                GreetingsModal.fillAccessInputAllForm();
                AllureAttachments.attachText("Onbording", "Приветсвенные модули завершены");
                AllureAttachments.attachScreenshot("Скриншот главного экрана");
            });

            return this;
        }


        public SubscriptionTestFlow processRecurrentInAdmin() {
            step("Проведение рекуррентного платежа через админку", () -> {
                // Сохраняем текущее окно (ЛК клиента)
                String originalWindow = WebDriverRunner.getWebDriver().getWindowHandle();

                try {
                    processRecurrentInAdmin();

                } finally {
                    step("Возврат в ЛК клиента", () -> {
                        closeWindow();
                        switchTo().window(originalWindow);
                        AllureAttachments.attachText("Возврат", "Успешный вовзрат в ЛК клиента");
                    });
                }

            });
            return this;

        }

        public SubscriptionTestFlow cancelPremiumSubscription() {
            step("Отмена Premium подписки", () -> {
                open(AddressPage.SUBSCRIPTION_SETTINGS);
                WaitHelper.waitForPageLoad();
                WaitHelper.waitForAnimationComplete();
                AllureAttachments.attachScreenshot("Premium");
            });
            return this;
        }

        private void switchToNewWindow(String originalWindow) {
            for (String windowHadle : WebDriverRunner.getWebDriver().getWindowHandles()) {
                if (!windowHadle.equals(originalWindow)) {
                    switchTo().window(windowHadle);
                    WaitHelper.waitForPageLoad();
                    break;
                }
            }
        }
    }

    public enum PageType {
        PREMIUM_UNSUBSCRIBE("Премиум", AddressPage.SUBSCRIPTION_SETTINGS),
        LITE_UNSUBSCRIBE("Лайт", AddressPage.PAYMENTS_SETTINGS);

        private final String name;
        private final String url;

        PageType(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }

    // Метод в классе
    protected void navigateToPage(PageType pageType, int stepNumber) {
        step(stepNumber + ". Переход к отпискам " + pageType.getName(), () -> {
            open(pageType.getUrl());
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Страница отписки от " + pageType.getName());
        });
    }
}