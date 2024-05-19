package io.brightskies.loyalty.order.dtos;

import java.sql.Date;
import java.util.List;

import io.brightskies.loyalty.order.OrderedProduct;

public record OrderReadingDto(long id, Date orderDate, List<OrderedProduct> orderedProducts,
        float moneySpent, int pointsSpent, String customerPhoneNumber, int pointsEarned) {

}
