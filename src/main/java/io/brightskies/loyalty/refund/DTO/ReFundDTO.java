package io.brightskies.loyalty.refund.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

import io.brightskies.loyalty.constants.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

@Data
@ToString
@AllArgsConstructor
public class ReFundDTO {
    @Schema(requiredProperties = {
            ValidationMessages.NOT_BLANK_SCHEMA })
    @NotBlank(message = "Phone number"
            + ValidationMessages.NOT_BLANK)
    @Pattern(message = ValidationMessages.INVALID_PHONE_NUMBER, regexp = ValidationMessages.PHONE_NUMBER_REGEX)
    private String phoneNumber;

    @Schema(minimum = "0")
    @PositiveOrZero(message = "Order id"
            + ValidationMessages.POSITIVE_OR_ZERO)
    private long orderId;

    @Schema(requiredProperties = {
            ValidationMessages.NOT_NULL_SCHEMA })
    @NotNull(message = "Refunded products"
            + ValidationMessages.NOT_NULL)
    private List<ProductRefundDTO> ProductRefund;
}
