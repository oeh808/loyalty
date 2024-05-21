package io.brightskies.loyalty.refund.entity;

import io.brightskies.loyalty.product.entity.Product;
import io.brightskies.loyalty.transaction.entity.TransactionProduct;
import jakarta.persistence.Entity;

@Entity
public class RefundedProduct extends TransactionProduct {
    public RefundedProduct(Product product, int quantity) {
        super(product, quantity);
    }
}
