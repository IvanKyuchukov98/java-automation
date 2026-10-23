package com.qa.automation.steps.ui;

import com.qa.automation.api.client.PostcodeClient;
import com.qa.automation.api.models.PostcodeLookup;
import com.qa.automation.ui.pages.CheckoutPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class CheckoutSteps {

    private final CheckoutPage checkoutPage = new CheckoutPage();
    private final PostcodeClient postcodeClient = new PostcodeClient();

    private String countryCode;
    private String postcode;
    private String houseNumber;

    @When("the customer continues as the logged-in user")
    public void continueAsLoggedInUser() {
        checkoutPage.proceedAsLoggedInCustomer();
    }

    @When("the customer enters country {string}, postal code {string} and house number {string}")
    public void enterAddressLookupFields(String country, String postcode, String houseNumber) {
        checkoutPage.waitForSavedAddress();
        this.countryCode = checkoutPage.enterAddressLookupFields(country, postcode, houseNumber);
        this.postcode = postcode;
        this.houseNumber = houseNumber;
    }

    @Then("street, city and state are filled in from the postcode lookup API")
    public void addressIsFilledFromLookup() {
        PostcodeLookup expected = postcodeClient.lookup(countryCode, postcode, houseNumber)
                .then().statusCode(200)
                .extract().as(PostcodeLookup.class);

        checkoutPage.street().shouldHave(exactValue(expected.street()));
        checkoutPage.city().shouldHave(exactValue(expected.city()));
        checkoutPage.state().shouldHave(exactValue(expected.state()));
    }

    @When("the customer proceeds to payment")
    public void proceedToPayment() {
        checkoutPage.proceedToPayment();
    }

    @When("the customer pays with {string}")
    public void payWith(String paymentMethod) {
        checkoutPage.choosePaymentMethod(paymentMethod);
        checkoutPage.confirmPayment();
    }

    @When("the customer pays with {string} using:")
    public void payWithDetails(String paymentMethod, Map<String, String> details) {
        checkoutPage.choosePaymentMethod(paymentMethod);
        checkoutPage.fillPaymentDetails(resolvePlaceholders(details));
        checkoutPage.confirmPayment();
    }

    @Then("the payment message {string} is shown")
    public void paymentMessageIsShown(String message) {
        checkoutPage.paymentSuccessMessage().shouldBe(visible).shouldHave(text(message));
    }

    @Then("the order is confirmed with an invoice number")
    public void orderIsConfirmed() {
        checkoutPage.confirmPayment();
        // The message is rendered via innerHTML, which strips ids, so match the number in the text.
        checkoutPage.orderConfirmation()
                .shouldBe(visible)
                .shouldHave(text("Thanks for your order!"))
                .shouldHave(matchText("invoice number is INV-\\d+"));
    }

    /** Replaces {future-expiry} so the card date never goes stale. */
    private Map<String, String> resolvePlaceholders(Map<String, String> details) {
        String futureExpiry = YearMonth.now().plusYears(2).format(DateTimeFormatter.ofPattern("MM/yyyy"));
        Map<String, String> resolved = new LinkedHashMap<>();
        details.forEach((field, value) -> resolved.put(field, value.replace("{future-expiry}", futureExpiry)));
        return resolved;
    }
}
