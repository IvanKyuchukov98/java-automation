package com.qa.automation.api.client;

import com.qa.automation.api.models.LoginRequest;
import io.restassured.response.Response;

public class AuthClient extends ApiClient {

    public Response login(String email, String password) {
        return request()
                .body(new LoginRequest(email, password))
                .post("/users/login");
    }
}
