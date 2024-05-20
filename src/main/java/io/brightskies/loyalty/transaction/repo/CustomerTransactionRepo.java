package io.brightskies.loyalty.transaction.repo;

import io.brightskies.loyalty.transaction.entity.CustomerTransaction;
import java.util.List;
import io.brightskies.loyalty.customer.entity.Customer;

public interface CustomerTransactionRepo extends ReadOnlyRepo<CustomerTransaction, Long> {
    List<CustomerTransaction> findByCustomer(Customer customer);
}
