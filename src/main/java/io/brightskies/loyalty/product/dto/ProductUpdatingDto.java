package io.brightskies.loyalty.product.dto;

// No Validation needed as anything that breaks validation constraints gets ignored at the service level
public record ProductUpdatingDto(String name, float price, int pointsValue) {

}
