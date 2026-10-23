package com.qa.automation.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {

    private Money() {
    }

    /** Parses UI price text such as "$14.15" or "14.15" into an amount with 2 decimals. */
    public static BigDecimal parse(String text) {
        String digits = text.replaceAll("[^0-9.]", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("No amount in text: '" + text + "'");
        }
        return new BigDecimal(digits).setScale(2, RoundingMode.HALF_UP);
    }
}
