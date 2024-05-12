package io.brightskies.loyalty.order.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.order.ProductsOrdered;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    @ElementCollection
    private List<ProductsOrdered> productsOrdered = new ArrayList<ProductsOrdered>();

    private Date orderDate;

    private float moneySpent;

    private int pointsSpent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pointsId")
    // Will end up changing if a refund is made on the order
    private PointsEntry pointsEarned;
}
