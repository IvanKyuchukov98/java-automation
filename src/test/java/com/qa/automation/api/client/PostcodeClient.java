package com.qa.automation.api.client;

import io.restassured.response.Response;

public class PostcodeClient extends ApiClient {

    public Response lookup(String countryCode, String postcode, String houseNumber) {
        return request()
                .queryParam("country", countryCode)
                .queryParam("postcode", postcode)
                .queryParam("house_number", houseNumber)
                .get("/postcode-lookup");
    }
}
