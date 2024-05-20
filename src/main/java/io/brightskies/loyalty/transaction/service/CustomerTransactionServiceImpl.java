package io.brightskies.loyalty.transaction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.service.CustomerService;
import io.brightskies.loyalty.transaction.entity.CustomerTransaction;
import io.brightskies.loyalty.transaction.repo.CustomerTransactionRepo;

@Service
public class CustomerTransactionServiceImpl implements CustomerTransactionService {
    private CustomerTransactionRepo customerTransactionRepo;
    private CustomerService customerService;

    public CustomerTransactionServiceImpl(CustomerTransactionRepo customerTransactionRepo,
            CustomerService customerService) {
        this.customerTransactionRepo = customerTransactionRepo;
        this.customerService = customerService;
    }

    @Override
    public List<CustomerTransaction> getCustomerTransactionsByCustomer(String phoneNumber) {
        Customer customer = customerService.getCustomer(phoneNumber);

        return customerTransactionRepo.findByCustomer(customer);
    }

}
