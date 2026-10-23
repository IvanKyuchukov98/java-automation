package com.qa.automation.api.client;

import io.restassured.response.Response;

public class ProductsClient extends ApiClient {

    public Response getProducts(int page) {
        return request()
                .queryParam("page", page)
                .get("/products");
    }

    public Response getProduct(String id) {
        return request()
                .pathParam("id", id)
                .get("/products/{id}");
    }
}
