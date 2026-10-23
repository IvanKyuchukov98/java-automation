package com.qa.automation.ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.qa.automation.utils.Money;

import java.math.BigDecimal;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;

public class ProductDetailsPage {

    private final SelenideElement name = $(byAttribute("data-test", "product-name"));
    private final SelenideElement unitPrice = $(byAttribute("data-test", "unit-price"));
    private final SelenideElement quantityInput = $(byAttribute("data-test", "quantity"));
    private final SelenideElement addToCartButton = $(byAttribute("data-test", "add-to-cart"));
    private final SelenideElement toastMessage = $("#toast-container .toast-message");

    public SelenideElement name() {
        return name;
    }

    public BigDecimal unitPrice() {
        return Money.parse(unitPrice.shouldBe(visible).getText());
    }

    public void setQuantity(int quantity) {
        quantityInput.setValue(String.valueOf(quantity));
    }

    public void addToCart() {
        addToCartButton.click();
    }

    public SelenideElement toastMessage() {
        return toastMessage;
    }
}
