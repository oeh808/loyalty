package io.brightskies.loyalty.transaction.entity;

import java.sql.Date;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import io.brightskies.loyalty.customer.entity.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Immutable
@Getter
@Subselect("select * from CUSTOMER_TRANSACTION")
public class CustomerTransaction {
    @Id
    private String id;

    private Date transactionDate;

    private float moneyExchanged;

    private int pointsExchanged;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private Customer customer;

    private String transactionType;

    private long transactionId;

    private int pointsEarned;
}
