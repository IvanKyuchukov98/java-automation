package com.qa.automation.steps.api;

import com.qa.automation.context.ApiContext;
import io.cucumber.java.en.Then;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

public class CommonApiSteps {

    private final ApiContext context;

    public CommonApiSteps(ApiContext context) {
        this.context = context;
    }

    @Then("the response status code is {int}")
    public void statusCodeIs(int expected) {
        assertThat(context.lastResponse().statusCode())
                .as("HTTP status code")
                .isEqualTo(expected);
    }

    @Then("the response matches the {string} schema")
    public void responseMatchesSchema(String schemaName) {
        context.lastResponse().then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/" + schemaName + ".json"));
    }
}
