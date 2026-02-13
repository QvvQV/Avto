package ru.tutor.test;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import ru.tutor.unsubscribe.LiteUnsubscribePage;
import ru.tutor.unsubscribe.PremiumUnsubscribePage;
import ru.tutor.base.BaseUiTest;
import ru.tutor.page.CloudPage;
import ru.tutor.page.RedirectPage;
import ru.tutor.page.AddressPage;
import ru.tutor.page.dashbords.helloDash;
import ru.tutor.page.TtPage;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;


@Story("Отписка пользователя от платформы")
public class UnsubscribeTest extends BaseUiTest {

    private static final TtPage ttPage = new TtPage();

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

        step("1. Начало оформления заказа", () -> {
            open(AddressPage.urlMain);


            ttPage.clickBtnOpenFor();
            ttPage.clickPremium();
        });
        step("2. Заполнение данных и оплата", () -> {
            CloudPage.setDateModal();
            AllureAttachments.attachText("Email пользователя", CloudPage.atrEmail());

            CloudPage.clickCardPageBtn();
        });
        step("3. Обработка редиректа после оплаты",
                RedirectPage::redirectPage
        );
        step("4. Прохождение приветсвтенного onboarding", () -> {
            helloDash.fillingOutTheWelcomeWindow();
            AllureAttachments.attachText("Onbording", "Приветсвенные модули завершены");
            AllureAttachments.attachScreenshot("Скриншот главного экрана");
        });
        step("5. Переход к отпискам", () -> {
            open(AddressPage.SUBSCRIPTION_SETTINGS);
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Страница отписки от Премиума");
        });

        step("6. Полный процесс отмены Premium подписки",
            PremiumUnsubscribePage::completePremiumUnsubscription
        );
        step("7. Переход к отпискам", () -> {
            open(AddressPage.PAYMENTS_SETTINGS);
            WaitHelper.waitForPageLoad();
            WaitHelper.waitForAnimationComplete();
            AllureAttachments.attachScreenshot("Страница отписки от Лайта");
        });
        step("8. Полный процесс отмены Lite подписки",
            LiteUnsubscribePage::completeLiteUnsubscription
        );
    }


    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Order(2)
    @DisplayName("✅ Успешная отписка Рекурентного пользователя от Премиума и Лайта")
    void successfulUnsubscriptionRecurringOfAUserFromPremiumAndLite() {
        SubscriptionTestFlow flow = new SubscriptionTestFlow();

        flow.purchasePremiumSubscription()
                .completeOnbording()
                .processRecurrentInAdmin()
                .cancelPremiumSubscription();
    }


    private static class SubscriptionTestFlow {

        private String userEmail;

        public SubscriptionTestFlow purchasePremiumSubscription() {
            step("Оформление и оплата Premium подписки", () -> {
                open(AddressPage.urlMain);

                ttPage.clickBtnOpenFor();
                ttPage.clickPremium();

                CloudPage.setDateModal();

                userEmail = CloudPage.atrEmail();
                AllureAttachments.attachText("Email пользователя", userEmail);

                CloudPage.clickCardPageBtn();
                RedirectPage.redirectPage();
            });
            return this;
        }

        public SubscriptionTestFlow completeOnbording() {
            step("Завершение onbording процесса", () -> {
                helloDash.fillingOutTheWelcomeWindow();
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
}