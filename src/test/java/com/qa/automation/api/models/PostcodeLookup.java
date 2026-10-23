package com.qa.automation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PostcodeLookup(String street, String city, String state, String country, String postcode) {
}
