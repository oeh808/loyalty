package io.brightskies.loyalty.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.exception.CustomerException;
import io.brightskies.loyalty.customer.exception.CustomerExceptionMessages;
import io.brightskies.loyalty.customer.repo.CustomerRepo;
import io.brightskies.loyalty.customer.service.CustomerService;
import io.brightskies.loyalty.customer.service.CustomerServiceImpl;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class CustomerServiceTest {
    @TestConfiguration
    static class ServiceTestConifg {
        @Bean
        @Autowired
        CustomerService service(CustomerRepo customerRepo) {
            return new CustomerServiceImpl(customerRepo);
        }
    }

    @MockBean
    private CustomerRepo customerRepo;

    @Autowired
    private CustomerService customerService;

    private static Customer customer;

    private static List<Customer> customers;

    @BeforeAll
    public static void setUp() {
        customer = new Customer(1, "01009007723", 50);

        customers = new ArrayList<Customer>();
        customers.add(customer);
    }

    @BeforeEach
    public void setUpMocks() {
        when(customerRepo.save(customer)).thenReturn(customer);

        when(customerRepo.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(customerRepo.findById(customer.getId() - 1)).thenReturn(Optional.empty());

        when(customerRepo.findByPhoneNumber(customer.getPhoneNumber())).thenReturn(Optional.of(customer));
        when(customerRepo.findByPhoneNumber(customer.getPhoneNumber() + "0")).thenReturn(Optional.empty());

        when(customerRepo.findAll()).thenReturn(customers);
    }

    @Test
    public void createCustomer_ReturnsCreatedCustomer() {
        assertEquals(customer, customerService.createCustomer(customer));
    }

    @Test
    public void getCustomer_RetrievesCustomerWhenGivenValidId() {
        assertEquals(customer, customerService.getCustomer(customer.getId()));
    }

    @Test
    public void getCustomer_ThrowsErrorWhenGivenInvalidId() {
        CustomerException ex = assertThrows(CustomerException.class,
                () -> {
                    customerService.getCustomer(customer.getId() - 1);
                });
        assertTrue(ex.getMessage().contains(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_ID));
    }

    @Test
    public void getCustomer_RetrievesCustomerWhenGivenValidPhoneNumber() {
        assertEquals(customer, customerService.getCustomer(customer.getPhoneNumber()));
    }

    @Test
    public void getCustomer_ThrowsErrorWhenGivenInvalidPhoneNumber() {
        CustomerException ex = assertThrows(CustomerException.class,
                () -> {
                    customerService.getCustomer(customer.getPhoneNumber() + "0");
                });
        assertTrue(ex.getMessage().contains(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_PHONE_NUMBER));
    }

    @Test
    public void getAllCustomers_ReturnsAListOfCustomers() {
        assertEquals(customers, customerService.getAllCustomers());
    }

    @Test
    public void updateCustomerPhoneNumber_ReturnsUpdatedCustomerWhenGivenValidId() {
        Customer updatedCustomer = new Customer(customer.getId(), "11009007723", customer.getTotalPoints());
        when(customerRepo.save(updatedCustomer)).thenReturn(updatedCustomer);

        assertEquals(updatedCustomer,
                customerService.updateCustomerPhoneNumber(customer.getId(), updatedCustomer.getPhoneNumber()));

    }

    @Test
    public void updateCustomerPhoneNumber_ThrowsErrorWhenGivenInvalidId() {
        CustomerException ex = assertThrows(CustomerException.class,
                () -> {
                    customerService.updateCustomerPhoneNumber(customer.getId() - 1, customer.getPhoneNumber());
                });
        assertTrue(ex.getMessage().contains(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_ID));

        verify(customerRepo, times(0)).save(any(Customer.class));
    }

    @Test
    public void updateCustomerPointsTotal_ReturnsUpdatedCustomerWhenGivenValidId() {
        Customer updatedCustomer = new Customer(customer.getId(), customer.getPhoneNumber(),
                customer.getTotalPoints() + 1);
        when(customerRepo.save(updatedCustomer)).thenReturn(updatedCustomer);

        assertEquals(updatedCustomer,
                customerService.updateCustomerPointsTotal(customer.getId(), updatedCustomer.getTotalPoints()));
    }

    @Test
    public void updateCustomerPointsTotal_ThrowsErrorWhenGivenInvalidId() {
        CustomerException ex = assertThrows(CustomerException.class,
                () -> {
                    customerService.updateCustomerPointsTotal(customer.getId() - 1, customer.getTotalPoints());
                });
        assertTrue(ex.getMessage().contains(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_ID));

        verify(customerRepo, times(0)).save(any(Customer.class));
    }

    @Test
    public void deleteCustomer_CallsDeleteWhenGivenValidId() {
        customerService.deleteCustomer(customer.getId());

        verify(customerRepo, times(1)).deleteById(customer.getId());
    }

    @Test
    public void deleteCustomer_ThrowsErrorWhenGivenInvalidId() {
        CustomerException ex = assertThrows(CustomerException.class,
                () -> {
                    customerService.deleteCustomer(customer.getId() - 1);
                });
        assertTrue(ex.getMessage().contains(CustomerExceptionMessages.CUSTOMER_NOT_FOUND_PHONE_NUMBER));

        verify(customerRepo, times(0)).deleteById(anyLong());
    }
}
