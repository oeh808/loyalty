package io.brightskies.loyalty.transaction.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.brightskies.loyalty.transaction.dto.CustomerTransactionReadingDto;
import io.brightskies.loyalty.transaction.mapper.CustomerTransactionMapper;
import io.brightskies.loyalty.transaction.service.CustomerTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@Tag(name = "Customer Transactions", description = "Controller for handling mappings for customer transactions (orders and refunds)")
@RequestMapping("/transactions")
public class CustomerTransactionController {
    private CustomerTransactionService customerTransactionService;
    private CustomerTransactionMapper customerTransactionMapper;

    public CustomerTransactionController(CustomerTransactionService customerTransactionService,
            CustomerTransactionMapper customerTransactionMapper) {
        this.customerTransactionService = customerTransactionService;
        this.customerTransactionMapper = customerTransactionMapper;
    }

    @Operation(description = "GET endpoint for retrieving all transactions associated with a customer's phone number" +
            "\n\n Returns a list of transactions as a list of CustomerTransactionReadingDto.", summary = "Get Customer's Transactions")
    @GetMapping("/{phoneNumber}")
    public List<CustomerTransactionReadingDto> getTransactionsByCustomer(
            @Parameter(in = ParameterIn.PATH, name = "phoneNumber", description = "Phone Number") @PathVariable String phoneNumber) {
        List<CustomerTransactionReadingDto> dtos = customerTransactionMapper
                .toDto(customerTransactionService.getCustomerTransactionsByCustomer(phoneNumber));

        return dtos;
    }

}
