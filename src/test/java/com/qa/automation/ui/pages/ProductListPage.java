package com.qa.automation.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.qa.automation.utils.Money;

import java.math.BigDecimal;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/** Home page: product grid with search, sorting and filters. */
public class ProductListPage {

    private final SelenideElement searchInput = $(byAttribute("data-test", "search-query"));
    private final SelenideElement searchButton = $(byAttribute("data-test", "search-submit"));
    private final SelenideElement searchTerm = $(byAttribute("data-test", "search-term"));
    private final SelenideElement noResults = $(byAttribute("data-test", "no-results"));
    private final SelenideElement sortSelect = $(byAttribute("data-test", "sort"));
    private final ElementsCollection productCards = $$("a.card");
    private final ElementsCollection productNames = $$(byAttribute("data-test", "product-name"));
    private final ElementsCollection productPrices = $$(byAttribute("data-test", "product-price"));

    public ProductListPage open() {
        Selenide.open("/");
        productNames.shouldHave(sizeGreaterThan(0));
        return this;
    }

    public void searchFor(String term) {
        searchInput.setValue(term);
        searchButton.click();
        waitForState("search_completed");
    }

    public void sortBy(String option) {
        sortSelect.selectOption(option);
        waitForState("sorting_completed");
    }

    public void openProduct(String name) {
        productCards.findBy(text(name)).click();
    }

    public BigDecimal priceOf(String name) {
        SelenideElement card = productCards.findBy(text(name));
        return Money.parse(card.$(byAttribute("data-test", "product-price")).getText());
    }

    public ElementsCollection productNames() {
        return productNames;
    }

    public List<BigDecimal> productPrices() {
        return productPrices.shouldHave(sizeGreaterThan(0)).texts().stream()
                .map(Money::parse)
                .toList();
    }

    public SelenideElement searchTerm() {
        return searchTerm;
    }

    public SelenideElement noResults() {
        return noResults;
    }

    /** The product grid exposes its loading state in a data-test attribute, e.g. "search_completed". */
    private void waitForState(String state) {
        $(byAttribute("data-test", state)).should(exist);
    }
}
