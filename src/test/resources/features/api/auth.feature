@api @auth
Feature: Authentication API

  @smoke
  Scenario: Customer receives an access token with valid credentials
    When the client logs in with valid customer credentials
    Then the response status code is 200
    And a bearer access token is returned

  # Uses an unknown email on purpose: repeated wrong passwords lock real accounts.
  Scenario: Login is rejected for unknown credentials
    When the client logs in with email "unknown.user@example.com" and password "wrong-password"
    Then the response status code is 401
    And the error message is "Unauthorized"
