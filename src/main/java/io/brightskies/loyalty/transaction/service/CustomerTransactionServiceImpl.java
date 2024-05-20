package io.brightskies.loyalty.transaction.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.service.CustomerService;
import io.brightskies.loyalty.transaction.entity.CustomerTransaction;
import io.brightskies.loyalty.transaction.repo.CustomerTransactionRepo;
import lombok.extern.log4j.Log4j2;

@Log4j2
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
        log.info("Running getCustomerTransactionsByCustomer(" + phoneNumber + ") in CustomerTransactionServiceImpl...");
        Customer customer = customerService.getCustomer(phoneNumber);

        log.info("Searching for transaction...");
        return customerTransactionRepo.findByCustomer(customer);
    }

}
