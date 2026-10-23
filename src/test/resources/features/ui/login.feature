@ui @login
Feature: Customer login
  As a registered customer
  I want to log in to the Toolshop
  So that I can manage my account and orders

  Background:
    Given the user is on the login page

  @smoke
  Scenario: Customer logs in with valid credentials
    When the user logs in with valid customer credentials
    Then the "My account" page is displayed
    And the customer name is shown in the navigation menu

  # Uses an unknown email on purpose: repeated wrong passwords lock real accounts.
  Scenario: Login is rejected for unknown credentials
    When the user logs in with email "unknown.user@example.com" and password "wrong-password"
    Then the login error "Invalid email or password" is displayed
