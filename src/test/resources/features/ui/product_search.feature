@ui @search
Feature: Product search
  As a shopper
  I want to search the catalogue by name
  So that I can quickly find the tool I need

  Background:
    Given the user is on the home page

  @smoke
  Scenario Outline: Search returns only matching products for "<term>"
    When the user searches for "<term>"
    Then the search caption shows "<term>"
    And every product shown contains "<term>" in its name

    Examples:
      | term   |
      | pliers |
      | hammer |
      | saw    |

  Scenario: Search with no matches shows a message
    When the user searches for "nonexistent-tool-xyz"
    Then the message "There are no products found." is displayed
