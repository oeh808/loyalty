package io.brightskies.loyalty.product.dto;

import io.brightskies.loyalty.constants.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductCreationDto(
        @Schema(requiredProperties = {
                ValidationMessages.NOT_BLANK_SCHEMA }) @NotBlank(message = "product name"
                        + ValidationMessages.NOT_BLANK) String name,
        @Schema(minimum = ">0") @Positive(message = "price"
                + ValidationMessages.POSITIVE) float price,
        @Schema(minimum = "0") @PositiveOrZero(message = "pointsValue"
                + ValidationMessages.POSITIVE_OR_ZERO) int pointsValue){

}
