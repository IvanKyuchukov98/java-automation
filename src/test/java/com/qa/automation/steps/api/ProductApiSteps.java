package com.qa.automation.steps.api;

import com.qa.automation.api.client.ProductsClient;
import com.qa.automation.api.models.Product;
import com.qa.automation.api.models.ProductPage;
import com.qa.automation.context.ApiContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductApiSteps {

    private final ApiContext context;
    private final ProductsClient productsClient = new ProductsClient();

    public ProductApiSteps(ApiContext context) {
        this.context = context;
    }

    @When("the client requests page {int} of the product list")
    public void requestProductPage(int page) {
        context.setLastResponse(productsClient.getProducts(page));
    }

    @Then("the product list is on page {int} with at most {int} products")
    public void productListPage(int page, int maxPerPage) {
        ProductPage productPage = context.lastResponse().as(ProductPage.class);

        assertThat(productPage.currentPage()).isEqualTo(page);
        assertThat(productPage.data())
                .isNotEmpty()
                .hasSizeLessThanOrEqualTo(maxPerPage);
        assertThat(productPage.total()).isGreaterThanOrEqualTo(productPage.data().size());
    }

    @Then("every product has a name, a category and a positive price")
    public void everyProductIsValid() {
        ProductPage productPage = context.lastResponse().as(ProductPage.class);

        assertThat(productPage.data()).allSatisfy(product -> {
            assertThat(product.name()).as("name of %s", product.id()).isNotBlank();
            assertThat(product.category()).as("category of %s", product.name()).isNotNull();
            assertThat(product.price()).as("price of %s", product.name()).isGreaterThan(BigDecimal.ZERO);
        });
    }
}
