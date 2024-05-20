package io.brightskies.loyalty.refund.DTO;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ProductRefundDTO {
    private long productId;
    private int quantity;

}
