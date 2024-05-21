package io.brightskies.loyalty.transaction.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.brightskies.loyalty.transaction.dto.CustomerTransactionReadingDto;
import io.brightskies.loyalty.transaction.service.CustomerTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Log4j2
@RestController
@Tag(name = "Customer Transactions", description = "Controller for handling mappings for customer transactions (orders and refunds)")
@RequestMapping("/transactions")
public class CustomerTransactionController {
    private CustomerTransactionService customerTransactionService;

    public CustomerTransactionController(CustomerTransactionService customerTransactionService) {
        this.customerTransactionService = customerTransactionService;
    }

    @Operation(description = "GET endpoint for retrieving all transactions associated with a customer's phone number" +
            "\n\n Returns a list of transactions as a list of CustomerTransactionReadingDto.", summary = "Get Customer's Transactions")
    @GetMapping("/{phoneNumber}")
    public List<CustomerTransactionReadingDto> getTransactionsByCustomer(
            @Parameter(in = ParameterIn.PATH, name = "phoneNumber", description = "Phone Number") @PathVariable String phoneNumber) {
        log.info("Recieved: GET request to /transactions/" + phoneNumber);
        return customerTransactionService.getCustomerTransactionsByCustomer(phoneNumber);
    }

}
