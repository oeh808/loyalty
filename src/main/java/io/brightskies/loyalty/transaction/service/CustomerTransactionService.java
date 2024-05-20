package io.brightskies.loyalty.transaction.service;

import java.util.List;

import io.brightskies.loyalty.transaction.entity.CustomerTransaction;

public interface CustomerTransactionService {
    List<CustomerTransaction> getCustomerTransactionsByCustomer(String phoneNumber);
}
