//package ru.tutor.test.api.club;
//
//import com.codeborne.selenide.Selenide;
//import io.qameta.allure.Allure;
//import io.qameta.allure.Epic;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import ru.tutor.base.RecurrentPaymentTestBase;
//import ru.tutor.config.TestConfig;
//import ru.tutor.flow.SubscriptionTestFlow;
//import ru.tutor.model.club.ClubMemberResponse;
//import ru.tutor.model.club.ClubStatisticsResponse;
//import ru.tutor.constants.AddressPage;
//import ru.tutor.steps.AdminToUserLoginSteps;
//import ru.tutor.steps.AuthSteps;
//import ru.tutor.steps.OrderToggleSubscriptionSteps;
//import ru.tutor.steps.ReactivatePremiumSteps;
//import ru.tutor.steps.club.ClubSteps;
//import ru.tutor.test.executor.RecurrentPaymentTestExecutor;
//import ru.tutor.utils.WaitHelper;
//
//import static io.qameta.allure.Allure.step;
//
//public class Referals extends RecurrentPaymentTestBase {
//
//    private static final String BASE_URL = TestConfig.getCurrentApiUrl();
//
//    private RecurrentPaymentTestExecutor testExecutor;
//    private final AuthSteps authSteps = AuthSteps.withBaseUrl(BASE_URL);
//    private final ClubSteps clubSteps = ClubSteps.withBaseUrl(BASE_URL);
//    private final OrderToggleSubscriptionSteps orderSteps = OrderToggleSubscriptionSteps.withBaseUrl(BASE_URL);
//    private final ReactivatePremiumSteps reactivatePremiumSteps = ReactivatePremiumSteps.withBaseUrl(BASE_URL);
//
//    private final SubscriptionTestFlow flow = new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage);
//    private final AdminToUserLoginSteps adminToUserLoginSteps = new AdminToUserLoginSteps();
//    private static ClubMemberResponse memberInfo;
//    private static ClubStatisticsResponse statisticsInfo;
//
//
//    @Epic("Реферальная цепочка клуба")
//    @Override
//    protected void initializePaymentCycleExecutor() {
//        super.initializePaymentCycleExecutor();
//        testExecutor = new RecurrentPaymentTestExecutor(
//                orderDatesPage,
//                adminOrderDetails,
//                adminOrdersPage,
//                paymentCycleExecutor
//        );
//
//    }
//
//    @Tag("UI + API")
//    @Test
//    @DisplayName("Создание цепочки членов клуба")
//    void createdReferrals() {
//        testExecutor.executeTwoPaymentTest(
//                "Cloud Premium",
//                () -> new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage).purchasePremiumSubscription(),
//                SubscriptionTestFlow::processRecurrentInAdmin,
//                context, 1
//        );
//        step("Перейти в ЛК пользователя", () -> {
//            WaitHelper.waitForAnimationComplete();
//            adminOrdersPage.goToUserPersonalAccount();
//        });
//
//        String referralLink = step("Получить реферальную ссылку", () -> {
//
//            String token = AdminToUserLoginSteps.switchToTabAndGetToken();
//
//            Response response = step("GET /user/club/member", () ->
//                    clubSteps.getClubMember(token)
//            );
//
//            memberInfo = step("Валидация ответа", () ->
//                    clubSteps.validateClubMemberResponse(response)
//            );
//
//            step("Проверка значений членства", () -> {
//                clubSteps.validateClubMemberValues(memberInfo);
//
//                Allure.addAttachment("Информация о членстве", "text/plain",
//                        "ID: " + memberInfo.getId() + "\n" +
//                                "Активен: " + memberInfo.getActive() + "\n" +
//                                "Тип членства: " + memberInfo.getMemberType() + "\n" +
//                                "Дата членства: " + memberInfo.getMembershipDate() + "\n" +
//                                "Реферальная ссылка: " + memberInfo.getReferralLink() + "\n" +
//                                "Детали: " + memberInfo.getDetails());
//
//            });
//            return memberInfo.getReferralLink();
//        });
//
//        step("Регистрируем второго пользователя клуба", () -> {
//            String urlWithReferral = AddressPage.urlMainRev + "?club_ref=" + referralLink;
//            Selenide.open(urlWithReferral);
//
//            new SubscriptionTestFlow(ttPage, cloudPage, sberPage, adminOrdersPage)
//                    .purchasePremiumSubscription() // или другой тариф
//                    .processRecurrentInAdmin();
//        });
//
//            step("Перейти в ЛК пользователя", () -> {
//                WaitHelper.waitForAnimationComplete();
//                adminOrdersPage.goToUserPersonalAccount();
//            });
//
//            String referralLink2 = step("Получить реферальную ссылку", () -> {
//
//                String token = AdminToUserLoginSteps.switchToTabAndGetToken();
//
//                Response response = step("GET /user/club/member", () ->
//                        clubSteps.getClubMember(token)
//                );
//
//                memberInfo = step("Валидация ответа", () ->
//                        clubSteps.validateClubMemberResponse(response)
//                );
//
//                step("Проверка значений членства", () -> {
//                    clubSteps.validateClubMemberValues(memberInfo);
//
//                    Allure.addAttachment("Информация о членстве", "text/plain",
//                            "ID: " + memberInfo.getId() + "\n" +
//                                    "Активен: " + memberInfo.getActive() + "\n" +
//                                    "Тип членства: " + memberInfo.getMemberType() + "\n" +
//                                    "Дата членства: " + memberInfo.getMembershipDate() + "\n" +
//                                    "Реферальная ссылка: " + memberInfo.getReferralLink() + "\n" +
//                                    "Детали: " + memberInfo.getDetails());
//                });
//                return memberInfo.getReferralLink();
//            });
//
//            step("Регистрируем второго пользователя клуба", () -> {
//                String urlWithReferral2 = AddressPage.urlMainRev + "?club_ref=" + referralLink2;
//                Selenide.open(urlWithReferral2);
//            });
//    }
//}
