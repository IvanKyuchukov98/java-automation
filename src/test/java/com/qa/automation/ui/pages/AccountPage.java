package com.qa.automation.ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;

public class AccountPage {

    private final SelenideElement pageTitle = $(byAttribute("data-test", "page-title"));

    public SelenideElement pageTitle() {
        return pageTitle;
    }
}
