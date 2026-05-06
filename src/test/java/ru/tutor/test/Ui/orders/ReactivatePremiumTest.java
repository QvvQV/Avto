package ru.tutor.test.Ui.orders;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.tutor.flow.ReactivatePremiumFlow;
import ru.tutor.base.RecurrentPaymentTestBase;
import ru.tutor.flow.SubscriptionTestFlow;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.modals.*;

// TP-6415 (Реализовать работу ссылок типа affrid в личном кабинете) и TP-6417 (Переработать восстановление премиума по кнопке)
public class ReactivatePremiumTest extends RecurrentPaymentTestBase {

    private ReactivatePremiumFlow reactivateFlow;
    private final SubscriptionTestFlow flow = new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);

    @Epic("Отписка от подписки")
    @Override
    protected void initializePaymentCycleExecutor() {
        super.initializePaymentCycleExecutor();

        this.reactivateFlow = new ReactivatePremiumFlow(
                ttPage, cloudPage, sberPage, adminOrdersPage, adminOrderDetails,
                orderDatesPage, paymentCycleExecutor, context
        );
    }

    @Test
    @Tag("UI" + "API")
    @Tag("aff_rid / aff_rid")
    @DisplayName("Восстановление премиума по ссылке из письма внутренней почты")
    void restoreYourPremiumAccountUsingAnInternalEmail() {
        reactivateFlow
                .createUserWithAffRidAndRecurrent()
                .goToUserPersonalAccount()
                .addAffiliateParameter()
                .fetchAuthToken()
                .downgradeToLite()
                .reactivatePremiumWithAffRid(reactivateFlow.getAffRid())
                .verifyAffRid();
    }

    @Test
    @Tag("UI" + "API")
    @Tag("aff_rid / aff_rid")
    @DisplayName("Восстановление премиума по ссылке из email-рассылки")
    void restoreYourPremiumAccountUsingLinkFromEmailNewsletter() {
        reactivateFlow
                .createUserWithAffRidAndRecurrent()
                .goToUserPersonalAccount()
                .downgradeToLite()
                .addAffiliateParameterFromEmail()
                .reactivatePremiumByEmail()
                .verifyAffRid();
    }

    @Test
    @Tag("UI" + "API")
    @Tag("aff_rid / aff_rid")
    @DisplayName("Восстановление Премиума по ссылке из email-рассылки отменённого заказа из Админки ")
    void restorePremiumCancelAdmin() {
        reactivateFlow
                .createUserWithAffRidAndRecurrent()
                .cancelOrderInAdmin()
                .addAffiliateParameterFromEmail()
                .payFromEmailNewsletter()
                .verifyAffRid();
    }

//    @Tag("UI" + "API")
//    @Test
//    @DisplayName("Восстановление Премиума по ссылке из email-рассылки отменённого заказа из Админки (aff_rid / органика)")
//    void restorePremiumCancelAdminWithoutAffRid() {
//        reactivateFlow
//                .createUserWithAffRidAndRecurrent()
//                .cancelOrderInAdmin()
//                .removeAffiliateParameterFromEmail()
//                .payFromEmailNewsletter()
//                .verifyAffRid();
//    }


    @Test
    @Tag("UI" + "API")
    @Tag("aff_rid / aff_rid")
    @DisplayName("Оформление с aff_rid, восстановление Премиума по тарифу Пожизненный доступ без aff_rid")
    void restorePremiumlifeAccess() {
        flow.purchasePremiumSubscriptionAffRid();
        reactivateFlow.openFirstWeek();
        cloudPage.clickCardPageBtn();
        flow.processUnlimitedInAdmin();
        reactivateFlow.goToUserPersonalAccount();
    }

    @Test
    @Tag("UI" + "API")
    @Tag("aff_rid / aff_rid")
    @DisplayName("Оформление Renewal внутри ЛК c Aff_Rid в URL")
    void renewalDesignInsideThePersonalAccount() {
        reactivateFlow
                .createUserWithAffRidAndRecurrent()
                .goToUserPersonalAccount()
                .takeCashbackAndFillWelcomeWindow()
                .addAffiliateParameter()
                .downgradeToLite()
                .processRenewalInPersonalAccount();
    }

    @Test
    @Tag("UI" + "API")
    @Tag("органика/органика")
    @DisplayName("Восстановление премиума по ссылке из письма внутренней почты Без африда")
    void restoreYourPremiumAccountUsingAnInternalEmailWithoutAffRid() {
        reactivateFlow
                .createUserAndRecurrent()
                .goToUserPersonalAccount()
                .fetchAuthToken()
                .downgradeToLite()
                .reactivatePremium();
//                .verifyAffRid();
    }

    @Test
    @Tag("UI" + "API")
    @Tag("aff_rid / органика")
    @DisplayName("Оформление билла с потоковой ссылкой. Оформление реневалла без потоковой ссылки в url")
    void restoreYourPremiumAccountUsingWithAffRidRenewalWithoutAffRid() {
        reactivateFlow
                .createUserWithAffRidAndRecurrent()
                .goToUserPersonalAccount()
                .takeCashbackAndFillWelcomeWindow()
                .fetchAuthToken()
                .downgradeToLite()
                .processRenewalInPersonalAccount()
                .verifyAffRid();
    }

// Основные ручки для отработки
    //        "user/orders/reactivate-premium" (done)
//        "resubscribe"
//        "reactivate-premium-by-email" (done)
//        "/user/orders/pay-lifetime-tariff"
//        + пропихнуть в каждую aff_rid ссылку

    /*
     * Сценарии
     *
     * А) "Восстановление премиума по ссылке из письма внутренней почты"
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
     * Б) "Восстановление премиума по ссылке из email-рассылки"
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
     * В) "Восстановление премиума по ссылке из email-рассылки"
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

    /*
    Заказ по органике, реневал по органике. Ожидается: Премиум на сисак в воронку органики, реневал на сисак в воронку органики
Заказ с афридом, реневал по органике. Ожидается: Премиум на веба в воронку африда, реневал на сисак в воронку органики
Заказ по органике, реневал с афридом (из таски 6417). Ожидается: Премиум на сисак в воронку органики, реневал на веба в воронку африда
Заказ с афридом, реневал с афридом (из таски 6417). Ожидается: Премиум на веба в воронку африда, реневал на веба в воронку африда
     */

}
