package io.brightskies.loyalty.customer.service;

import java.util.List;

import io.brightskies.loyalty.customer.entity.Customer;

public interface CustomerService {
    Customer createCustomer(Customer customer);

    Customer getCustomer(long id);

    Customer getCustomer(String phoneNumber);

    List<Customer> getAllCustomers();

    Customer updateCustomerPhoneNumber(long id, String phoneNumber);

    Customer updateCustomerPointsTotal(long id, int pointsTotal);

    void deleteCustomer(long id);
}
