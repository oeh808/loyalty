package io.brightskies.loyalty.order.dtos;

import io.brightskies.loyalty.constants.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderedProductDto(
                @Schema(minimum = "0") @PositiveOrZero(message = "Product id"
                                + ValidationMessages.POSITIVE_OR_ZERO) long productId,
                @Schema(minimum = "1") @PositiveOrZero(message = "Product quantity"
                                + ValidationMessages.POSITIVE) int quantity) {

}
