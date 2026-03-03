package ru.tutor.test.api;


import io.qameta.allure.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.config.TestConfig;
import ru.tutor.model.fortune.GameWheelRewardResponse;
import ru.tutor.steps.AuthSteps;
import ru.tutor.steps.StoriesSteps;


import java.util.List;

import static io.qameta.allure.Allure.step;

@ExtendWith(AllureJunit5.class)
@Epic("API Тесты пользователя")
@Feature("Игровое колесо")
@DisplayName("Тесты эндпоинта игрового колеса")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoryTest {

    private final AuthSteps authSteps =  AuthSteps.withBaseUrl(TestConfig.getCurrentApiUrl());
    private final StoriesSteps  storiesSteps =  StoriesSteps.withBaseUrl(TestConfig.getCurrentApiUrl());
    private final String TEST_EMAIL = TestConfig.getTestEmailFortune();
    private final String TEST_PASSWORD = TestConfig.getTestPassword();

    private static String token;
    private static List<GameWheelRewardResponse> rewards;

    @BeforeAll
    public static void setUp() {
        Allure.label("component", "API");
        Allure.label("feature", "Game Wheel");
    }

    @BeforeEach
    void setUpAuth() {
        // Логин перед каждым тестом
        token = step("Логин пользователя", () ->
                authSteps.loginAndGetToken(TEST_EMAIL, TEST_PASSWORD)
        );
    }

//    @Test
    @Order(1)
    @DisplayName("Успешное получение списка наград игрового колеса (Проверка 200 OK)")
    @Story("Основной сценарий")
    @Severity(SeverityLevel.CRITICAL)
    void getGameWheelRewardsSuccess() {

        step("POST /user/stories/screen", () ->
                storiesSteps.showStories(token)
        );

//        rewards = step("Валидация ответа со списком наград", () ->
//                gameWheelRewardsSteps.validateGameWheelRewardsResponse(response)
//        );
//
//        step("Проверка структуры наград", () -> {
//            gameWheelRewardsSteps.validaterewardStructure(rewards);
//
//            Allure.addAttachment("Количество наград", "text/plain",
//                    "Получено наград: " + rewards.size());
//        });
    }
}