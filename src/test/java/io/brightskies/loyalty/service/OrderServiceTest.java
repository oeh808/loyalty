package io.brightskies.loyalty.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.brightskies.loyalty.constants.PointsConstants;
import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.exception.CustomerException;
import io.brightskies.loyalty.customer.service.CustomerService;
import io.brightskies.loyalty.order.OrderedProduct;
import io.brightskies.loyalty.order.entity.Order;
import io.brightskies.loyalty.order.exception.OrderException;
import io.brightskies.loyalty.order.exception.OrderExceptionMessages;
import io.brightskies.loyalty.order.repo.OrderRepo;
import io.brightskies.loyalty.order.service.OrderService;
import io.brightskies.loyalty.order.service.OrderServiceImpl;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.service.PointsEntryService;
import io.brightskies.loyalty.product.entity.Product;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class OrderServiceTest {
    @TestConfiguration
    static class ServiceTestConifg {
        @Bean
        @Autowired
        OrderService service(OrderRepo orderRepo, CustomerService customerService,
                PointsEntryService pointsEntryService) {
            return new OrderServiceImpl(orderRepo, customerService, pointsEntryService);
        }
    }

    @MockBean
    private OrderRepo orderRepo;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private PointsEntryService pointsEntryService;

    @Autowired
    private OrderService orderService;

    private static Customer customer;

    private static PointsEntry pointsEntry1;
    private static PointsEntry pointsEntry2;
    private static PointsEntry negativePointsEntry;
    private static List<PointsEntry> pointsEntries;

    private static OrderedProduct orderedProduct1;
    private static OrderedProduct orderedProduct2;
    private static List<OrderedProduct> orderedProducts;

    private static Order order;
    private static List<Order> orders;

    @BeforeAll
    public static void setUp() {
        orderedProduct1 = new OrderedProduct(new Product(1, "Eggs", 60, 30), 2, 0);
        orderedProduct2 = new OrderedProduct(new Product(2, "Milk", 32, 16), 4, 0);
        orderedProducts = new ArrayList<>();
        orderedProducts.add(orderedProduct1);
        orderedProducts.add(orderedProduct2);
    }

    @BeforeEach
    public void setUpMocks() {
        // --- PointsEntry Service ---
        pointsEntry1 = new PointsEntry(1, 150, Date.valueOf("2030-05-20"), customer);
        pointsEntry2 = new PointsEntry(2, 350, Date.valueOf("2030-06-20"), customer);
        // Negative points entry not added to list except in specific tests
        negativePointsEntry = new PointsEntry(3, -50, Date.valueOf("2030-05-19"), customer);

        pointsEntries = new ArrayList<>();
        pointsEntries.add(pointsEntry1);
        pointsEntries.add(pointsEntry2);

        when(pointsEntryService.createPointsEntry(any(PointsEntry.class))).thenReturn(pointsEntry1);

        when(pointsEntryService.getNonExpiredPointsEntriesByCustomer(customer)).thenReturn(pointsEntries);

        // --- Customer Service ---
        customer = new Customer(1, "01002936283", 500);
        doAnswer(invocation -> {
            return invocation.getArgument(0);
        }).when(customerService).createCustomer(any(Customer.class));

        when(customerService.getCustomer(customer.getPhoneNumber())).thenReturn(customer);
        when(customerService.getCustomer(customer.getPhoneNumber() + "0")).thenThrow(CustomerException.class);

        when(customerService.updateCustomerPointsTotal(anyLong(), anyInt())).thenReturn(customer);

        // --- Order Repo ---
        order = new Order(3, orderedProducts, Date.valueOf("2030-04-20"), 300, 300, customer, null);
        orders = new ArrayList<>();
        orders.add(order);

        doAnswer(invocation -> {
            return invocation.getArgument(0);
        }).when(orderRepo).save(any(Order.class));

        when(orderRepo.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepo.findById(order.getId() - 1)).thenReturn(Optional.empty());

        when(orderRepo.findAll()).thenReturn(orders);
        when(orderRepo.findByCustomer_PhoneNumber(customer.getPhoneNumber())).thenReturn(orders);
    }

    @Test
    public void placeOrder_AddsACustomerWhenPhoneNumberIsNotRegistered() {
        orderService.placeOrder(orderedProducts, 0, 0,
                customer.getPhoneNumber() + "0");
        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    public void placeOrder_ThrowsErrorWhenCustomerTriesToRedeemMorePointsThanTheyHave() {
        OrderException ex = assertThrows(OrderException.class,
                () -> {
                    orderService.placeOrder(orderedProducts, 0, customer.getTotalPoints() + 1,
                            customer.getPhoneNumber());
                });
        assertTrue(ex.getMessage().contains(OrderExceptionMessages.NOT_ENOUGH_POINTS));
    }

    @Test
    public void placeOrder_DoesNotRedeemPointsWhenPointsSpentIsMoreThanZero() {
        orderService.placeOrder(orderedProducts, 1, 0, customer.getPhoneNumber());
        OrderService orderServiceSpy = Mockito.spy(orderService);

        verify(orderServiceSpy, times(0)).redeemPoints(anyInt(), any(Customer.class));
    }

    @Test
    public void placeOrder_DoesNotAcquirePointsWhenMoneySpentIsMoreThanZero() {
        orderService.placeOrder(orderedProducts, 0, 1, customer.getPhoneNumber());
        OrderService orderServiceSpy = Mockito.spy(orderService);

        verify(orderServiceSpy, times(0)).calculatePointsEarned(anyList(), anyFloat(), anyInt());
    }

    @Test
    public void placeOrder_ReturnsCreatedOrderAndUpdatesCustomerAndPointEntry() {
        order.setPointsEntry(pointsEntry1);
        Order newOrder = orderService.placeOrder(orderedProducts, order.getMoneySpent(), order.getPointsSpent(),
                customer.getPhoneNumber());

        assertEquals(order.getMoneySpent(), newOrder.getMoneySpent());
        assertEquals(order.getPointsSpent(), newOrder.getPointsSpent());
        assertEquals(order.getPointsEntry(), newOrder.getPointsEntry());
        assertEquals(order.getCustomer(), newOrder.getCustomer());
        assertEquals(order.getOrderedProducts(), newOrder.getOrderedProducts());
    }

    @Test
    public void getOrder_RetrievesOrderWhenGivenValidId() {
        assertEquals(order, orderService.getOrder(order.getId()));
    }

    @Test
    public void getOrder_ThrowsErrorWhenGivenInvalidId() {
        OrderException ex = assertThrows(OrderException.class,
                () -> {
                    orderService.getOrder(order.getId() - 1);
                });
        assertTrue(ex.getMessage().contains(OrderExceptionMessages.ORDER_NOT_FOUND));
    }

    @Test
    public void getAllOrders_ReturnsListOfOrders() {
        assertEquals(orders, orderService.getAllOrders());
    }

    @Test
    public void getOrdersByCustomer_ReturnsListOfOrderWithAssociatedCustomer() {
        assertEquals(orders, orderService.getOrdersByCustomer(customer.getPhoneNumber()));
    }

    @Test
    public void calculatePointsEarned_ReturnsCorrectCalculation() {
        float pointsConvertedToMoney = order.getPointsSpent() * PointsConstants.WORTH_OF_ONE_POINT;
        float moneySpentRatio = order.getMoneySpent() / (order.getMoneySpent() + pointsConvertedToMoney);

        int expectedPoints = 0;
        expectedPoints += orderedProduct1.getProduct().getPointsValue() * moneySpentRatio
                * orderedProduct1.getQuantity();
        expectedPoints += orderedProduct2.getProduct().getPointsValue() * moneySpentRatio
                * orderedProduct2.getQuantity();

        assertEquals(expectedPoints,
                orderService.calculatePointsEarned(orderedProducts, order.getMoneySpent(), order.getPointsSpent()));
    }

    @Test
    public void redeemPoints_SubtractsPointsCorrectlyWhenPointsSpentIsLessThanAllPointsEntries() {
        int originalPointsInEntry1 = pointsEntry1.getNumOfPoints();
        int originalPointsInEntry2 = pointsEntry2.getNumOfPoints();
        int pointsToSpend = (int) (pointsEntry1.getNumOfPoints() * 0.5f);
        doAnswer(invocation -> {
            pointsEntry1.setNumOfPoints(invocation.getArgument(1));
            return null;
        }).when(pointsEntryService).updatePointsInEntry(pointsEntry1.getId(),
                pointsEntry1.getNumOfPoints() - pointsToSpend);

        orderService.redeemPoints(pointsToSpend, customer);

        assertEquals(originalPointsInEntry1 - pointsToSpend, pointsEntry1.getNumOfPoints());
        assertEquals(originalPointsInEntry2, pointsEntry2.getNumOfPoints());
    }

    @Test
    public void redeemPoints_SubtractsPointsCorrectlyWhenPointsSpentIsMoreThanOnePointsEntryIncludingNegativeEntry() {
        pointsEntries = new ArrayList<>();
        pointsEntries.add(negativePointsEntry);
        pointsEntries.add(pointsEntry1);
        pointsEntries.add(pointsEntry2);
        when(pointsEntryService.getNonExpiredPointsEntriesByCustomer(customer)).thenReturn(pointsEntries);

        int originalPointsInEntry1 = pointsEntry1.getNumOfPoints();
        int originalPointsInNegativeEntry = negativePointsEntry.getNumOfPoints();
        int pointsToSpend = (int) (pointsEntry1.getNumOfPoints() * 0.5f) - (-negativePointsEntry.getNumOfPoints());

        doAnswer(invocation -> {
            negativePointsEntry.setNumOfPoints(invocation.getArgument(1));
            return null;
        }).when(pointsEntryService).updatePointsInEntry(negativePointsEntry.getId(), 0);

        doAnswer(invocation -> {
            pointsEntry1.setNumOfPoints(invocation.getArgument(1));
            return null;
        }).when(pointsEntryService).updatePointsInEntry(pointsEntry1.getId(),
                pointsEntry1.getNumOfPoints() - (pointsToSpend + (-originalPointsInNegativeEntry)));

        orderService.redeemPoints(pointsToSpend, customer);

        assertEquals(0, negativePointsEntry.getNumOfPoints());
        assertEquals(originalPointsInEntry1 - (pointsToSpend + (-originalPointsInNegativeEntry)),
                pointsEntry1.getNumOfPoints());

    }

    @Test
    public void redeemPoints_SubtractsPointsCorrectlyWhenPointsSpentIsAllPointsEntries() {
        int pointsToSpend = pointsEntry1.getNumOfPoints() + pointsEntry2.getNumOfPoints();
        doAnswer(invocation -> {
            pointsEntry1.setNumOfPoints(invocation.getArgument(1));
            return null;
        }).when(pointsEntryService).updatePointsInEntry(pointsEntry1.getId(), 0);

        doAnswer(invocation -> {
            pointsEntry2.setNumOfPoints(invocation.getArgument(1));
            return null;
        }).when(pointsEntryService).updatePointsInEntry(pointsEntry2.getId(), 0);

        orderService.redeemPoints(pointsToSpend, customer);
        assertEquals(0, pointsEntry1.getNumOfPoints());
        assertEquals(0, pointsEntry2.getNumOfPoints());
    }

    @Test
    public void deleteOrder_CallsDeleteWhenGivenValidId() {
        orderService.deleteOrder(order.getId());

        verify(orderRepo, times(1)).deleteById(order.getId());
    }

    @Test
    public void deleteOrder_ThrowsErrorWhenGivenInvalidId() {
        OrderException ex = assertThrows(OrderException.class,
                () -> {
                    orderService.deleteOrder(order.getId() - 1);
                });
        assertTrue(ex.getMessage().contains(OrderExceptionMessages.ORDER_NOT_FOUND));

        verify(orderRepo, times(0)).delete(any(Order.class));
    }
}
