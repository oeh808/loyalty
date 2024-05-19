package io.brightskies.loyalty.order.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import io.brightskies.loyalty.order.dtos.OrderReadingDto;
import io.brightskies.loyalty.order.entity.Order;

@Component
public class OrderMapper {
    // --- To Dto ---
    public OrderReadingDto toDto(Order order) {
        OrderReadingDto dto = new OrderReadingDto(order.getId(), order.getOrderDate(), order.getOrderedProducts(),
                order.getMoneySpent(), order.getPointsSpent(), order.getCustomer().getPhoneNumber(),
                order.getPointsEarned());

        return dto;
    }

    public List<OrderReadingDto> toDtos(List<Order> orders) {
        List<OrderReadingDto> dtos = new ArrayList<>();

        for (Order order : orders) {
            dtos.add(toDto(order));
        }

        return dtos;
    }
}
