package io.brightskies.loyalty.order.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.brightskies.loyalty.order.dtos.OrderCreationDto;
import io.brightskies.loyalty.order.entity.Order;
import io.brightskies.loyalty.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@Tag(name = "Orders", description = "Controller for handling mappings for orders")
@RequestMapping("/orders")
public class OrderController {
    private OrderService orderService;

    @PostMapping()
    public Order placeOrder(@RequestBody OrderCreationDto dto) {
        return orderService.placeOrder(dto.orderedProducts(), dto.moneySpent(), dto.pointsSpent(), dto.phoneNumber());
    }

    @GetMapping("/{id}")
    public Order getSingleOrder(@PathVariable long id) {
        return orderService.getOrder(id);
    }

    @GetMapping()
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/customerOrders/{phoneNumber}")
    public List<Order> getAllOrdersFromCustomer(@PathVariable String phoneNumber) {
        return orderService.getOrdersByCustomer(phoneNumber);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable long id) {
        orderService.deleteOrder(id);
        return "Order deleted successfully";
    }

}
