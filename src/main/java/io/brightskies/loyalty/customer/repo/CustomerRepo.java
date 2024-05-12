package io.brightskies.loyalty.customer.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.brightskies.loyalty.customer.entity.Customer;

public interface CustomerRepo extends JpaRepository<Customer, String> {

}
