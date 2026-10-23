package com.qa.automation.ui.components;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;

/** Top navigation bar, shared by every page. */
public class Header {

    private final SelenideElement cartQuantity = $(byAttribute("data-test", "cart-quantity"));
    private final SelenideElement userMenu = $(byAttribute("data-test", "nav-menu"));

    public SelenideElement cartQuantity() {
        return cartQuantity;
    }

    public SelenideElement userMenu() {
        return userMenu;
    }
}
