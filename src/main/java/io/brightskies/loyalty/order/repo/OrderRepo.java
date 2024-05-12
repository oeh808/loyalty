package io.brightskies.loyalty.order.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.brightskies.loyalty.order.entity.Order;

public interface OrderRepo extends JpaRepository<Order, Integer> {

}
