package ru.tutor.api.Steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.tutor.page.Api.ConfigApi;

public class ConfigSteps {
    private final ConfigApi configApi;

    public ConfigSteps(String baseUrl, String basicAuth) {
        this.configApi = new ConfigApi(baseUrl, basicAuth);
    }

    @Step("GET /api/config - получить конфигурацию")
    public Response getConfig() {
        return configApi.getConfig();
    }

    @Step("POST /api/config - создать конфигурацию с телом: {body}")
    public Response createConfig(String body) {
        return configApi.createConfig(body);
    }

    @Step("PUT /api/config - обновить конфигурацию с телом: {body}")
    public Response updateConfig(String body) {
        return configApi.updateConfig(body);
    }

    @Step("DELETE /api/config - удалить конфигурацию")
    public Response deleteConfig() {
        return configApi.deleteConfig();
    }
}
