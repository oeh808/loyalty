package io.brightskies.loyalty.order.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.brightskies.loyalty.order.entity.Order;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByCustomer_PhoneNumber(String phoneNumber);
}
