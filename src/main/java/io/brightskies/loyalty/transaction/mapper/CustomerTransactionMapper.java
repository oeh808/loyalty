package io.brightskies.loyalty.transaction.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import io.brightskies.loyalty.transaction.dto.CustomerTransactionReadingDto;
import io.brightskies.loyalty.transaction.entity.CustomerTransaction;

@Component
public class CustomerTransactionMapper {
    // To Dto:
    public CustomerTransactionReadingDto toDto(CustomerTransaction customerTransaction) {
        CustomerTransactionReadingDto dto = new CustomerTransactionReadingDto(customerTransaction.getTransactionDate(),
                customerTransaction.getMoneyExchanged(), customerTransaction.getPointsExchanged(),
                customerTransaction.getPointsEarned(), customerTransaction.getTransactionType());

        return dto;
    }

    public List<CustomerTransactionReadingDto> toDto(List<CustomerTransaction> customerTransactions) {
        List<CustomerTransactionReadingDto> dtos = new ArrayList<>();
        for (CustomerTransaction customerTransaction : customerTransactions) {
            dtos.add(toDto(customerTransaction));
        }

        return dtos;
    }
}
