package ru.tutor.flow.account;

import io.qameta.allure.Step;
import ru.tutor.config.TestConfig;
import ru.tutor.model.account.RegistrationRequest;
import ru.tutor.model.account.RegistrationResponse;
import ru.tutor.steps.account.AuthSteps;
import ru.tutor.steps.account.RegistrationSteps;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FreshUserFixture {

    public record FreshUser(String email, String password, String token) {}

    private final RegistrationSteps registrationSteps;
    private final AuthSteps authSteps;

    public FreshUserFixture() {
        this.registrationSteps = new RegistrationSteps(TestConfig.getCurrentApiUrl());
        this.authSteps = new AuthSteps(TestConfig.getCurrentApiUrl());
    }

    @Step("Регистрация нового пользователя POST /register")
    public FreshUser register() {
        RegistrationRequest request = RegistrationRequest.generateRandom();
        RegistrationResponse response = registrationSteps.validateRegistrationResponse(registrationSteps.register(request));
        assertTrue(response.isSuccessful(), "Регистрация должна быть успешной");
        String token = authSteps.loginAndGetToken(request.getEmail(), request.getPassword());
        return new FreshUser(request.getEmail(), request.getPassword(), token);
    }
}
