package com.qa.automation.steps.api;

import com.qa.automation.api.client.AuthClient;
import com.qa.automation.api.models.LoginResponse;
import com.qa.automation.config.Config;
import com.qa.automation.context.ApiContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthApiSteps {

    private final ApiContext context;
    private final AuthClient authClient = new AuthClient();

    public AuthApiSteps(ApiContext context) {
        this.context = context;
    }

    @When("the client logs in with valid customer credentials")
    public void loginWithValidCredentials() {
        context.setLastResponse(authClient.login(Config.get("customer.email"), Config.get("customer.password")));
    }

    @When("the client logs in with email {string} and password {string}")
    public void loginWith(String email, String password) {
        context.setLastResponse(authClient.login(email, password));
    }

    @Then("a bearer access token is returned")
    public void bearerTokenIsReturned() {
        LoginResponse login = context.lastResponse().as(LoginResponse.class);

        assertThat(login.accessToken()).isNotBlank();
        assertThat(login.tokenType()).isEqualToIgnoringCase("bearer");
        assertThat(login.expiresIn()).isPositive();
    }

    @Then("the error message is {string}")
    public void errorMessageIs(String message) {
        assertThat(context.lastResponse().jsonPath().getString("error")).isEqualTo(message);
    }
}
