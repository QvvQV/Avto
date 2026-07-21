package ru.tutor.test.api.page;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.steps.PageSteps;
import ru.tutor.config.TestConfig;

import java.util.List;

@ExtendWith(AllureJunit5.class)
public class PageTest {
    private final PageSteps pageSteps = new PageSteps(TestConfig.getCurrentApiUrl());

//    @Test
    @AllureId("47")
    @DisplayName("Проверка на код 200 футер")
    void checkAllStaticPages(){
        List<String> endpoints = List.of(
                 "/about-company?_rsc=1wqyk",
                "/contacts?_rsc=1wqyk",
                "/about-subscription?_rsc=1wqyk",
                "/webmasters?_rsc=1wqyk",
                "/new-horizons?_rsc=1wqyk",
                "/for-authors?_rsc=1wqyk",
                "/vacancies?_rsc=1wqyk"
        );

        pageSteps.checkAllPage(endpoints);
    }

}
