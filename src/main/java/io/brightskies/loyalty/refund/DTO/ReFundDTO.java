package io.brightskies.loyalty.refund.DTO;

import io.brightskies.loyalty.order.OrderedProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
public class ReFundDTO {
    private String phoneNumber;
    private long orderId;
    private List<ProductRefundDTO> ProductRefund;
}
