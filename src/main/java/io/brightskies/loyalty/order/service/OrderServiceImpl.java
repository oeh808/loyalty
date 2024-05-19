package io.brightskies.loyalty.order.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import io.brightskies.loyalty.constants.PointsConstants;
import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.exception.CustomerException;
import io.brightskies.loyalty.customer.service.CustomerService;
import io.brightskies.loyalty.order.OrderedProduct;
import io.brightskies.loyalty.order.entity.Order;
import io.brightskies.loyalty.order.exception.OrderException;
import io.brightskies.loyalty.order.exception.OrderExceptionMessages;
import io.brightskies.loyalty.order.repo.OrderRepo;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.service.PointsEntryService;

@Service
public class OrderServiceImpl implements OrderService {
    private OrderRepo orderRepo;
    private CustomerService customerService;
    private PointsEntryService pointsEntryService;

    public OrderServiceImpl(OrderRepo orderRepo, CustomerService customerService,
            PointsEntryService pointsEntryService) {
        this.orderRepo = orderRepo;
        this.customerService = customerService;
        this.pointsEntryService = pointsEntryService;
    }

    @Override
    public Order placeOrder(List<OrderedProduct> orderedProducts, float moneySpent, int pointsSpent,
            String phoneNumber) {
        Customer customer;
        try {
            customer = customerService.getCustomer(phoneNumber);
        } catch (CustomerException ex) {
            /*
             * If a customer does not already exist with the given phone number,
             * they are added to the table of customers
             */
            customer = new Customer(0, phoneNumber, 0);
            customer = customerService.createCustomer(customer);
        }

        Date pointsExpiryDate = new Date(Calendar.getInstance().getTime().getTime());
        DateUtils.addMonths(pointsExpiryDate, PointsConstants.MONTHS_UNTIL_EXPIRY);
        PointsEntry pointsEntry = new PointsEntry(0, 0, pointsExpiryDate, null, false);

        /*
         * If the customer tries to spend more points then they have,
         * the order is rejected
         */
        if (pointsSpent > customer.getTotalPoints()) {
            throw new OrderException(OrderExceptionMessages.NOT_ENOUGH_POINTS);
        }

        // Redeeming points
        List<PointsEntry> pointsEntriesRedeemedFrom = new ArrayList<>();
        if (pointsSpent > 0) {
            pointsEntriesRedeemedFrom = redeemPoints(pointsSpent, customer);
            customer.setTotalPoints(customer.getTotalPoints() - pointsSpent);
        }

        // Acquiring points
        if (moneySpent > 0) {
            int pointsEarned = calculatePointsEarned(orderedProducts, moneySpent, pointsSpent);
            customer.setTotalPoints(customer.getTotalPoints() + pointsEarned);

            pointsEntry.setNumOfPoints(pointsEarned);
        }

        // Saving the updated customer and points entry
        customer = customerService.updateCustomerPointsTotal(customer.getId(), customer.getTotalPoints());
        pointsEntry.setCustomer(customer);
        pointsEntry = pointsEntryService.createPointsEntry(pointsEntry);

        // Finally creating the order
        Order order = new Order(0, orderedProducts, new Date(Calendar.getInstance().getTime().getTime()), moneySpent,
                pointsSpent, pointsEntriesRedeemedFrom, customer, pointsEntry.getNumOfPoints());
        return orderRepo.save(order);
    }

    @Override
    public Order getOrder(long id) {
        Optional<Order> opOrder = orderRepo.findById(id);
        if (opOrder.isPresent()) {
            return opOrder.get();
        } else {
            throw new OrderException(OrderExceptionMessages.ORDER_NOT_FOUND);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @Override
    public List<Order> getOrdersByCustomer(String phoneNumber) {
        return orderRepo.findByCustomer_PhoneNumber(phoneNumber);
    }

    /*
     * The points earned are relative to how much money was spent on the order,
     * for example if the customer paid 50% as money and 50% as points then they
     * earn 50% of the total points they could have earned.
     * 
     * Points are converted to money before this calculation is done
     */
    @Override
    public int calculatePointsEarned(List<OrderedProduct> orderedProducts, float moneySpent, int pointsSpent) {
        float pointsConvertedToMoney = pointsSpent * PointsConstants.WORTH_OF_ONE_POINT;
        float ratioOfMoneySpent = moneySpent / (moneySpent + pointsConvertedToMoney);

        int pointsEarned = 0;

        for (OrderedProduct orderedProduct : orderedProducts) {
            int productsPoints = orderedProduct.getProduct().getPointsValue() * orderedProduct.getQuantity();

            pointsEarned += productsPoints * ratioOfMoneySpent;
        }

        return pointsEarned;
    }

    @Override
    public List<PointsEntry> redeemPoints(int pointsSpent, Customer customer) {
        List<PointsEntry> pointsEntriesRedeemedFrom = new ArrayList<>();

        List<PointsEntry> pointsEntries = pointsEntryService.getNonExpiredPointsEntriesByCustomer(customer);

        for (PointsEntry pointsEntry : pointsEntries) {
            // The loop keeps going until either all the point spent are accounted for,
            // or every pointEntry is searched through
            if (pointsSpent == 0) {
                break;
            }
            int points = pointsEntry.getNumOfPoints();
            /*
             * If the points entry has negative points, it is added to the pointsSpent.
             * This is meant to set negative point entries back to 0 while increasing the
             * number of points to be redeemed from other entries
             */
            if (points == 0) {
                continue;
            } else if (pointsSpent >= points) {
                pointsSpent -= points;
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(), 0);

                if (points > 0) {
                    pointsEntriesRedeemedFrom.add(pointsEntry);
                }
            } else {
                points -= pointsSpent;
                pointsSpent = 0;
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(), points);

                pointsEntriesRedeemedFrom.add(pointsEntry);
            }
        }

        return pointsEntriesRedeemedFrom;
    }

    @Override
    public void deleteOrder(long id) {
        Optional<Order> opOrder = orderRepo.findById(id);
        if (opOrder.isPresent()) {
            orderRepo.deleteById(id);
        } else {
            throw new OrderException(OrderExceptionMessages.ORDER_NOT_FOUND);
        }
    }
}
