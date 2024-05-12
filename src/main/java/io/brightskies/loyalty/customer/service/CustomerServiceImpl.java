package io.brightskies.loyalty.customer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.exception.CustomerException;
import io.brightskies.loyalty.customer.exception.CustomerExceptionMessages;
import io.brightskies.loyalty.customer.repo.CustomerRepo;

@Service
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepo customerRepo;

    public CustomerServiceImpl(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return customerRepo.save(customer);
    }

    @Override
    public Customer getCustomer(long id) {
        Optional<Customer> opCustomer = customerRepo.findById(id);
        if (opCustomer.isPresent()) {
            return opCustomer.get();
        } else {
            throw new CustomerException(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_ID);
        }
    }

    @Override
    public Customer getCustomer(String phoneNumber) {
        Optional<Customer> opCustomer = customerRepo.findByPhoneNumber(phoneNumber);
        if (opCustomer.isPresent()) {
            return opCustomer.get();
        } else {
            throw new CustomerException(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_PHONE_NUMBER);
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    @Override
    public Customer updateCustomerPhoneNumber(long id, String phoneNumber) {
        Optional<Customer> opCustomer = customerRepo.findById(id);
        if (opCustomer.isEmpty()) {
            throw new CustomerException(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_ID);
        } else {
            Customer customer = opCustomer.get();
            customer.setPhoneNumber(phoneNumber);

            return customerRepo.save(customer);
        }
    }

    @Override
    public Customer updateCustomerPointsTotal(long id, int pointsTotal) {
        Optional<Customer> opCustomer = customerRepo.findById(id);
        if (opCustomer.isEmpty()) {
            throw new CustomerException(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_ID);
        } else {
            Customer customer = opCustomer.get();
            customer.setTotalPoints(pointsTotal);

            return customerRepo.save(customer);
        }
    }

    @Override
    public void deleteCustomer(long id) {
        Optional<Customer> opCustomer = customerRepo.findById(id);
        if (opCustomer.isPresent()) {
            customerRepo.deleteById(id);
        } else {
            throw new CustomerException(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_PHONE_NUMBER);
        }
    }

}
