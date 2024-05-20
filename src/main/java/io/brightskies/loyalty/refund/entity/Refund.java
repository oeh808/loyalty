package io.brightskies.loyalty.refund.entity;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.order.entity.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

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
    private List<RefundedProduct> productsRefunded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Order order;

    private float moneyRefunded;

    private int pointsRefunded;

    private Date refundDate;


}
