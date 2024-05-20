package io.brightskies.loyalty.refund.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ProductRefundDTO {
    private long productId;
    private int quantity;

}
