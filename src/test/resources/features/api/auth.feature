@api @auth
Feature: Authentication API

  @smoke
  Scenario: Customer receives an access token with valid credentials
    When the client logs in with valid customer credentials
    Then the response status code is 200
    And a bearer access token is returned

  Scenario: Login is rejected with a wrong password
    When the client logs in with email "customer@practicesoftwaretesting.com" and password "wrong-password"
    Then the response status code is 401
    And the error message is "Unauthorized"
