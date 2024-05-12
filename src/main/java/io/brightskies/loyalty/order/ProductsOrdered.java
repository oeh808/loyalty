package io.brightskies.loyalty.order;

import io.brightskies.loyalty.product.entity.Product;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductsOrdered {
    private Product product;
    private int quantity;
}
