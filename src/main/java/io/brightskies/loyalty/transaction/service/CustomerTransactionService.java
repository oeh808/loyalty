package io.brightskies.loyalty.transaction.service;

import java.util.List;

import io.brightskies.loyalty.transaction.dto.CustomerTransactionReadingDto;

public interface CustomerTransactionService {
    List<CustomerTransactionReadingDto> getCustomerTransactionsByCustomer(String phoneNumber);
}
