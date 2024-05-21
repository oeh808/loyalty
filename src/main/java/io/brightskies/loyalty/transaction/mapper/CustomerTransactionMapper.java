package io.brightskies.loyalty.transaction.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import io.brightskies.loyalty.transaction.dto.CustomerTransactionReadingDto;
import io.brightskies.loyalty.transaction.entity.CustomerTransaction;
import io.brightskies.loyalty.transaction.exception.TransactionException;
import io.brightskies.loyalty.transaction.exception.TransactionExceptionMessages;
import io.brightskies.loyalty.transaction.other.TransactionProduct;

@Component
public class CustomerTransactionMapper {
    // To Dto:
    public CustomerTransactionReadingDto toDto(CustomerTransaction customerTransaction,
            List<TransactionProduct> transactionProducts) {
        CustomerTransactionReadingDto dto = new CustomerTransactionReadingDto(customerTransaction.getTransactionDate(),
                customerTransaction.getMoneyExchanged(), customerTransaction.getPointsExchanged(),
                transactionProducts,
                customerTransaction.getPointsEarned(), customerTransaction.getTransactionType());

        return dto;
    }

    public List<CustomerTransactionReadingDto> toDto(List<CustomerTransaction> customerTransactions,
            List<List<TransactionProduct>> transactionProductsList) {
        if (customerTransactions.size() != transactionProductsList.size()) {
            throw new TransactionException(TransactionExceptionMessages.NUMBER_OF_TRANSACTION_DONT_MATCH);
        }
        List<CustomerTransactionReadingDto> dtos = new ArrayList<>();
        for (int i = 0; i < customerTransactions.size(); i++) {
            dtos.add(toDto(customerTransactions.get(i), transactionProductsList.get(i)));
        }

        return dtos;
    }
}
