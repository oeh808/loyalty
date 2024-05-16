package io.brightskies.loyalty.refund.entity;

import java.sql.Date;
import java.util.List;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.order.OrderedProduct;
import io.brightskies.loyalty.order.entity.Order;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    Customer customer;

    @Embedded
    @ElementCollection
    private List<OrderedProduct> productsRefunded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Order order;

    private float moneyRefunded;

    private int pointsRefunded;

    private Date refundDate;

    public Refund(Customer customer, Order order, List<OrderedProduct> orderedProducts, float moneyRefunded, int pointsRefunded, Date date) {
        this.customer = customer;
        this.order = order;
        this.productsRefunded = orderedProducts;
        this.moneyRefunded = moneyRefunded;
        this.pointsRefunded = pointsRefunded;
        this.refundDate = date;
    }
}
