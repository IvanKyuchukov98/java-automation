package com.qa.automation.steps.ui;

import com.qa.automation.config.Config;
import com.qa.automation.ui.components.Header;
import com.qa.automation.ui.pages.AccountPage;
import com.qa.automation.ui.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class LoginSteps {

    private final LoginPage loginPage = new LoginPage();
    private final AccountPage accountPage = new AccountPage();
    private final Header header = new Header();

    @Given("the user is on the login page")
    public void openLoginPage() {
        loginPage.open();
    }

    @Given("the customer is logged in")
    public void customerIsLoggedIn() {
        loginPage.open();
        loginWithValidCredentials();
        pageIsDisplayed("My account");
    }

    @When("the user logs in with valid customer credentials")
    public void loginWithValidCredentials() {
        loginPage.loginAs(Config.get("customer.email"), Config.get("customer.password"));
    }

    @When("the user logs in with email {string} and password {string}")
    public void loginWith(String email, String password) {
        loginPage.loginAs(email, password);
    }

    @Then("the {string} page is displayed")
    public void pageIsDisplayed(String title) {
        accountPage.pageTitle().shouldHave(exactText(title));
    }

    @Then("the customer name is shown in the navigation menu")
    public void customerNameIsShown() {
        header.userMenu().shouldHave(text(Config.get("customer.name")));
    }

    @Then("the login error {string} is displayed")
    public void loginErrorIsDisplayed(String message) {
        loginPage.errorMessage().shouldBe(visible).shouldHave(text(message));
    }
}
