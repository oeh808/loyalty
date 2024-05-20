package io.brightskies.loyalty.customer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.exception.CustomerException;
import io.brightskies.loyalty.customer.exception.CustomerExceptionMessages;
import io.brightskies.loyalty.customer.repo.CustomerRepo;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepo customerRepo;

    public CustomerServiceImpl(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        log.info("Running createCustomer(" + customer.toString() + ") in CustomerServiceImpl...");
        return customerRepo.save(customer);
    }

    @Override
    public Customer getCustomer(long id) {
        log.info("Running getCustomer(" + id + ") in CustomerServiceImpl...");
        Optional<Customer> opCustomer = customerRepo.findById(id);
        if (opCustomer.isPresent()) {
            return opCustomer.get();
        } else {
            log.error("Invalid customer id: " + id + "!");
            throw new CustomerException(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_ID);
        }
    }

    @Override
    public Customer getCustomer(String phoneNumber) {
        log.info("Running getCustomer(" + phoneNumber + ") in CustomerServiceImpl...");
        Optional<Customer> opCustomer = customerRepo.findByPhoneNumber(phoneNumber);
        if (opCustomer.isPresent()) {
            return opCustomer.get();
        } else {
            log.error("Invalid customer phone number: " + phoneNumber + "!");
            throw new CustomerException(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_PHONE_NUMBER);
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        log.info("Running getAllCustomers() in CustomerServiceImpl...");
        return customerRepo.findAll();
    }

    @Override
    public Customer updateCustomerPhoneNumber(long id, String phoneNumber) {
        log.info("Running updateCustomerPhoneNumber(" + id + ", " + phoneNumber + ") in CustomerServiceImpl...");
        log.info("Checking customer id exists...");
        Optional<Customer> opCustomer = customerRepo.findById(id);
        if (opCustomer.isEmpty()) {
            log.error("Invalid customer id: " + id + "!");
            throw new CustomerException(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_ID);
        } else {
            log.info("Updating customer phone number...");
            Customer customer = opCustomer.get();
            customer.setPhoneNumber(phoneNumber);

            log.info("Saving updated customer...");
            return customerRepo.save(customer);
        }
    }

    @Override
    public Customer updateCustomerPointsTotal(long id, int pointsTotal) {
        log.info("Running updateCustomerPointsTotal(" + id + ", " + pointsTotal + ") in CustomerServiceImpl...");
        log.info("Checking customer id exists...");
        Optional<Customer> opCustomer = customerRepo.findById(id);
        if (opCustomer.isEmpty()) {
            log.error("Invalid customer id: " + id + "!");
            throw new CustomerException(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_ID);
        } else {
            log.info("Updating customer points total...");
            Customer customer = opCustomer.get();
            customer.setTotalPoints(pointsTotal);

            log.info("Saving updated customer...");
            return customerRepo.save(customer);
        }
    }

    @Override
    public void deleteCustomer(long id) {
        log.info("Running deleteCustomer(" + id + ") in CustomerServiceImpl...");
        log.info("Checking customer id exists...");
        Optional<Customer> opCustomer = customerRepo.findById(id);
        if (opCustomer.isPresent()) {
            log.info("Deleting customer...");
            customerRepo.deleteById(id);
        } else {
            log.error("Invalid customer id: " + id + "!");
            throw new CustomerException(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_ID);
        }
    }

}
