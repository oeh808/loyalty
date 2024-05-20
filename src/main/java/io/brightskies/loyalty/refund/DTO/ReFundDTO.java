package io.brightskies.loyalty.refund.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
public class ReFundDTO {
    private String phoneNumber;
    private long orderId;
    private List<ProductRefundDTO> ProductRefund;
}
