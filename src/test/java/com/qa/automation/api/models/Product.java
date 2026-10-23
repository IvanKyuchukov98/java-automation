package com.qa.automation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Product(
        String id,
        String name,
        BigDecimal price,
        boolean inStock,
        boolean isRental,
        Category category,
        Brand brand) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Category(String id, String name, String slug) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Brand(String id, String name) {
    }
}
