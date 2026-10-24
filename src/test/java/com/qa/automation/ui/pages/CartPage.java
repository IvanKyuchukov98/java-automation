package com.qa.automation.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/** Step 1 of checkout: the shopping cart. */
public class CartPage {

    private final ElementsCollection productTitles = $$(byAttribute("data-test", "product-title"));
    private final SelenideElement cartTotal = $(byAttribute("data-test", "cart-total"));
    private final SelenideElement proceedButton = $(byAttribute("data-test", "proceed-1"));
    private final SelenideElement emptyCartMessage = $(withText("The cart is empty"));

    /** Opens the cart directly; the cart id lives in session storage, so it survives navigation. */
    public CartPage open() {
        Selenide.open("/checkout");
        return this;
    }

    public ElementsCollection productTitles() {
        return productTitles;
    }

    /**
     * Waits until Angular has bound the current quantity (ngModel writes it a tick after the row renders),
     * then types over it. Clearing first is avoided: WebDriver's clear() fires a change event, which makes
     * the cart send a second, racing update for an empty (= 1) quantity.
     */
    public void setQuantity(String productName, int quantity) {
        SelenideElement input = row(productName).$(byAttribute("data-test", "product-quantity"))
                .shouldNotHave(exactValue(""));
        String backspaces = Keys.BACK_SPACE.toString().repeat(input.getValue().length());
        input.sendKeys(Keys.END + backspaces + quantity, Keys.TAB);
    }

    public SelenideElement quantityOf(String productName) {
        return row(productName).$(byAttribute("data-test", "product-quantity"));
    }

    public SelenideElement linePriceOf(String productName) {
        return row(productName).$(byAttribute("data-test", "line-price"));
    }

    public void remove(String productName) {
        row(productName).$("a.btn-danger").click();
    }

    public SelenideElement cartTotal() {
        return cartTotal;
    }

    public SelenideElement emptyCartMessage() {
        return emptyCartMessage;
    }

    public void proceed() {
        proceedButton.click();
    }

    private SelenideElement row(String productName) {
        return $$("table tbody tr").findBy(text(productName));
    }
}
