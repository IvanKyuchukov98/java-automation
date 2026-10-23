package com.qa.automation.steps.ui;

import com.qa.automation.context.UiContext;
import com.qa.automation.ui.components.Header;
import com.qa.automation.ui.pages.ProductDetailsPage;
import com.qa.automation.ui.pages.ProductListPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.allMatch;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductSteps {

    private final UiContext context;
    private final ProductListPage productList = new ProductListPage();
    private final ProductDetailsPage productDetails = new ProductDetailsPage();
    private final Header header = new Header();

    public ProductSteps(UiContext context) {
        this.context = context;
    }

    @Given("the user is on the home page")
    public void openHomePage() {
        productList.open();
    }

    @When("the user searches for {string}")
    public void searchFor(String term) {
        productList.searchFor(term);
    }

    @When("the user sorts products by {string}")
    public void sortBy(String option) {
        productList.sortBy(option);
    }

    @When("the user opens the product {string}")
    public void openProduct(String name) {
        context.rememberProduct(name, productList.priceOf(name));
        productList.openProduct(name);
    }

    @When("the user adds {int} of the product to the cart")
    public void addToCart(int quantity) {
        productDetails.setQuantity(quantity);
        productDetails.addToCart();
    }

    @Given("the cart contains {int} x {string}")
    public void cartContains(int quantity, String name) {
        productList.open();
        openProduct(name);
        addToCart(quantity);
        header.cartQuantity().shouldHave(exactText(String.valueOf(quantity)));
    }

    @Then("the search caption shows {string}")
    public void searchCaptionShows(String term) {
        productList.searchTerm().shouldHave(exactText(term));
    }

    @Then("every product shown contains {string} in its name")
    public void everyProductContains(String term) {
        productList.productNames()
                .shouldHave(sizeGreaterThan(0))
                .shouldHave(allMatch("name contains " + term,
                        element -> element.getText().toLowerCase().contains(term.toLowerCase())));
    }

    @Then("the message {string} is displayed")
    public void noResultsMessage(String message) {
        productList.noResults().shouldBe(visible).shouldHave(text(message));
    }

    @Then("the products are sorted by price {word}")
    public void productsSortedByPrice(String direction) {
        List<BigDecimal> prices = productList.productPrices();
        Comparator<BigDecimal> order = direction.equals("ascending")
                ? Comparator.naturalOrder()
                : Comparator.reverseOrder();

        assertThat(prices).as("product prices").isSortedAccordingTo(order);
    }

    @Then("the products are sorted by name {word}")
    public void productsSortedByName(String direction) {
        List<String> names = productList.productNames().shouldHave(sizeGreaterThan(0)).texts();
        Comparator<String> order = direction.equals("ascending")
                ? String.CASE_INSENSITIVE_ORDER
                : String.CASE_INSENSITIVE_ORDER.reversed();

        assertThat(names).as("product names").isSortedAccordingTo(order);
    }

    @Then("the product page shows the same name and price as the list")
    public void productPageMatchesList() {
        productDetails.name().shouldHave(exactText(context.productName()));
        assertThat(productDetails.unitPrice()).isEqualByComparingTo(context.productPrice());
    }

    @Then("the confirmation {string} is shown")
    public void confirmationIsShown(String message) {
        productDetails.toastMessage().shouldBe(visible).shouldHave(text(message));
    }

    @Then("the cart badge shows {int}")
    public void cartBadgeShows(int quantity) {
        header.cartQuantity().shouldHave(exactText(String.valueOf(quantity)));
    }
}
