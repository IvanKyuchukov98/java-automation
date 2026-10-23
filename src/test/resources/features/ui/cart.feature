@ui @cart
Feature: Shopping cart
  As a shopper
  I want to review and change my cart before checkout

  Background:
    Given the cart contains 1 x "Combination Pliers"
    And the user opens the cart

  Scenario: Cart shows the added product
    Then the cart contains the product with quantity 1
    And the line total is 1 times the unit price

  @smoke
  Scenario: Changing the quantity updates the totals
    When the user changes the quantity to 4
    Then the line total is 4 times the unit price

  Scenario: Removing the only product empties the cart
    When the user removes the product from the cart
    Then the cart is empty
