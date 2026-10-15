package ru.tutor.flow.club;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.purchase.PurchaseFlow;
import ru.tutor.flow.purchase.PurchaseResult;
import ru.tutor.flow.account.RenewalModalFlow;
import ru.tutor.model.admin.PaymentType;
import ru.tutor.model.club.ClubLinkResponse;
import ru.tutor.model.club.ClubPayoutRequisitesRequest;
import ru.tutor.model.club.ClubReferralsResponse;
import ru.tutor.model.club.ClubWithdrawalResponse;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.steps.account.AccountSteps;
import ru.tutor.steps.admin.AdminSteps;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.steps.club.ClubSteps;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class ReferralChainFlow {

    private static final int PREMIUM_PRICE = 399;
    private static final int PREMIUM_DAYS = 30;
    private static final int WELCOME_BONUS = 1000;
    private static final int AFTER_LIFE = 3000;
    private static final int AFTER_LIFE_PREMIUM_PRICE = 2601;
    private static final List<Integer> CLUB_LINK_PRODUCT_IDS = List.of(220, 170, 259);
    private static final int REFERRALS_LIMIT = 10;
    private static final int REFERRALS_PAGE = 1;
    private static final int EXPECTED_REFERRALS_TOTAL = 3;
    private static final int EXPECTED_PREMIUM_REFERRALS =1;
    private static final int EXPECTED_LIFETIME_REFERRALS = 2;

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

    public record Parent(String email, String token, String linkUrl) {
        public Parent {
            Objects.requireNonNull(email, "email Родителя не должен быть null");
            Objects.requireNonNull(token, "token Родителя не должен быть null");
            Objects.requireNonNull(linkUrl, "linkUrl Родителя не должен быть null");
            if (linkUrl.isBlank()) {
                throw new IllegalArgumentException("linkUrl Родителя не должна быть пустой");
            }
        }
    }

    public record Chain(Parent parent, Parent client2, Parent client3, Parent client4) {
        public Chain {
            Objects.requireNonNull(parent, "parent не должен быть null");
            Objects.requireNonNull(client2, "client2 не должен быть null");
            Objects.requireNonNull(client3, "client3 не должен быть null");
            Objects.requireNonNull(client4, "client4 не должен быть null");
        }
    }

    public record PremiumMember(String email, String token) {
    }

    private record PremiumPurchase(String email, OrderSteps.PaymentCredentials credentials) {
    }

    private record ApprovedWithdrawal(String token, int withdrawalId) {
    }

    // ==================== ПОКУПКА ====================

    private PremiumPurchase createPremiumPurchase() {
        PremiumPurchase purchase = createPremiumOrder();
        payPremiumInAdmin(purchase.email());
        return purchase;
    }

    private PremiumPurchase createPremiumOrder() {
        String email = step("Создание пользователя с тарифом Премиум на главной", () -> {
            PurchaseResult result = purchaseFlow.premium();
            return result.email();
        });
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);
        return new PremiumPurchase(email, credentials);
    }

    @Step("Закрытие браузера и открытие по созданной ссылке (новая сессия)")
    private void reopenByReferralLink(String referralUrl) {
        step("Закрытие браузера и сброс сессии", Selenide::closeWebDriver);
        step("Открытие реферальной ссылки в новой сессии", () -> open(referralUrl));
    }

    private PremiumPurchase createLifePurchaseFromReferral(String referralUrl) {
        reopenByReferralLink(referralUrl);
        String email = step("Создание заказа Пожизненный по реферальной ссылке", () ->
                purchaseFlow.lifeReferral(referralUrl).email());
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);
        return new PremiumPurchase(email, credentials);
    }

    private PremiumPurchase createPremiumPurchaseFromReferral(String referralUrl) {
        reopenByReferralLink(referralUrl);
        String email = step("Создание заказа Премиум по реферальной ссылке", () ->
                purchaseFlow.premiumReferral(referralUrl).email());
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);
        return new PremiumPurchase(email, credentials);
    }

    private String payPremiumInAdmin(String email) {
        return payPremiumInAdmin(email, PaymentType.RECURRING);
    }

    private String payPremiumInAdmin(String email, PaymentType expectedType) {
        return step("Оплата 399 в админке + проверка типа платежа", () -> {
            adminSteps.login();
            String orderId = adminSteps.findFirstOrderId(email);
            adminSteps.createPaymentAndValidateDates(orderId, PREMIUM_PRICE, PREMIUM_DAYS);
            step("Проверка типа платежа 399 (" + expectedType + ")", () ->
                    adminSteps.validatePaymentType(orderId, PREMIUM_PRICE, expectedType));
            return orderId;
        });
    }

    private ApprovedWithdrawal createApprovedWithdrawal() {
        PremiumPurchase purchase = createPremiumPurchase();
        String token = prepareMemberWithLife(purchase, true);
//        Integer withdrawalId = step("Создание заявки на вывод 3000 (POST /user/club/v2/payouts)", () ->
//                clubSteps.validatePayoutCreated(clubSteps.createPayout(token, AFTER_LIFE)).id());

        ClubWithdrawalResponse created = step("Создание заявки на вывод 3000 (POST /user/club/v2/payouts)", () ->
                clubSteps.validatePayoutCreated(clubSteps.createPayout(token,AFTER_LIFE)));
        step("Read-back: заявка присутствует в списке (GET /user/club/v2/ payouts)", () ->
        clubSteps.validatePayoutPresent(clubSteps.validatePayoutsResponse(clubSteps.getPayouts(token)),
                created));

        Integer withdrawalId = created.id();

        step("Проверка статуса заявки в админке: new/Новая", () -> {
            adminSteps.login();
            adminSteps.validateWithdrawalStatus(adminSteps.getClubWithdrawal(withdrawalId), "new", "Новая");
        });

        return new ApprovedWithdrawal(token, withdrawalId);
    }

    private void fillRequisitesAndVerifySummary(PremiumMember member, String typeLabel,
                                                Function<String, ClubPayoutRequisitesRequest> saveRequisites,
                                                BiConsumer<Response, ClubPayoutRequisitesRequest> validateRequisites) {
        clubSteps.getAndValidatePayoutSummary(member.token(), WELCOME_BONUS, WELCOME_BONUS, false, false);

        step("Заполнение реквизитов " + typeLabel, () -> {
            ClubPayoutRequisitesRequest request = saveRequisites.apply(member.email());
            validateRequisites.accept(clubSteps.getPayoutRequisites(member.token()), request);
        });

        clubSteps.getAndValidatePayoutSummary(member.token(), WELCOME_BONUS, WELCOME_BONUS, true, false);

        step("Удаление реквизитов", () -> {
            clubSteps.validateRequisitesDeleted(clubSteps.deletePayoutRequisites(member.token()));
            clubSteps.validateRequisitesAbsent(clubSteps.getPayoutRequisites(member.token()));
        });

        clubSteps.getAndValidatePayoutSummary(member.token(), WELCOME_BONUS, WELCOME_BONUS, false, false);
    }

    @Step("Премиум (no pay): смена пароля + шеринг + реквизиты + ссылка + Пожизненный (semi)")
    private PremiumMember createSemiMemberWithLife() {
        PremiumPurchase purchase = createPremiumOrder();

        String token = prepareMemberWithLife(purchase, false);
        return new PremiumMember(purchase.email(), token);
    }

    private String prepareMemberWithLife(PremiumPurchase purchase, boolean expectFull) {
        String token = loginAndChangePasswordAndShare(purchase);
        if (expectFull) {
            clubSteps.getAndValidatePayoutSummary(token, WELCOME_BONUS, WELCOME_BONUS, false, false);
        } else {
            clubSteps.getAndValidatePayoutSummarySemi(token, WELCOME_BONUS, WELCOME_BONUS, false, false);
        }
        clubSteps.saveGeneratePayoutRequisites(token, purchase.email());
        ClubLinkResponse link = clubSteps.createAndValidateClubLink(
                token, CLUB_LINK_PRODUCT_IDS.getFirst(), purchase.email()
        );
        renewalModalFlow.logoutAfterTest();
        createLifePurchaseFromReferral(link.url());
        if (expectFull) {
            clubSteps.getAndValidatePayoutSummary(token, AFTER_LIFE, AFTER_LIFE, true, true);
        } else {
            clubSteps.getAndValidatePayoutSummarySemi(token, AFTER_LIFE, AFTER_LIFE, true, false);
        }
        return token;
    }

    private String loginAndChangePasswordAndShare(PremiumPurchase purchase) {
        String token = authSteps.loginViaRefreshToken(purchase.email(), purchase.credentials().password());
        accountSteps.validateEditPasswordSuccess(
                accountSteps.editPassword(token, purchase.credentials().password(), TestConfig.getTestPassword())
        );
        clubSteps.shareRewardAndValidate(token);
        return token;
    }

    // ==================== ПУБЛИЧНЫЕ СЦЕНАРИИ ====================

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

    @Step("Этап 6: Проверка списка рефералов Родителя (3 реферала: 1 premium + 2 lifetime)")
    public void verifyParentReferrals(Chain chain) {
        ClubReferralsResponse referrals = clubSteps.getAndValidateClubReferrals(
                chain.parent().token(), REFERRALS_LIMIT, REFERRALS_PAGE);
        step("Сверка состава рефералов Родителя", () ->
                assertAll("Состава рефералов Родителя",
                        () -> assertEquals(EXPECTED_REFERRALS_TOTAL, referrals.pagination().total(),
                                "pagination.total должен быть 3"),
                        () -> assertEquals(EXPECTED_REFERRALS_TOTAL, referrals.safeItems().size(),
                                "В items должно быть 3 реферала"),
                        () -> assertEquals(1, referrals.pagination().pages(),
                                "pagination.pages должен быть 1"),
                        () -> assertEquals(0, referrals.countWithoutSubscription(),
                                "Все рефералы цепочки должны иметь подписку"),
                        () -> assertEquals(EXPECTED_PREMIUM_REFERRALS, referrals.summary().premium(),
                                "summary.premium должен быть 1"),
                        () -> assertEquals(EXPECTED_LIFETIME_REFERRALS, referrals.summary().lifetime(),
                                "summary.lifetime должен быть 2"),
                        () -> assertEquals(0, referrals.summary().trial(),
                                "summary.trial должен быть 0"),
                        () -> assertEquals(0, referrals.summary().lite(),
                                "summary.lite должен быть 0"),
                        () -> assertEquals(0, referrals.summary().paymentError(),
                                "summary.paymentError должен быть 0"),
                        () -> assertEquals(0, referrals.summary().unsubscribed(),
                                "summary.unsubscribed должен быть 0"),
                        () -> assertEquals(referrals.summary().total(), referrals.pagination().total(),
                                "Без рефералов без подписки summary.total должен совпадать с pagination.total")));

        step("Проверка структуры, сортировки и сверки summary с items", () -> {
            clubSteps.validateReferralsItems(referrals.items());
            clubSteps.validateReferralsSortedByIncomePerMonthDesc(referrals.items());
            clubSteps.validateReferralsSummaryMatchesItems(referrals);
        });

        step("Проверка активности подписок и ссылок рефералов", () ->
                assertAll("Статусы и ссылки рефералов",
                        () -> assertTrue(referrals.safeItems().stream()
                                        .allMatch(item -> item.subscriptionStatus().
                                                isActiveSubscription()),
                                "Все рефералы цепочки должны иметь активную подписку"),
                        () -> assertEquals(EXPECTED_REFERRALS_TOTAL, referrals.
                                        linkNames().size(),
                                "У каждого реферала должна быть ссылка"),
                        () -> assertTrue(referrals.linkNames().stream()
                                        .allMatch(name -> name != null && !name.isBlank(
                                        ) && !"null".equals(name)),
                                "link.name должен быть непустым и не строкой \"null\""))
        );
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
        PremiumMember member = createPremiumMemberAndShare();
        fillRequisitesAndVerifySummary(member, "ИП",
                email -> clubSteps.saveGeneratePayoutRequisites(member.token(), email),
                clubSteps::validateRequisitesMatchRequest);
    }

    @Step("Премиум: смена пароля + шеринг (semi) + реквизиты + ссылка + Пожизненный по ссылке")
    public void premiumShareAndLifeSemi() {
        PremiumMember member = createSemiMemberWithLife();
        String freshToken = authSteps.loginViaRefreshToken(member.email(), TestConfig.getTestPassword());
        step("Попытка вывода средств: POST /user/club/v2/payouts (canRequest=false) -> 500", () ->
                clubSteps.validatePayoutRejected(clubSteps.createPayout(freshToken, AFTER_LIFE)));
    }

    private Parent publishClubAfterPurchase(PremiumPurchase purchase, int linkProductId) {
        String token = loginAndChangePasswordAndShare(purchase);
        clubSteps.getAndValidatePayoutSummary(token, WELCOME_BONUS, WELCOME_BONUS, false, false);
        ClubLinkResponse link = clubSteps.createAndValidateClubLink(token, linkProductId, purchase.email());
        renewalModalFlow.logoutAfterTest();
        return new Parent(purchase.email(), token, link.url());
    }

    @Step("Заполнение реквизитов СЗ и проверка ручки summary")
    public void fillSzRequisitesAndVerifySummary() {
        PremiumMember member = createPremiumMemberAndShare();
        fillRequisitesAndVerifySummary(member, "Самозанятого",
                email -> clubSteps.saveGeneratePayoutRequisitesSz(member.token(), email),
                clubSteps::validateSzRequisitesMatchRequest);
    }

    @Step("Заявка на вывод + отклонение в админке (безопасный возврат)")
    public void createWithdrawalAndReject() {
        ApprovedWithdrawal withdrawal = createApprovedWithdrawal();

        step("Отклонение заявки (POST reject, returnBonus=true, 204)",
                () -> adminSteps.rejectClubWithdrawal(withdrawal.withdrawalId(), true));
        step("Проверка статуса заявки после отклонения: rejected/Отклонена", () ->
                adminSteps.validateWithdrawalStatus(adminSteps.getClubWithdrawal(withdrawal.withdrawalId()),
                        "rejected", "Отклонена"));
        clubSteps.getAndValidatePayoutSummary(withdrawal.token(), AFTER_LIFE, AFTER_LIFE, true, true);
    }

    @Step("Премиум: смена пароля + шеринг + реквизиты + ссылка + Пожизненный + оплата 399 с баланса (type=19)")
    public void premiumShareAndLifePayFromClubBalance() {
        PremiumMember member = createSemiMemberWithLife();

        step("Оплата 399, проверка на type=19 CLUB_BALANCE",
                () -> payPremiumInAdmin(member.email, PaymentType.CLUB_BALANCE));
        clubSteps.getAndValidatePayoutSummarySemi(member.token, AFTER_LIFE_PREMIUM_PRICE, AFTER_LIFE_PREMIUM_PRICE, true, false);
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
    public void withdrawalPayment() {
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
        clubSteps.getAndValidatePayoutSummary(withdrawal.token(), AFTER_LIFE, AFTER_LIFE, true, true);
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
}