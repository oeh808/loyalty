package io.brightskies.loyalty.order.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.brightskies.loyalty.order.dtos.OrderCreationDto;
import io.brightskies.loyalty.order.dtos.OrderReadingDto;
import io.brightskies.loyalty.order.entity.Order;
import io.brightskies.loyalty.order.mapper.OrderMapper;
import io.brightskies.loyalty.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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
        private OrderMapper orderMapper;

        public OrderController(OrderService orderService, OrderMapper orderMapper) {
                this.orderService = orderService;
                this.orderMapper = orderMapper;
        }

        @Operation(description = "POST endpoint for creating an order" +
                        "\n\n Returns the order created.", summary = "Create an Order")
        @PostMapping()
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Must conform to required properties of OrderCreationDto")
        public Order placeOrder(@Valid @RequestBody OrderCreationDto dto) {

                return orderService.placeOrder(dto.orderedProductsDto(), dto.moneySpent(), dto.pointsSpent(),
                                dto.phoneNumber());
        }

        @Operation(description = "GET endpoint for retrieving a single order given its id" +
                        "\n\n Returns an instance of type order.", summary = "Get Single Order")
        @GetMapping("/{id}")
        public OrderReadingDto getSingleOrder(
                        @Parameter(in = ParameterIn.PATH, name = "id", description = "Order ID") @PathVariable long id) {
                return orderMapper.toDto(orderService.getOrder(id));
        }

        @Operation(description = "GET endpoint for retrieving ALL orders" +
                        "\n\n Returns a list of orders.", summary = "Get All Orders")
        @GetMapping()
        public List<OrderReadingDto> getAllOrders() {
                return orderMapper.toDtos(orderService.getAllOrders());
        }

        @Operation(description = "GET endpoint for retrieving all orders associated with a customer's phone number" +
                        "\n\n Returns a list of orders.", summary = "Get Customer's Orders")
        @GetMapping("/customerOrders/{phoneNumber}")
        public List<OrderReadingDto> getAllOrdersFromCustomer(
                        @Parameter(in = ParameterIn.PATH, name = "phoneNumber", description = "Phone Number") @PathVariable String phoneNumber) {
                return orderMapper.toDtos(orderService.getOrdersByCustomer(phoneNumber));
        }

        @Operation(description = "DELETE endpoint for deleting a single order given its id" +
                        "\n\n Returns a String confirming the delete was succcessful.", summary = "Delete Order")
        @DeleteMapping("/{id}")
        public String deleteOrder(
                        @Parameter(in = ParameterIn.PATH, name = "id", description = "Order ID") @PathVariable long id) {
                orderService.deleteOrder(id);
                return "Order deleted successfully";
        }

}
