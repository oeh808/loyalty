package io.brightskies.loyalty.order.service;

import java.util.List;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.order.OrderedProduct;
import io.brightskies.loyalty.order.dtos.OrderedProductDto;
import io.brightskies.loyalty.order.entity.Order;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;

public interface OrderService {
    Order placeOrder(List<OrderedProductDto> orderedProductsDto, float moneySpent, int pointsSpent, String phoneNumber);

    Order getOrder(long id);

    List<Order> getAllOrders();

    List<Order> getOrdersByCustomer(String phoneNumber);

    int calculatePointsEarned(List<OrderedProduct> orderedProducts, float moneySpent, int pointsSpent);

    List<PointsEntry> redeemPoints(int pointsSpent, Customer customer);

    void deleteOrder(long id);

    List<OrderedProduct> retrieveOrderedProducts(List<OrderedProductDto> orderedProductsDto);
}
