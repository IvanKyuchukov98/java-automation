package com.qa.automation.api.client;

import com.qa.automation.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Base for API clients. Every call starts from a fresh request, so headers,
 * bodies and auth never leak between requests or scenarios.
 */
public abstract class ApiClient {

    private static final AllureRestAssured ALLURE_FILTER = new AllureRestAssured();

    protected RequestSpecification request() {
        return given().spec(new RequestSpecBuilder()
                .setBaseUri(Config.apiBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(ALLURE_FILTER)
                .log(LogDetail.URI)
                .build());
    }

    protected RequestSpecification authorizedRequest(String token) {
        return request().auth().oauth2(token);
    }
}
