package io.brightskies.loyalty.order.entity;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.order.OrderedProduct;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
    private List<OrderedProduct> orderedProducts = new ArrayList<OrderedProduct>();

    private Date orderDate;

    private float moneySpent;

    private int pointsSpent;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "pointEntriesRedeemedFrom", joinColumns = @JoinColumn(name = "orderId"), inverseJoinColumns = @JoinColumn(name = "pointsEntryId"))
    private List<PointsEntry> entries;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private Customer customer;

    private int pointsEarned;
}
