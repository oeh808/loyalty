package io.brightskies.loyalty.transaction.dto;

import java.sql.Date;

public record CustomerTransactionReadingDto(Date transactionDate, float moneyExchanged, int pointsExchanged,
                int pointsEarned, String transactionType) {

}
