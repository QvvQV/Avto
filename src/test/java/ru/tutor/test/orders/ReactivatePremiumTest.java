package ru.tutor.test.orders;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.unsubscribe.LiteUnsubscribePage;
import ru.tutor.base.RecurrentPaymentTestBase;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.SubscriptionTestFlow;
import ru.tutor.page.CloudPage;
import ru.tutor.page.RedirectPage;
import ru.tutor.page.AddressPage;
import ru.tutor.page.modals.*;
import ru.tutor.steps.AdminToUserLoginSteps;
import ru.tutor.steps.ReactivatePremiumSteps;
import ru.tutor.steps.OrderToggleSubscriptionSteps;
import ru.tutor.steps.UrlModificationSteps;
import ru.tutor.test.executor.RecurrentPaymentTestExecutor;
import ru.tutor.utils.WaitHelper;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;

// TP-6415 (Реализовать работу ссылок типа affrid в личном кабинете) и TP-6417 (Переработать восстановление премиума по кнопке)
public class ReactivatePremiumTest extends RecurrentPaymentTestBase {

    private RecurrentPaymentTestExecutor testExecutor;
    private final OrderToggleSubscriptionSteps orderSteps = OrderToggleSubscriptionSteps.withBaseUrl(TestConfig.getApiBaseUrl());
    private final ReactivatePremiumSteps reactivatePremiumSteps = ReactivatePremiumSteps.withBaseUrl(TestConfig.getApiBaseUrl());
    private final Integer TEST_ORDER_ID = 180;
    private final SubscriptionTestFlow flow = new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);
    private final AdminToUserLoginSteps adminToUserLoginSteps = new AdminToUserLoginSteps();


    @Epic("Отписка от подписки")
    @Override
    protected void initializePaymentCycleExecutor() {
        super.initializePaymentCycleExecutor();
        testExecutor = new RecurrentPaymentTestExecutor(
                orderDatesPage,
                adminOrderDetails,
                adminOrdersPage,
                paymentCycleExecutor
        );

    }

    @Tag("UI" + "API")
    @Test
    @DisplayName("Восстановление премиума по ссылке из письма внутренней почты")
    void restoreYourPremiumAccountUsingAnInternalEmail() {
        testExecutor.executeTwoPaymentTest(
                "Cloud Premium",
                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage).purchasePremiumSubscriptionAffRid(),
                SubscriptionTestFlow::processRecurrentInAdmin,
                context, 1
        );
        step("Перейти в ЛК пользователя", () -> {
            WaitHelper.waitForAnimationComplete();
            adminOrdersPage.goToUserPersonalAccount();
        });

        step("Добавить аффилейт-параметр", UrlModificationSteps::addAffiliateParameter
        );

        step("Выполнить отписку до тарифа Лайт", () -> {
            String token = AdminToUserLoginSteps.switchToTabAndGetToken();
            step("Отписка до Лайта: POST /user/orders/toggle_subscription/" + TEST_ORDER_ID, () ->
                    orderSteps.toggleOrderSubscription(token, TEST_ORDER_ID)
            );
            step("Реактивация подписки по внутренней почте: POST /user/orders/reactivate-premium", () ->
                    reactivatePremiumSteps.reactivatePremium(token)
            );
        });
        step("Проверяем aff_rid метку созданного заказа", () -> adminOrdersPage.renewal()
        );
    }

    @Tag("UI" + "API")
    @Test
    @DisplayName("Восстановление премиума по ссылке из email-рассылки")
    void restoreYourPremiumAccountUsingLinkFromEmailNewsletter() {
        testExecutor.executeTwoPaymentTest(
                "Cloud Premium",
                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage).purchasePremiumSubscriptionAffRid(),
                SubscriptionTestFlow::processRecurrentInAdmin,
                context,1
        );
        step("Перейти в ЛК пользователя", () -> {
            WaitHelper.waitForAnimationComplete();
            adminOrdersPage.goToUserPersonalAccount();
        });

        step("Выполнить отписку до тарифа Лайт", () -> {
            String token = AdminToUserLoginSteps.switchToTabAndGetToken();
            step("Отписка до Лайта: POST /user/orders/toggle_subscription/" + TEST_ORDER_ID, () ->
                    orderSteps.toggleOrderSubscription(token, TEST_ORDER_ID)
            );
            step("Добавить аффилейт-параметр + ссылки из email-рассылки", UrlModificationSteps::addAffiliateParameterByEmail);
            step("Реактивация подписки по внутренней почте: POST /user/orders/reactivate-premium", () ->
                    reactivatePremiumSteps.reactivatePremiumByEmail(token)
            );
        });
        step("Проверяем aff_rid метку созданного заказа", () -> adminOrdersPage.renewal()
        );
    }

    @Tag("UI" + "API")
    @Test
    @DisplayName("Восстановление Премиума по ссылке из email-рассылки отменённого заказа из Админки")
    void restorePremiumCancelAdmin() {
        testExecutor.executeTwoPaymentTest(
                "Cloud Premium",
                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage).purchasePremiumSubscriptionAffRid(),
                SubscriptionTestFlow::processRecurrentInAdmin,
                context, 1
        );
        step("Перейти в ЛК пользователя после отмены заказа через Админку", () ->
                adminOrdersPage.cancelOrderWithAdmin()
        );
        step("Добавить аффилейт-параметр + ссылки из email-рассылки", UrlModificationSteps::addAffiliateParameterByEmail);
        step("Проводим оплату подписки из письма email-рассылки", adminToUserLoginSteps::sendEmailPay);
        CloudPage.clickCardPageBtnLK();
        step("Проверяем aff_rid метку созданного заказа", () -> adminOrdersPage.renewal()
        );
    }

    @Tag("UI" + "API")
    @Test
    @DisplayName("Восстановление Премиума по тарифу Пожизненный доступ")
    void restorePremiumLifetimeAccess() {
        flow.purchasePremiumSubscriptionAffRid();

        step("Обработка редиректа после оплаты", RedirectPage::redirectPage
        );
        step("Заполнение модального окна", GreetingsModal::fillAccessInput
        );
        product.beginProduct();
        unlockNextWeek.openUnlimitedAccess();
        CloudPage.clickCardPageBtnLK();
        flow.processUnlimitedInAdmin();
        step("Перейти в ЛК пользователя", () -> {
            WaitHelper.waitForAnimationComplete();
            adminOrdersPage.goToUserPersonalAccount();
        });
    }

    @Tag("UI" + "API")
    @Test
    @DisplayName("Оформление Renewall внутри ЛК")
    void renewalDesignInsideThePersonalAccount() {
        testExecutor.executeTwoPaymentTest(
                "Cloud Premium",
                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage).purchasePremiumSubscriptionAffRid(),
                SubscriptionTestFlow::processRecurrentInAdmin,
                context, 1
        );
        step("Перейти в ЛК пользователя", () -> {
            WaitHelper.waitForAnimationComplete();
            adminOrdersPage.goToUserPersonalAccount();
        });

        step("Добавить аффилейт-параметр", UrlModificationSteps::addAffiliateParameter
        );

        step("Выполнить отписку до тарифа Лайт", () -> {
                    String token = AdminToUserLoginSteps.switchToTabAndGetToken();
                    step("Отписка до Лайта: POST /user/orders/toggle_subscription/" + TEST_ORDER_ID, () ->
                            orderSteps.toggleOrderSubscription(token, TEST_ORDER_ID)
                    );
        });
        step("Заполнение приветсвенного модального окна", GreetingsModal::fillOutTwoFieldsWelcomeModal);
        step("Переход к отпискам", () -> {
                    open(AddressPage.PAYMENTS_SETTINGS);
                    WaitHelper.waitForPageLoad();
                     step("Вызов модального окна оплаты", LiteUnsubscribePage::clickSubscribe
                     );
        });
        CloudPage.clickCardPageBtnLK();
        step("Проверяем aff_rid метку созданного заказа", () -> adminOrdersPage.renewal()
        );
    }
