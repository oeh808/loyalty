package io.brightskies.loyalty.refund.DTO;

import io.brightskies.loyalty.order.OrderedProduct;
import lombok.Data;

import java.util.List;

@Data
public class ReFundDTO {
    private String phoneNumber;
    private long orderId;
    private List<OrderedProduct> orderedProducts;
}
