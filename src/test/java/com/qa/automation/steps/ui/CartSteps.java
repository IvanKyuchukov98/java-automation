package com.qa.automation.steps.ui;

import com.qa.automation.context.UiContext;
import com.qa.automation.ui.pages.CartPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.visible;

public class CartSteps {

    private final UiContext context;
    private final CartPage cartPage = new CartPage();

    public CartSteps(UiContext context) {
        this.context = context;
    }

    @When("the user opens the cart")
    public void openCart() {
        cartPage.open();
    }

    @When("the user changes the quantity to {int}")
    public void changeQuantity(int quantity) {
        cartPage.setQuantity(context.productName(), quantity);
    }

    @When("the user removes the product from the cart")
    public void removeProduct() {
        cartPage.remove(context.productName());
    }

    @When("the user proceeds to checkout")
    public void proceedToCheckout() {
        cartPage.proceed();
    }

    @Then("the cart contains the product with quantity {int}")
    public void cartContainsProduct(int quantity) {
        cartPage.productTitles().shouldHave(exactTexts(context.productName()));
        cartPage.quantityOf(context.productName()).shouldHave(exactValue(String.valueOf(quantity)));
    }

    @Then("the line total is {int} times the unit price")
    public void lineTotalIs(int quantity) {
        BigDecimal expected = context.productPrice().multiply(BigDecimal.valueOf(quantity));
        String expectedText = "$" + expected.setScale(2);

        // Totals are recalculated asynchronously, so use Selenide's waiting assertions.
        cartPage.linePriceOf(context.productName()).shouldHave(exactText(expectedText));
        cartPage.cartTotal().shouldHave(exactText(expectedText));
    }

    @Then("the cart is empty")
    public void cartIsEmpty() {
        cartPage.productTitles().shouldHave(size(0));
        cartPage.emptyCartMessage().shouldBe(visible);
    }
}
