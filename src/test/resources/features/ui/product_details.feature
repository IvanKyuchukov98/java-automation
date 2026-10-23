@ui @product
Feature: Product details
  As a shopper
  I want to see a product's details and add it to my cart

  Background:
    Given the user is on the home page
    And the user opens the product "Combination Pliers"

  Scenario: Product page matches the product list
    Then the product page shows the same name and price as the list

  @smoke
  Scenario: Add a product to the cart
    When the user adds 3 of the product to the cart
    Then the confirmation "Product added to shopping cart." is shown
    And the cart badge shows 3
