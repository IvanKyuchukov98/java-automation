package com.qa.automation.ui.pages;

import com.codeborne.selenide.SelenideElement;

import java.util.Map;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;

/** Steps 2-4 of checkout: sign in, billing address and payment. */
public class CheckoutPage {

    private final SelenideElement proceedAsLoggedInButton = $(byAttribute("data-test", "proceed-2"));

    private final SelenideElement country = $(byAttribute("data-test", "country"));
    private final SelenideElement postalCode = $(byAttribute("data-test", "postal_code"));
    private final SelenideElement houseNumber = $(byAttribute("data-test", "house_number"));
    private final SelenideElement street = $(byAttribute("data-test", "street"));
    private final SelenideElement city = $(byAttribute("data-test", "city"));
    private final SelenideElement state = $(byAttribute("data-test", "state"));
    private final SelenideElement proceedToPaymentButton = $(byAttribute("data-test", "proceed-3"));

    private final SelenideElement paymentMethodSelect = $(byAttribute("data-test", "payment-method"));
    private final SelenideElement finishButton = $(byAttribute("data-test", "finish"));
    private final SelenideElement paymentSuccessMessage = $(byAttribute("data-test", "payment-success-message"));
    private final SelenideElement orderConfirmation = $("#order-confirmation");

    public void proceedAsLoggedInCustomer() {
        proceedAsLoggedInButton.click();
    }

    /**
     * For a logged-in customer the form is pre-filled asynchronously from the saved profile.
     * Typing before that finishes gets overwritten, so wait for the saved street first.
     */
    public void waitForSavedAddress() {
        street.shouldNotHave(exactValue(""));
    }

    /**
     * Entering country, postal code and house number triggers a postcode lookup
     * that fills street, city and state. Returns the selected country code.
     */
    public String enterAddressLookupFields(String countryName, String postcode, String number) {
        country.selectOption(countryName);
        postalCode.setValue(postcode);
        houseNumber.setValue(number);
        return country.getValue();
    }

    public SelenideElement street() {
        return street;
    }

    public SelenideElement city() {
        return city;
    }

    public SelenideElement state() {
        return state;
    }

    public void proceedToPayment() {
        proceedToPaymentButton.shouldBe(enabled).click();
    }

    public void choosePaymentMethod(String value) {
        paymentMethodSelect.selectOptionByValue(value);
    }

    /** Fills payment fields by their data-test names, e.g. credit_card_number, cvv. */
    public void fillPaymentDetails(Map<String, String> details) {
        details.forEach((field, value) -> $(byAttribute("data-test", field)).setValue(value));
    }

    public void confirmPayment() {
        finishButton.shouldBe(enabled).click();
    }

    public SelenideElement paymentSuccessMessage() {
        return paymentSuccessMessage;
    }

    public SelenideElement orderConfirmation() {
        return orderConfirmation;
    }
}
