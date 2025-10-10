package ru.tutor.test.api;

import io.qameta.allure.AllureId;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.api.Steps.CatalogSteps;

import java.util.List;

@ExtendWith(AllureJunit5.class)
public class CatalogTest {
    private final CatalogSteps catalogSteps =
            new CatalogSteps("https://ttplace.ru", "admin", "JWwppsEA84B4BozJgE44sNiZZ");

    @Test
    @AllureId("41")
    @DisplayName("Все страницы каталога возвращают 200")
    void checkAllCatalogPages() {
        List<String> endpoints = List.of(
                "crash?_rsc=1ld0r",
                "godofwar?_rsc=1ld0r",
                "koty?_rsc=1ld0r",
                "18-plus?_rsc=1ld0r",
                "for-men?_rsc=1ld0r",
                "?_rsc=1ld0r",
                "professions?_rsc=1ld0r",
                "sports?_rsc=1ld0r",
                "top?_rsc=1ld0r",
                "asd?_rsc=1ld0r",
                "bad?_rsc=1ld0r",
                "regberg?_rsc=1ld0r",
                "dlya-zhenshin?_rsc=1ld0r",
                "dlya-muzchin?_rsc=1ld0r",
                "krasota-i-uhod?_rsc=1ld0r",
                "product/testkeis?_rsc=1ld0r",
                "pohudenie?_rsc=1ld0r",
                "professii-i-naviki?_rsc=1ld0r",
                "prochee?_rsc=1ld0r",
                "samorazvitie?_rsc=1ld0r",
                "sport?_rsc=1ld0r",
                "test-18?_rsc=1ld0r"
        );

        catalogSteps.checkAllCatalogPages(endpoints);
    }
}
