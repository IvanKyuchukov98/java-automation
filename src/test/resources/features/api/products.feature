@api @products
Feature: Products API
  The product catalogue is served as a paginated list.

  @smoke
  Scenario: Product list returns the first page of products
    When the client requests page 1 of the product list
    Then the response status code is 200
    And the response matches the "product-page" schema
    And the product list is on page 1 with at most 9 products
    And every product has a name, a category and a positive price
