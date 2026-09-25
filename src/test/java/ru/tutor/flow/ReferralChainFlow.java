package ru.tutor.flow;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.model.club.ClubLinkResponse;
import ru.tutor.model.club.ClubPayoutRequisitesRequest;
import ru.tutor.model.club.ClubPayoutSummaryResponse;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.steps.AccountSteps;
import ru.tutor.steps.AdminSteps;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.OrderSteps;
import ru.tutor.steps.club.ClubSteps;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReferralChainFlow {

    private static final int PREMIUM_PRICE = 399;
    private static final int PREMIUM_DAYS = 30;
    private static final List<Integer> CLUB_LINK_PRODUCT_IDS = List.of(220, 170, 259);

    private final PurchaseFlow purchaseFlow;
    private final CloudPage cloudPage;
    private final RenewalModalFlow renewalModalFlow;
    private final AdminSteps adminSteps;
    private final AuthSteps authSteps;
    private final AccountSteps accountSteps;
    private final ClubSteps clubSteps;

    public ReferralChainFlow(PurchaseFlow purchaseFlow, CloudPage cloudPage, RenewalModalFlow renewalModalFlow,
                             AdminSteps adminSteps, AuthSteps authSteps,
                             AccountSteps accountSteps, ClubSteps clubSteps) {
        this.purchaseFlow = purchaseFlow;
        this.cloudPage = cloudPage;
        this.renewalModalFlow = renewalModalFlow;
        this.adminSteps = adminSteps;
        this.authSteps = authSteps;
        this.accountSteps = accountSteps;
        this.clubSteps = clubSteps;
    }

    public record Parent(String email, String token, String linkUrl) {}

    public record Chain(Parent parent, Parent client2, Parent client3, Parent client4) {}

    public record PremiumMember(String email, String token) {}

    private record PremiumPurchase(String email, OrderSteps.PaymentCredentials credentials) {}

    private record ApprovedWithdrawal(String token, int withdrawalId) {}

    // ==================== ПОКУПКА ====================

    private PremiumPurchase createPremiumPurchase() {
        String email = step("Создание пользователя с тарифом Премиум на главной", () -> {
            PurchaseResult result = purchaseFlow.premium();
            return result.email();
        });
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);

        payPremiumInAdmin(email);
        return new PremiumPurchase(email, credentials);
    }

    private PremiumPurchase createLifePurchaseFromReferral(String referralUrl) {
        reopenByReferralLink(referralUrl) ;
        String email = step("Создание заказа Пожизненный по реферальной ссылке", () ->
                purchaseFlow.lifeReferral(referralUrl).email());
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);
        return new PremiumPurchase(email, credentials);
    }

    private PremiumPurchase createPremiumPurchaseFromReferral(String referralUrl) {
        reopenByReferralLink(referralUrl) ;
        String email = step("Создание заказа Премиум по реферальной ссылке", () ->
                purchaseFlow.premiumReferral(referralUrl).email());
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);
        return new PremiumPurchase(email, credentials);
    }

    private void payPremiumInAdmin(String email) {
        step("Оплата 399 в админке", () -> {
            adminSteps.login();
            String orderId = adminSteps.findOrderId(email);
            adminSteps.createPaymentAndValidateDates(orderId, PREMIUM_PRICE, PREMIUM_DAYS);
        });
    }

    private ApprovedWithdrawal createApprovedWithdrawal() {
        PremiumMember member = step("Создание Премиум-заказа + оплата 399 + шеринг",
                this::createPremiumMemberAndShare);
        clubSteps.getAndValidatePayoutSummary(member.token(), 1000, 1000, false, false);
        clubSteps.saveGeneratePayoutRequisites(member.token(), member.email());

        ClubLinkResponse link = clubSteps.createAndValidateClubLink(
                member.token(), CLUB_LINK_PRODUCT_IDS.getFirst(), member.email()
        );
        renewalModalFlow.logoutAfterTest();
        createLifePurchaseFromReferral(link.url());

        clubSteps.getAndValidatePayoutSummary(member.token(), 3000, 3000, true, true);
        Integer withdrawalId = step("Создание заявки на вывод 3000 (POST /user/club/v2/payouts)", () ->
                clubSteps.validatePayoutCreated(clubSteps.createPayout(member.token(), 3000)).id());

        step("Проверка статуса заявки в админке: new/Новая", () -> {
            adminSteps.login();
            adminSteps.validateWithdrawalStatus(adminSteps.getClubWithdrawal(withdrawalId), "new", "Новая");
        });

        return new ApprovedWithdrawal(member.token(), withdrawalId);
    }

    private Parent publishClubAfterPurchase(PremiumPurchase purchase, int linkProductId) {
        String token = authSteps.loginViaRefreshToken(purchase.email(), purchase.credentials().password());
        accountSteps.validateEditPasswordSuccess(
                accountSteps.editPassword(token, purchase.credentials().password(), TestConfig.getTestPassword()));
                clubSteps.shareRewardAndValidate(token);
                clubSteps.getAndValidatePayoutSummary(token, 1000, 1000, false, false);
                ClubLinkResponse link = clubSteps.createAndValidateClubLink(token, linkProductId, purchase.email());
                renewalModalFlow.logoutAfterTest();
                return new Parent(purchase.email(), token, link.url());
    }

    // ==================== ПУБЛИЧНЫЕ СЦЕНАРИИ ====================


    @Step("Закрытие браузера и открытие по созданной ссылке (новая сессия)")
    private void reopenByReferralLink(String referralUrl) {
        step("Закрытие браузера и сброс сессии", Selenide::closeWebDriver);
        step("Открытие реферальной ссылки в новой сессии", () -> open(referralUrl));
    }

    @Step("Этап 1: Родитель - заказ Премиум + оплата 399 в админке + ссылка")
    public Parent createParentAndLink() {

        return publishClubAfterPurchase(createPremiumPurchase(), CLUB_LINK_PRODUCT_IDS.getFirst());
    }

    @Step("Этап 2: Клиент - открытие ссылки Родителя + заказ Пожизненный")
    public Parent createClientFromReferralLink(String parentLinkUrl) {

        return publishClubAfterPurchase(createLifePurchaseFromReferral(parentLinkUrl), CLUB_LINK_PRODUCT_IDS.get(1));
    }

    @Step("Этап 3: Клиент - открытие ссылки 2-го клиента + заказ Премиум + оплата 399 в админке")
    public Parent createPremiumClientFromReferralLink(String referralUrl) {

        PremiumPurchase purchase = createPremiumPurchaseFromReferral(referralUrl);
        payPremiumInAdmin(purchase.email());
        return publishClubAfterPurchase(purchase, CLUB_LINK_PRODUCT_IDS.get(2));
    }

    @Step("Этап 5: Проверка баланса Родителя (ожидается {expectedBalance})")
    public void verifyParentBalance(Parent parent, int expectedBalance) {

        clubSteps.getAndValidatePayoutSummary(parent.token(), expectedBalance, expectedBalance, false, false);
    }

    @Step("Создание Премиум-заказа + оплата 399 в админке + шеринг")
    public PremiumMember createPremiumMemberAndShare() {

        PremiumPurchase purchase = createPremiumPurchase();
        String token = authSteps.loginViaRefreshToken(purchase.email(), purchase.credentials().password());
        clubSteps.shareRewardAndValidate(token);
        return new PremiumMember(purchase.email(), token);
    }

    @Step("Заполнение реквизитов ИП и проверка ручки summary")
    public void fillRequisitesAndVerifySummary() {

        PremiumMember member = step("Создание Премиум-заказа + шеринг",
                this::createPremiumMemberAndShare);

        clubSteps.getAndValidatePayoutSummary(member.token(), 1000, 1000, false, false);

        ClubPayoutRequisitesRequest request = clubSteps.saveGeneratePayoutRequisites(member.token(), member.email());
        clubSteps.validateRequisitesMatchRequest(clubSteps.getPayoutRequisites(member.token()), request);

        clubSteps.getAndValidatePayoutSummary(member.token(), 1000, 1000, true, false);
        clubSteps.validateRequisitesDeleted(clubSteps.deletePayoutRequisites(member.token()));
        clubSteps.validateRequisitesAbsent(clubSteps.getPayoutRequisites(member.token()));

        clubSteps.getAndValidatePayoutSummary(member.token(), 1000, 1000, false, false);
    }

    @Step("Заявка на вывод + отклонение в админке (безопасный возврат)")
    public void createWithdrawalAndReject() {
        ApprovedWithdrawal withdrawal = createApprovedWithdrawal();

        step("Отклонение заявки (POST reject, returnBonus=true, 204)",
                () -> adminSteps.rejectClubWithdrawal(withdrawal.withdrawalId(), true));
        step("Проверка статуса заявки после отклонения: rejected/Отклонена", () ->
                adminSteps.validateWithdrawalStatus(adminSteps.getClubWithdrawal(withdrawal.withdrawalId()),
                        "rejected", "Отклонена"));
        clubSteps.getAndValidatePayoutSummary(withdrawal.token(), 3000, 3000, true, true);
    }

    @Step("Заявка на вывод + отклонение в админке (не возврат)")
    public void createWithdrawalAndRejectNotReturnBonus() {
        ApprovedWithdrawal withdrawal = createApprovedWithdrawal();

        step("Отклонение заявки (POST reject, returnBonus=false, 204)",
                () -> adminSteps.rejectClubWithdrawal(withdrawal.withdrawalId(), false));
        step("Проверка статуса заявки после отклонения: rejected/Отклонена", () ->
                adminSteps.validateWithdrawalStatus(adminSteps.getClubWithdrawal(withdrawal.withdrawalId()),
                        "rejected", "Отклонена"));
        clubSteps.getAndValidatePayoutSummary(withdrawal.token(), 0, 0, true, false);
    }

    @Step("Согласование заявки статус 'Оплачено'")
    public void withdrawalPayment(){
        ApprovedWithdrawal withdrawal = createApprovedWithdrawal();

        step("Согласование заявки (POST withdrawal, isPaid=true, 201)",
                () -> adminSteps.withdrawalPayment(withdrawal.withdrawalId(), true));
        step("Проверка статуса заявки после перевода в статус 'Оплачено'", () ->
                adminSteps.validateWithdrawalStatus(adminSteps.getClubWithdrawal(withdrawal.withdrawalId()),
                        "paid", "Выплачено"));
        clubSteps.getAndValidatePayoutSummary(withdrawal.token(), 0, 0, true, false);
    }

    @Step("Заявка на вывод + удаление её из админки (возврат средств)")
    public void createWithdrawalAndDelete() {
        ApprovedWithdrawal withdrawal = createApprovedWithdrawal();

        step("Удаление заявки (DELETE, returnBonus=true, 204)",
                () -> adminSteps.deleteClubWithdrawal(withdrawal.withdrawalId(), true));
        clubSteps.getAndValidatePayoutSummary(withdrawal.token(), 3000, 3000, true, true);
    }

    @Step("Заявка на вывод + удаление её из админки (не возвращать средства)")
    public void createWithdrawalAndDeleteNotReturnBonus() {
        ApprovedWithdrawal withdrawal = createApprovedWithdrawal();

        step("Удаление заявки (DELETE, returnBonus=false, 204)",
                () -> adminSteps.deleteClubWithdrawal(withdrawal.withdrawalId(), false));
        clubSteps.getAndValidatePayoutSummary(withdrawal.token(), 0, 0, true, false);
    }

    @Step("Построение цепочки из 4 участников")
    public Chain buildChain() {
        Parent parent = createParentAndLink();
        Parent client2 = createClientFromReferralLink(parent.linkUrl());
        Parent client3 = createPremiumClientFromReferralLink(client2.linkUrl());
        Parent client4 = createClientFromReferralLink(client3.linkUrl());
        return new Chain(parent, client2, client3, client4);
    }

    @Step("Проверка участников цепочки")
    public void validateChainMembers(Chain chain) {
        step("Проверка созданных участников цепочки", () -> {
            assertNotNull(chain.parent().email(), "email Родителя не должен быть null");
            assertFalse(chain.parent().linkUrl().isBlank(), "Ссылка Родителя не должна быть пустой");
            assertNotNull(chain.client2().email(), "email Клиента 2 не должен быть null");
            assertFalse(chain.client2().linkUrl().isBlank(), "Ссылка Клиента 2 не должна быть пустой");
            assertNotNull(chain.client3().email(), "email Клиента 3 не должен быть null");
            assertFalse(chain.client3().linkUrl().isBlank(), "Ссылка Клиента 3 не должна быть пустой");
            assertNotNull(chain.client4().email(), "email Клиента 4 не должен быть null");
            assertFalse(chain.client4().linkUrl().isBlank(), "Ссылка Клиента 4 не должна быть пустой");

            Allure.addAttachment("Родитель (email)", chain.parent().email());
            Allure.addAttachment("Реферальная ссылка Родителя", chain.parent().linkUrl());
            Allure.addAttachment("Клиент 2 (email)", chain.client2().email());
            Allure.addAttachment("Реферальная ссылка Клиента 2", chain.client2().linkUrl());
            Allure.addAttachment("Клиент 3 (email)", chain.client3().email());
            Allure.addAttachment("Реферальная ссылка Клиента 3", chain.client3().linkUrl());
            Allure.addAttachment("Клиент 4 (email)", chain.client4().email());
            Allure.addAttachment("Реферальная ссылка Клиента 4", chain.client4().linkUrl());
        });
    }

    // semi - создаёт ссылки / получает по ним отчисления / финансы не может вывести / оплата club_balance
}