package ru.tutor.flow.account;

import io.qameta.allure.Step;
import net.datafaker.Faker;
import ru.tutor.config.TestConfig;
import ru.tutor.flow.purchase.RevshareFlow;
import ru.tutor.model.account.RegistrationRequest;
import ru.tutor.model.account.UserEditDataRequest;
import ru.tutor.page.account.FirstCourses;
import ru.tutor.page.account.Registration;
import ru.tutor.page.modals.GreetingsModal;
import ru.tutor.page.modals.Product;
import ru.tutor.page.modals.UnlockNextWeek;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.page.paymentModalWindow.PaymentModal;
import ru.tutor.page.personalAccount.HotNovelties;
import ru.tutor.steps.account.AccountSteps;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.account.RegistrationSteps;
import ru.tutor.steps.orders.OrderSteps;
import ru.tutor.steps.orders.OrderToggleSubscriptionSteps;
import ru.tutor.utils.WaitHelper;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.refresh;
import static io.qameta.allure.Allure.step;

public class RegistrationFlow {

    private final Faker faker = new Faker(Locale.ENGLISH);
    private final RegistrationSteps registrationSteps;
    private final AuthSteps authSteps;
    private final AccountSteps accountSteps;
    private final FirstCourses firstCourses = new FirstCourses();
    private final GreetingsModal greetingsModal = new GreetingsModal();
    private final HotNovelties hotNovelties = new HotNovelties();
    private final PaymentModal paymentModal = new PaymentModal();
    private final CloudPage cloudPage = new CloudPage();
    private final RenewalModalFlow renewalModalFlow;

    public RegistrationFlow() {
        String BASE_URL = TestConfig.getCurrentApiUrl();
        this.registrationSteps = new RegistrationSteps(BASE_URL);
        this.authSteps = new AuthSteps(BASE_URL);
        this.accountSteps = new AccountSteps(BASE_URL);

        OrderSteps orderSteps = new OrderSteps(BASE_URL);
        SberPage sberPage = new SberPage();
        RevshareFlow revshareFlow = new RevshareFlow(cloudPage, orderSteps);
        this.renewalModalFlow = new RenewalModalFlow(new Registration(),
                new Product(), new UnlockNextWeek(), cloudPage, new TtPage(), sberPage,
                revshareFlow,new OrderToggleSubscriptionSteps(BASE_URL));
    }

    @Step("Регистрация нового пользователя → вход → приветственная форма → клик на первый курс")
    public void registerNewUserAndOpenFirstCourse() {
        String email = faker.internet().emailAddress().toLowerCase();

        step("Регистрация нового пользователя (POST /register)", () ->
                registrationSteps.registerAndGetResponse(new RegistrationRequest(
                        email, "Статус", "", TestConfig.getTestPassword(), "", true, 0)));

        step("Вход через refresh_token и приветственная форма (PUT /user/edit/data)", () -> {
            String token = authSteps.loginViaRefreshToken(email, TestConfig.getTestPassword());
            WaitHelper.waitForPageLoad();
            accountSteps.validateEditDataSuccess(accountSteps.editUserData(token,
                    new UserEditDataRequest().name(faker.name().name()).gender(0).email(email)));
        });

        step("Обновление страницы", () -> {
            refresh();
            WaitHelper.waitForPageLoad();
        });

        greetingsModal.closeWelcomeModal();
        hotNovelties.scrollToBlock();
        hotNovelties.clickStartFirstCard();

        paymentModal.clickOpenAccess();

        if (TestConfig.isProd()) {
            renewalModalFlow.assertPaymentFrameVisible();
        } else {
            cloudPage.clickCardPageBtn();
            WaitHelper.waitForAnimationComplete();
            renewalModalFlow.verifyProductPresent(258);
        }
    }
}