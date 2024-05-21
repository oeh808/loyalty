package io.brightskies.loyalty.transaction.dto;

import java.sql.Date;
import java.util.List;

import io.brightskies.loyalty.transaction.entity.TransactionProduct;

public record CustomerTransactionReadingDto(Date transactionDate, float moneyExchanged, int pointsExchanged,
                List<TransactionProduct> transactionProducts,
                int pointsEarned, String transactionType) {

}
