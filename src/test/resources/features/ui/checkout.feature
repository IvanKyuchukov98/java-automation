@ui @checkout
Feature: Checkout
  As a logged-in customer
  I want to pay for the products in my cart
  So that my order is placed

  Background:
    Given the customer is logged in
    And the cart contains 2 x "Combination Pliers"
    And the user opens the cart
    And the user proceeds to checkout
    And the customer continues as the logged-in user
    And the customer enters country "Austria", postal code "1010" and house number "98"
    And street, city and state are filled in from the postcode lookup API
    And the customer proceeds to payment

  @smoke
  Scenario: Pay with cash on delivery
    When the customer pays with "cash-on-delivery"
    Then the payment message "Payment was successful" is shown
    And the order is confirmed with an invoice number

  Scenario: Pay with a credit card
    When the customer pays with "credit-card" using:
      | credit_card_number | 4111-1111-1111-1111 |
      | expiration_date    | {future-expiry}     |
      | cvv                | 123                 |
      | card_holder_name   | Jane Doe            |
    Then the payment message "Payment was successful" is shown
    And the order is confirmed with an invoice number
