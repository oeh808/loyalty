package io.brightskies.loyalty.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.repo.CustomerRepo;

@ActiveProfiles("test")
@DataJpaTest
public class CustomerRepoTest {
    @Autowired
    private CustomerRepo customerRepo;

    private static Customer customer;

    @BeforeAll
    public static void setUp() {
        customer = new Customer(1, "01009007723", 0);
    }

    @BeforeEach
    public void setUpForEach() {
        customer = customerRepo.save(customer);
    }

    @AfterEach
    public void tearDownForEach() {
        customerRepo.deleteAll();
    }

    @Test
    public void findByPhoneNumber_ReturnsOptionalCustomerGivenRegisteredPhoneNumber() {
        Optional<Customer> opCustomer = customerRepo.findByPhoneNumber(customer.getPhoneNumber());

        assertEquals(customer, opCustomer.get());
    }

    @Test
    public void findByPhoneNumber_ReturnsEmptyOptionalGivenUnregisteredPhoneNumber() {
        Optional<Customer> opCustomer = customerRepo.findByPhoneNumber(customer.getPhoneNumber() + "0");

        assertTrue(opCustomer.isEmpty());
    }
}
