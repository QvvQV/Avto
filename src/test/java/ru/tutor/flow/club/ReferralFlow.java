package ru.tutor.flow.club;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.config.TestConfig;
import ru.tutor.constants.AddressPage;
import ru.tutor.flow.AdminFlow;
import ru.tutor.flow.PurchaseFlow;
import ru.tutor.flow.PurchaseResult;
import ru.tutor.model.club.ClubMemberResponse;
import ru.tutor.model.club.ClubStatisticsResponse;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.RedirectPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.page.admin.AdminOrderDetails;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.modals.GreetingsModal;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.SecretMenu;
import ru.tutor.steps.AdminToUserLoginSteps;
import ru.tutor.steps.club.ClubSteps;
import ru.tutor.test.context.TestExecutionContext;
import ru.tutor.test.executor.RecurrentPaymentTestExecutor;
import ru.tutor.utils.TabManager;

import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferralFlow {

    private final RecurrentPaymentTestExecutor testExecutor;
    private final ClubSteps clubSteps;
    private final TestExecutionContext context;
    private final PurchaseFlow purchaseFlow;
    private final AdminFlow adminFlow;

    private ClubMemberResponse currentMemberInfo;

    private final List<String> userTokens = new ArrayList<>();

    public ReferralFlow(TtPage ttPage, CloudPage cloudPage, SberPage sberPage, AdminOrdersPage adminOrdersPage,
                        AdminOrderDetails adminOrderDetails, RecurrentPaymentTestExecutor testExecutor, TestExecutionContext context,
                        PurchaseFlow purchaseFlow, AdminFlow adminFlow) {

        this.testExecutor = testExecutor;
        this.context = context;
        this.clubSteps = new ClubSteps(TestConfig.getCurrentApiUrl());
        this.purchaseFlow = purchaseFlow;
        this.adminFlow = adminFlow;

    }

    /**
     * Создание реферальной цепочки указанной длины
     *
     * @param 'chainLength' количество пользователей в цепочке
     * @return список реферальных ссылок
     */
    @Step("Создание реферальной цепочки из {chainLength} пользователей")
    public List<String> createReferralChain(int chainLength) {
        return step("Создание реферальной цепочки из " + chainLength + " пользователей ",
                () -> {
                    List<String> referralLinks = new ArrayList<>();

                    // Первый пользователь (корень цепочки)
                    String firstLink = createRootUser();
                    referralLinks.add(firstLink);
                    logoutCurrentUser();

                    for (int i = 1; i < chainLength; i++) {
                        String previousLink = referralLinks.get(i - 1);
                        String newLink = createUserByReferral(previousLink, i + 1);
                        referralLinks.add(newLink);
                        logoutCurrentUser();
                    }

                    logChainSummary(referralLinks);
                    return referralLinks;
                });
    }

    @Step("Создание корневого пользователя (без реферальной ссылки)")
    private String createRootUser() {
        executePayment();
        processPostPaymentSteps();
        String token = AdminToUserLoginSteps.switchToTabAndGetToken();
        userTokens.add(token);
        return getCurrentUserReferralLink("корневого");
    }

    @Step("Создание пользователя {userNumber} по реферальной ссылке")
    private String createUserByReferral(String referralLink, int userNumber) {

        return step("Создание пользователя " + userNumber + " по реферальной ссылке", () -> {
            String urlWithReferral = AddressPage.URL_MAIN + "?club_ref=" + referralLink;
            Selenide.open(urlWithReferral);

            Allure.addAttachment(String.format("👤 Пользователь #%d", userNumber),
                    "Открыта ссылка: " + urlWithReferral);

            executePayment();
            processPostPaymentSteps();

            String token = AdminToUserLoginSteps.switchToTabAndGetToken();
            userTokens.add(token);

            return getCurrentUserReferralLink(String.valueOf(userNumber));
        });
    }

    @Step("Выполнение платежа")
    private String executePayment() {
        PurchaseResult result = purchaseFlow.premium();
        adminFlow.processRecurrent(result.email());
        testExecutor.executePaymentTest("Cloud Premium", result.email(), context, 1);
        return result.email();
    }

    @Step("Шаги после оплаты (редирект, бонус, регистрация)")
    private void processPostPaymentSteps() {
        TabManager.goToTheOrderTab();
        new RedirectPage().redirectPage();
        new Product().pickUpBonus();
        new GreetingsModal().fillAccessInput();
    }

    @Step("Получение реферальной ссылки {userDescription} пользователя")
    private String getCurrentUserReferralLink(String userDescription) {
        String token = AdminToUserLoginSteps.switchToTabAndGetToken();

        Response response = clubSteps.getClubMember(token);
        currentMemberInfo = clubSteps.validateClubMemberResponse(response);
        clubSteps.validateClubMemberValues(currentMemberInfo);

        logMemberInfo(userDescription);

        return currentMemberInfo.referralLink();
    }

    @Step("Выход текущего пользователя")
    private void logoutCurrentUser() {
        new SecretMenu().logoutCurrentUser();
    }

    @Step("Логирование информации о членстве")
    private void logMemberInfo(String userDescription) {
        String memberDetails = String.format("""
                        👤 Информация о %s пользователе:
                        ID: %d
                        Активен: %s
                        Тип членства: %s
                        Дата членства: %s
                        Реферальная ссылка: %s
                        Детали: %s""",
                userDescription,
                currentMemberInfo.id(),
                currentMemberInfo.isActive(),
                currentMemberInfo.memberType() != null ? currentMemberInfo.memberType() : "не указан",
                currentMemberInfo.membershipDate(),
                currentMemberInfo.referralLink(),
                currentMemberInfo.details() != null ? currentMemberInfo.details() : "нет"
        );

        Allure.addAttachment("Информация о членстве", "text/plain", memberDetails);
    }

    @Step("Логирование сводки по цепочке")
    private void logChainSummary(List<String> referralLinks) {
        StringBuilder summary = new StringBuilder();
        summary.append("📊 СВОДКА ПО РЕФЕРАЛЬНОЙ ЦЕПОЧКЕ\n");
        summary.append("================================\n\n");

        for (int i = 0; i < referralLinks.size(); i++) {
            summary.append(String.format("👤 Пользователь %d: %s\n",
                    i + 1, referralLinks.get(i)));
        }

        summary.append("\n✅ Цепочка из ").append(referralLinks.size())
                .append(" пользователей успешно создана!");

        Allure.addAttachment("Реферальная цепочка", "text/plain", summary.toString());
    }

    /**
     * Проверка статистики для конкретного пользователя
     */
    @Step("Проверка статистики пользователя {userIndex}")
    private ClubStatisticsResponse verifyUserStatistics(int userIndex, int expectedReferrals) {
        if (userIndex >= userTokens.size()) {
            throw new IllegalArgumentException("Нет токена для пользователя с индексом " + userIndex);
        }
        String token = userTokens.get(userIndex);
        return verifyUserStatisticsWithToken(token, expectedReferrals);
    }

    private ClubStatisticsResponse verifyUserStatisticsWithToken(String token, int expectedReferrals) {
        Response response = clubSteps.getClubStatistics(token);
        ClubStatisticsResponse stats = clubSteps.validateClubStatisticsResponse(response);

        Allure.addAttachment(String.format("Статистика пользователя (токен: %s...)",
                        token.substring(0, Math.min(10, token.length()))), "text/plain",
                String.format("Ожидаемые рефералы: %d\nФактические: %d\nБаланс: %d",
                        expectedReferrals, stats.referrals().all(), stats.balance()));

        assertEquals(expectedReferrals, stats.referrals().all(),
                "Количество рефералов не соответсвует ожидаемому");
        return stats;
    }

    @Step("Проверка статистики всей реферальной цепочки")
    public void verifyFullChainStatistics() {
        List<String> tokens = getUserTokens();
        int chainLength = tokens.size();

        Allure.addAttachment("Длина цепочки", String.valueOf(chainLength));

        for (int i = 0; i < chainLength; i++) {
            int expectedReferrals = chainLength - i - 1;
            verifyUserStatistics(i, expectedReferrals);
        }

        Allure.addAttachment("Результат", "✅ Статистика всей цепочки корректна");
    }

    public List<String> getUserTokens() {
        return userTokens;
    }
}
