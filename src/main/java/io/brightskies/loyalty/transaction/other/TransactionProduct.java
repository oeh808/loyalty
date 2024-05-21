package io.brightskies.loyalty.transaction.other;

import io.brightskies.loyalty.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionProduct {
    private Product product;
    private int quantity;
}
