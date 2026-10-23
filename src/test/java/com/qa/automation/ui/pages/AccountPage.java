package com.qa.automation.ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;

public class AccountPage {

    private final SelenideElement pageTitle = $(byAttribute("data-test", "page-title"));
    private final SelenideElement userMenu = $(byAttribute("data-test", "nav-menu"));

    public SelenideElement pageTitle() {
        return pageTitle;
    }

    public SelenideElement userMenu() {
        return userMenu;
    }
}
