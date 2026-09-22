package ru.tutor.page.modals;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.tutor.utils.AllureAttachments;
import ru.tutor.utils.WaitHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WelcomeBackModal {

    private static final Duration TIMEOUT = Duration.ofSeconds(20);

    private final SelenideElement AUTH_LINK = $x(
            "//a[contains(@class, 'bg-[#7200cc]') and normalize-space(.)='Авторизация']");

    @Step("Проверка ссылки «Авторизация» в модальном окне «Рады вновь вас видеть»")
    public void assertWelcomeBackModal() {
        step("Ожидание появления ссылки «Авторизация»", () ->
                WaitHelper.waitFor(() -> AUTH_LINK.is(visible), TIMEOUT,
                        "Ссылка «Авторизация» не появилась — модальное окно «Рады вновь вас видеть» не открылось"));
        step("Проверка href ссылки «Авторизация»", () -> {
            String href = AUTH_LINK.getAttribute("href");
            assertTrue(href != null && href.contains("/login"),
                    "href ссылки «Авторизация» не ведёт на /login, фактический: " + href);
        });

        AllureAttachments.attachScreenshot("Модальное окно «Рады вновь вас видеть»");
    }

    @Step("Проверка, что ссылка «Авторизация» (модалка «Рады вновь вас видеть») НЕ появилась")
    public void assertWelcomeBackModalNotShow() {
        step("Ожидание отсутствия ссылки «Авторизация»", () ->
                AUTH_LINK.shouldNot(appear, Duration.ofSeconds(10)));
    }
}
