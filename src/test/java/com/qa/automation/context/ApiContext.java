package com.qa.automation.context;

import io.restassured.response.Response;

/**
 * Per-scenario state shared between API step classes. PicoContainer creates a new
 * instance for every scenario, so nothing leaks between scenarios.
 */
public class ApiContext {

    private Response lastResponse;

    public Response lastResponse() {
        if (lastResponse == null) {
            throw new IllegalStateException("No API request has been sent in this scenario");
        }
        return lastResponse;
    }

    public void setLastResponse(Response response) {
        this.lastResponse = response;
    }
}
