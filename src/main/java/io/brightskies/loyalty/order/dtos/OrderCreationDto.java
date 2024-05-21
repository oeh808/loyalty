package io.brightskies.loyalty.order.dtos;

import java.util.List;

import io.brightskies.loyalty.constants.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderCreationDto(
        @Schema(requiredProperties = {
                ValidationMessages.NOT_NULL_SCHEMA }) @NotNull(message = "orderedProductsDto"
                        + ValidationMessages.NOT_NULL) List<OrderedProductDto> orderedProductsDto,
        @Schema(minimum = "0") @PositiveOrZero(message = "moneySpent"
                + ValidationMessages.POSITIVE_OR_ZERO) float moneySpent,
        @Schema(minimum = "0") @PositiveOrZero(message = "pointsSpent"
                + ValidationMessages.POSITIVE_OR_ZERO) int pointsSpent,
        @Schema(requiredProperties = {
                ValidationMessages.NOT_BLANK_SCHEMA }) @NotBlank(message = "phoneNumber"
                        + ValidationMessages.NOT_BLANK) @Pattern(message = ValidationMessages.INVALID_PHONE_NUMBER, regexp = ValidationMessages.PHONE_NUMBER_REGEX) String phoneNumber){
}
