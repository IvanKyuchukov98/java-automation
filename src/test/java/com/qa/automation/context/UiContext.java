package com.qa.automation.context;

import java.math.BigDecimal;

/**
 * Per-scenario state shared between UI step classes, e.g. the product picked in one
 * step and checked in the cart later. PicoContainer creates a new instance per scenario.
 */
public class UiContext {

    private String productName;
    private BigDecimal productPrice;

    public String productName() {
        if (productName == null) {
            throw new IllegalStateException("No product has been selected in this scenario");
        }
        return productName;
    }

    public BigDecimal productPrice() {
        if (productPrice == null) {
            throw new IllegalStateException("No product price has been stored in this scenario");
        }
        return productPrice;
    }

    public void rememberProduct(String name, BigDecimal price) {
        this.productName = name;
        this.productPrice = price;
    }
}
