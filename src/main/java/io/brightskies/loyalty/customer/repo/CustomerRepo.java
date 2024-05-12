package io.brightskies.loyalty.customer.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.brightskies.loyalty.customer.entity.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhoneNumber(String phoneNumber);
}
