package ru.tutor.base;

import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.tutor.utils.ConfigReader;

@ExtendWith(AllureJunit5.class)
public abstract class BaseApiTest {
    protected static final String API_BASE_URL = ConfigReader.get("api1.base.url");
    protected static final String TEST_EMAIL = ConfigReader.get("test.email");
    protected String TEST_PASSWORD = ConfigReader.get("test.password");
}
