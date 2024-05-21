package io.brightskies.loyalty.refund.DTO;

import io.brightskies.loyalty.constants.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ProductRefundDTO {
    @Schema(minimum = "0")
    @PositiveOrZero(message = "Product id"
            + ValidationMessages.POSITIVE_OR_ZERO)
    private long productId;

    @Schema(minimum = "1")
    @PositiveOrZero(message = "quantity"
            + ValidationMessages.POSITIVE)
    private int quantity;

}
