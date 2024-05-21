package io.brightskies.loyalty.order;

import io.brightskies.loyalty.product.entity.Product;
import io.brightskies.loyalty.transaction.entity.TransactionProduct;
import jakarta.persistence.Entity;

@Entity
public class OrderedProduct extends TransactionProduct {
    private int refundedQuantity = 0;

    public OrderedProduct(Product product, int quantity, int refundedQuantity) {
        super(product, quantity);
        this.refundedQuantity = refundedQuantity;
    }

    public int getRefundedQuantity() {
        return refundedQuantity;
    }

    public void setRefundedQuantity(int refundedQuantity) {
        this.refundedQuantity = refundedQuantity;
    }
}
