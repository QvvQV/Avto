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
import ru.tutor.utils.ClubTestDataGenerator;

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

    // ==================== БАЗОВЫЕ ШАГИ ====================

    private PremiumPurchase createPremiumPurchase() {
        String email = step("Создание пользователя с тарифом Премиум на главной", () -> {
            PurchaseResult result = purchaseFlow.premium();
            return result.email();
        });
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);

        payPremiumInAdmin(email);
        return new PremiumPurchase(email, credentials);
    }

    private Parent publishClub(String email, OrderSteps.PaymentCredentials credentials, int linkProductId) {
        String token = step("Вход в ЛК по refresh_token", () ->
                authSteps.loginViaRefreshToken(email, credentials.password()));

        step("Смена пароля в ЛК через API", () ->
                accountSteps.validateEditPasswordSuccess(
                        accountSteps.editPassword(token, credentials.password(), TestConfig.getTestPassword())
                ));

        step("Начисление награды за шеринг (POST /user/club/share-reward)", () -> {
            Response response = clubSteps.shareReward(token);
            clubSteps.validateShareRewardResponse(response);
        });
        step("Проверка сводки выплат (GET /user/club/v2/payouts/summary)", () -> {
            Response response = clubSteps.getPayoutSummary(token);
            ClubPayoutSummaryResponse summary = clubSteps.validateClubPayoutSummaryResponse(response);
            clubSteps.validateClubPayoutSummaryValues(summary);
        });

        ClubLinkResponse link = step("Создание реферальных ссылок (POST /user/club/v2/links)", () ->
                clubSteps.validateClubLinkResponse(clubSteps.createClubLink(token, linkProductId, email)));

        step("Выход из ЛК (сброс куки)",
                renewalModalFlow::logoutAfterTest
        );

        return new Parent(email, token, link.url());
    }

    private void payPremiumInAdmin(String email) {
        step("Оплата 399 в админке", () -> {
            adminSteps.login();
            String orderId = adminSteps.findOrderId(email);
            adminSteps.createPaymentAndValidateDates(orderId, PREMIUM_PRICE, PREMIUM_DAYS);
        });
    }

    @Step("Закрытие браузера и открытие по созданной ссылке (новая сессия)")
    private void reopenByReferralLink(String referralUrl) {
        step("Закрытие браузера и сброс сессии", Selenide::closeWebDriver);
        step("Открытие реферальной ссылки в новой сессии", () -> open(referralUrl));
    }

    @Step("Этап 1: Родитель - заказ Премиум + оплата 399 в админке + ссылка")
    public Parent createParentAndLink() {
        String email = step("Создание пользователя с тарифом Премиум на главной", () -> {
            PurchaseResult result = purchaseFlow.premium();
            return result.email();
        });
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);

        payPremiumInAdmin(email);

        return publishClub(email, credentials, CLUB_LINK_PRODUCT_IDS.getFirst());
    }

    @Step("Этап 2: Клиент - открытие ссылки Родителя + заказ Пожизненный")
    public Parent createClientFromReferralLink(String parentLinkUrl) {
        reopenByReferralLink(parentLinkUrl);
        String email = step("Открытие реферальной ссылки Родителя и создание заказа Пожизненный", () ->
                purchaseFlow.lifeReferral(parentLinkUrl).email());
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);
        return publishClub(email, credentials, CLUB_LINK_PRODUCT_IDS.get(1));
    }

    @Step("Этап 3: Клиент - открытие ссылки 2-го клиента + заказ Премиум + оплата 399 в админке")
    public Parent createPremiumClientFromReferralLink(String referralUrl) {
        reopenByReferralLink(referralUrl);
        String email = step("Открытие реферальной ссылки и создание заказа Премиум", () ->
                purchaseFlow.premiumReferral(referralUrl).email());
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);

        payPremiumInAdmin(email);

        return publishClub(email, credentials, CLUB_LINK_PRODUCT_IDS.get(2));
    }

    @Step("Этап 5: Проверка баланса Родителя (ожидается {expectedBalance})")
    public void verifyParentBalance(Parent parent, int expectedBalance) {
        step("Проверка сводки выплат Родителя (GET /user/club/v2/payouts/summary)", () -> {
            Response response = clubSteps.getPayoutSummary(parent.token());
            ClubPayoutSummaryResponse summary = clubSteps.validateClubPayoutSummaryResponse(response);
            clubSteps.validateClubPayoutSummaryValues(summary, expectedBalance, expectedBalance);
        });
    }

    @Step("Создание Премиум-заказа + оплата 399 в админке + шеринг")
    public PremiumMember createPremiumMemberAndShare() {
        String email = step("Создание пользователя с тарифом Премиум на главной", () -> {
            PurchaseResult result = purchaseFlow.premium();
            return result.email();
        });
        OrderSteps.PaymentCredentials credentials = OrderSteps.validateSuccessfulPayment(cloudPage);

        payPremiumInAdmin(email);

        String token = step("Вход в ЛК по refresh_token", () ->
                authSteps.loginViaRefreshToken(email, credentials.password()));

        step("Начисление награды за шеринг (POST /user/club/share-reward)", () -> {
            Response response = clubSteps.shareReward(token);
            clubSteps.validateShareRewardResponse(response);
        });

        return new PremiumMember(email, token);
    }

    @Step("Заполнение реквизитов ИП и проверка ручки summary")
    public void fillRequisitesAndVerifySummary() {
        PremiumMember member = step("Создание Премиум-заказа + шеринг",
                this::createPremiumMemberAndShare);

        step("До реквизитов: баланс 1000, profileValid=false", () -> {
            Response response = clubSteps.getPayoutSummary(member.token());
            ClubPayoutSummaryResponse summary = clubSteps.validateClubPayoutSummaryResponse(response);
            clubSteps.validateClubPayoutSummaryValues(summary, 1000, 1000, false, false);
        });

        ClubPayoutRequisitesRequest request = step("Генерация валидных реквизитов ИП",
                () -> ClubTestDataGenerator.generateValidPayoutRequisites(member.email()));

        step("PUT /user/club/v2/payouts/requisites - сохранение",
                () -> clubSteps.validateRequisitesSaved(
                        clubSteps.savePayoutRequisites(member.token(), request)));

        step("GET /user/club/v2/payouts/requisites - реквизиты совпадают с запросом", () -> {
            Response response = clubSteps.getPayoutRequisites(member.token());
            clubSteps.validateRequisitesMatchRequest(response, request);
        });

        step("После реквизитов: баланс 1000, profileValid=true, canRequestWithdrawal=false", () -> {
            Response response = clubSteps.getPayoutSummary(member.token());
            ClubPayoutSummaryResponse summary = clubSteps.validateClubPayoutSummaryResponse(response);
            clubSteps.validateClubPayoutSummaryValues(summary, 1000, 1000, true, false);
        });

        step("DELETE /user/club/v2/payouts/requisites - удаление реквизитов",
                () -> clubSteps.validateRequisitesDeleted(clubSteps.deletePayoutRequisites(member.token())));
        step("GET /user/club/v2/payouts/requisites - реквизиты удалены (тело null)", () -> {
            Response response = clubSteps.getPayoutRequisites(member.token());
            clubSteps.validateRequisitesAbsent(response);
        });
        step("После удаления: баланс 1000, profileValid=false", () -> {
            Response response = clubSteps.getPayoutSummary(member.token());
            ClubPayoutSummaryResponse summary = clubSteps.validateClubPayoutSummaryResponse(response);
            clubSteps.validateClubPayoutSummaryValues(summary, 1000, 1000, false, false);
        });
    }

    @Step("заявка на вывод + отклонение в админке (безопасный возврат)")
    public void createWithdrawalAndReject() {
        PremiumMember member = step("Создание Премиум-заказа + оплата 399 + шеринг", () ->
                createPremiumMemberAndShare());

        step("Проверка summary после шеринга: баланс 1000, profileValid=false", () -> {
            Response response = clubSteps.getPayoutSummary(member.token());
            ClubPayoutSummaryResponse summary = clubSteps.validateClubPayoutSummaryResponse(response);
            clubSteps.validateClubPayoutSummaryValues(summary, 1000, 1000, false, false);
        });
        step("Заполнение реквизитов ИП  (PUT /user/club/v2/payouts/requisites)", () -> {
            ClubPayoutRequisitesRequest request = ClubTestDataGenerator.generateValidPayoutRequisites(member.email());
            clubSteps.validateRequisitesSaved(clubSteps.savePayoutRequisites(member.token(), request));
        });

        ClubLinkResponse link = step("Создание реферальной ссылки (POST /user/club/v2/links)", () ->
                clubSteps.validateClubLinkResponse(
                        clubSteps.createClubLink(member.token(), CLUB_LINK_PRODUCT_IDS.getFirst(), member.email())
                ));
        step("Очистка куки", renewalModalFlow::logoutAfterTest);

        step("Создание Клиента по ссылке родителя (Пожизненный)", () -> {
            reopenByReferralLink(link.url());
            purchaseFlow.lifeReferral(link.url());
            OrderSteps.validateSuccessfulPayment(cloudPage);
        });

        step("Проверка баланса Родителя: 3000, profileValid=true, canRequestWithdrawal=true", () -> {
            Response response = clubSteps.getPayoutSummary(member.token());
            ClubPayoutSummaryResponse summary = clubSteps.validateClubPayoutSummaryResponse(response);
            clubSteps.validateClubPayoutSummaryValues(summary, 3000, 3000, true, true);
        });

        Integer withdrawalId = step("Создание заявки на вывод 3000 (POST /user/club/v2/payouts)", () -> {
            Response response = clubSteps.createPayout(member.token(), 3000);
            return clubSteps.validatePayoutCreated(response).id();
        });

        step("Проверка статуса заявки в админке: new/Новая", () -> {
            adminSteps.login();
            Response resp = adminSteps.getClubWithdrawal(withdrawalId);
            adminSteps.validateWithdrawalStatus(resp, "new", "Новая");
        });

        step("Отклонение заявки (POST reject, returnBonus=true, 204)",
                () -> adminSteps.rejectClubWithdrawal(withdrawalId, true));

        step("Проверка статуса заявки после отклонения: rejected/Отклонена", () -> {
            Response resp = adminSteps.getClubWithdrawal(withdrawalId);
            adminSteps.validateWithdrawalStatus(resp, "rejected", "Отклонена");
        });
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