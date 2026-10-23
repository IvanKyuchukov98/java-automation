@ui @sorting
Feature: Product sorting
  As a shopper
  I want to sort the product list
  So that I can compare tools by name or price

  Background:
    Given the user is on the home page

  Scenario Outline: Sort products by price "<option>"
    When the user sorts products by "<option>"
    Then the products are sorted by price <direction>

    @smoke
    Examples: Smoke
      | option             | direction  |
      | Price (Low - High) | ascending  |

    Examples: Regression
      | option             | direction  |
      | Price (High - Low) | descending |

  Scenario Outline: Sort products by name "<option>"
    When the user sorts products by "<option>"
    Then the products are sorted by name <direction>

    Examples:
      | option       | direction  |
      | Name (A - Z) | ascending  |
      | Name (Z - A) | descending |
