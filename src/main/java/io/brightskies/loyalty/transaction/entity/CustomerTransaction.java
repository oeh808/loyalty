package io.brightskies.loyalty.transaction.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import io.brightskies.loyalty.customer.entity.Customer;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
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
    private String transactionId;

    private Date transactionDate;

    private float moneyExchanged;

    private int pointsExchanged;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private Customer customer;

    private String transactionType;

    @Embedded
    @ElementCollection
    private List<TransactionProduct> transactionProducts = new ArrayList<>();

    private int pointsEarned;
}