//    }

// Основные ручки для отработки
    //        "user/orders/reactivate-premium" (done)
//        "resubscribe"
//        "reactivate-premium-by-email" (done)
//        "/user/orders/pay-lifetime-tariff"
//        + пропихнуть в каждую aff_rid ссылку

    /*
     * Сценарии
     *
     * А) Пользователь "Восстановление премиума по ссылке из письма внутренней почты"
     * Шаги:
        * 1. создать пользователя с aff_rid ссылкой
        * 2. перейти в админ, сверить запись Utm_метки
        * 3. провести рекуррент пользоваетлю
        * 4. отписать пользователя от Премиума в ЛК
        * 5. добавить aff_rid ссылку в адресную строку и активировать переподписку через внутреннюю Почту 'user/orders/reactivate-premium'
        * 6. вернутся в админ заказа
        * 7. сверить utm_метку, провести рекуррент
        * 8. проверить записи в offerum
     * _ОР:_ Лиду в офферум отбились сл-ие записи 1) Подписка 2) Регулярный платёж 3) Отписка до Лайта 4) Подписка 5) Регулярный платёж
     *
     * Б) Пользователь "Восстановление премиума по ссылке из email-рассылки"
     * Шаги:
         * 1. создать пользователя с aff_rid ссылкой
         * 2. перейти в админ, сверить запись Utm_метки
         * 3. провести рекуррент пользоваетлю
         * 4. отписать пользователя от Премиума в ЛК
         * 5. добавить aff_rid ссылку в адресную строку и активировать переподписку через ссылке из email-рассылки `recovery_from_email_pitanie_v1`
         `reactivate-premium-by-email`
         * 6. вернутся в админ заказа
         * 7. сверить utm_метку, провести рекуррент
         * 8. проверить записи в offerum
     * _ОР:_ Лиду в офферум отбились сл-ие записи 1) Подписка 2) Регулярный платёж 3) Отписка до Лайта 4) Подписка 5) Регулярный платёж
     *
     * В) Пользователь "Восстановление премиума по ссылке из email-рассылки"
     * Шаги:
        * 1. создать пользователя с aff_rid ссылкой
        * 2. перейти в админ, сверить запись Utm_метки
        * 3. провести рекуррент пользоваетлю
        * 4. отписать пользователя от Премиума в админ
        * 5. Перейти в ЛК, добавить aff_rid ссылку в адресную строку и активировать переподписку через ссылке из email-рассылки `recovery_from_email_pitanie_v1`
     *          `resubscribe`
        * 6. вернутся в админ заказа
        * 7. сверить utm_метку, провести рекуррент
        * 8. проверить записи в offerum
     * _ОР:_ Лиду в офферум отбились сл-ие записи 1) Подписка 2) Регулярный платёж 3) Отписка до Лайта 4) Подписка 5) Регулярный платёж
     */

}
