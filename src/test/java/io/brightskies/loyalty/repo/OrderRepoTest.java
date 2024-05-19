package io.brightskies.loyalty.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.repo.CustomerRepo;
import io.brightskies.loyalty.order.entity.Order;
import io.brightskies.loyalty.order.repo.OrderRepo;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.repo.PointsEntryRepo;

@ActiveProfiles("test")
@DataJpaTest
public class OrderRepoTest {
    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private PointsEntryRepo pointsEntryRepo;

    @Autowired
    private OrderRepo orderRepo;

    private static Customer customer;
    private static PointsEntry pointsEntry;
    private static Order order1;
    private static Order order2;

    @BeforeAll
    public static void setUp() {
        customer = new Customer(0, "01002029938", 204);
        pointsEntry = new PointsEntry(0, 200, Date.valueOf("2030-06-17"), null, false);
        order1 = new Order(0, null, Date.valueOf("2030-04-17"), 0, 0, null, null);
        order2 = new Order(0, null, Date.valueOf("2030-04-17"), 0, 0, null, null);
    }

    @BeforeEach
    public void setUpForEach() {
        customer = customerRepo.save(customer);

        pointsEntry.setCustomer(customer);
        pointsEntry = pointsEntryRepo.save(pointsEntry);

        order1.setCustomer(customer);
        order1.setPointsEntry(pointsEntry);
        order1 = orderRepo.save(order1);

        order2.setCustomer(customer);
        order2.setPointsEntry(pointsEntry);
        order2 = orderRepo.save(order2);
    }

    @AfterEach
    public void tearDownForEach() {
        orderRepo.deleteAll();
        pointsEntryRepo.deleteAll();
        customerRepo.deleteAll();
    }

    @Test
    public void findByCustomer_PhoneNumber_ReturnsListOfOrdersGivenPhoneNumberOfExistingCustomer() {
        List<Order> expectedOrders = new ArrayList<>();
        expectedOrders.add(order1);
        expectedOrders.add(order2);

        assertEquals(expectedOrders, orderRepo.findByCustomer_PhoneNumber(customer.getPhoneNumber()));
    }

    @Test
    public void findByCustomer_PhoneNumber_ReturnsEmptyListGivenPhoneNumberNotAssociatedWithCustomer() {
        List<Order> expectedOrders = new ArrayList<>();

        assertEquals(expectedOrders, orderRepo.findByCustomer_PhoneNumber(customer.getPhoneNumber() + "0"));
    }
}
