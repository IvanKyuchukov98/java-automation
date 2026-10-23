package com.qa.automation.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement emailInput = $(byAttribute("data-test", "email"));
    private final SelenideElement passwordInput = $(byAttribute("data-test", "password"));
    private final SelenideElement loginButton = $(byAttribute("data-test", "login-submit"));
    private final SelenideElement errorMessage = $(byAttribute("data-test", "login-error"));

    public LoginPage open() {
        Selenide.open("/auth/login");
        return this;
    }

    public void loginAs(String email, String password) {
        emailInput.setValue(email);
        passwordInput.setValue(password);
        loginButton.click();
    }

    public SelenideElement errorMessage() {
        return errorMessage;
    }
}
