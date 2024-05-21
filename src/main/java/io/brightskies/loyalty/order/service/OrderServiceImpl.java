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
import io.brightskies.loyalty.order.dtos.OrderedProductDto;
import io.brightskies.loyalty.order.entity.Order;
import io.brightskies.loyalty.order.exception.OrderException;
import io.brightskies.loyalty.order.exception.OrderExceptionMessages;
import io.brightskies.loyalty.order.repo.OrderRepo;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.service.PointsEntryService;
import io.brightskies.loyalty.product.entity.Product;
import io.brightskies.loyalty.product.service.ProductService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {
    private OrderRepo orderRepo;
    private CustomerService customerService;
    private PointsEntryService pointsEntryService;
    private ProductService productService;
    private PointsConstants pointsConstants;

    public OrderServiceImpl(OrderRepo orderRepo, CustomerService customerService,
            PointsEntryService pointsEntryService, ProductService productService, PointsConstants pointsConstants) {
        this.orderRepo = orderRepo;
        this.customerService = customerService;
        this.pointsEntryService = pointsEntryService;
        this.productService = productService;
        this.pointsConstants = pointsConstants;
    }

    @Override
    public Order placeOrder(List<OrderedProductDto> orderedProductsDto, float moneySpent, int pointsSpent,
            String phoneNumber) {
        log.info("Running placeOrder(" + orderedProductsDto.toString() + ", " + moneySpent
                + ", " + pointsSpent + ", " + phoneNumber + ") in OrderServiceImpl...");

        List<OrderedProduct> orderedProducts;
        log.info("Retrieving ordered products...");
        orderedProducts = retrieveOrderedProducts(orderedProductsDto);

        log.info("Checking if customer phone number is already recorded...");
        Customer customer;
        try {
            customer = customerService.getCustomer(phoneNumber);
        } catch (CustomerException ex) {
            /*
             * If a customer does not already exist with the given phone number,
             * they are added to the table of customers
             */
            log.info("Customer not already registered, creating new customer...");
            customer = new Customer(0, phoneNumber, 0);
            customer = customerService.createCustomer(customer);
        }

        Date pointsExpiryDate = new Date(Calendar.getInstance().getTime().getTime());

        pointsExpiryDate = new Date(
                DateUtils.addMonths(pointsExpiryDate, pointsConstants.MONTHS_UNTIL_EXPIRY).getTime());
        PointsEntry pointsEntry = new PointsEntry(0, 0, pointsExpiryDate, null, false);

        /*
         * If the customer tries to spend more points then they have,
         * the order is rejected
         */
        if (pointsSpent > customer.getTotalPoints()) {
            log.error("Customer trying to spend: " + pointsSpent + " but only has: " + customer.getTotalPoints()
                    + " points!");
            throw new OrderException(OrderExceptionMessages.NOT_ENOUGH_POINTS);
        }

        // Redeeming points
        List<PointsEntry> pointsEntriesRedeemedFrom = new ArrayList<>();
        if (pointsSpent > 0) {
            log.info("Redeeming customer points...");
            pointsEntriesRedeemedFrom = redeemPoints(pointsSpent, customer);
            customer.setTotalPoints(customer.getTotalPoints() - pointsSpent);
        }

        // Acquiring points
        if (moneySpent > 0) {
            log.info("Acquiring customer points...");
            int pointsEarned = calculatePointsEarned(orderedProducts, moneySpent, pointsSpent);
            customer.setTotalPoints(customer.getTotalPoints() + pointsEarned);

            pointsEntry.setNumOfPoints(pointsEarned);
        }

        // Saving the updated customer and points entry
        log.info("Updating customer total points...");
        customer = customerService.updateCustomerPointsTotal(customer.getId(), customer.getTotalPoints());
        log.info("Creating new points entry for order...");
        pointsEntry.setCustomer(customer);
        pointsEntry = pointsEntryService.createPointsEntry(pointsEntry);

        // Finally creating the order
        log.info("Saving order...");
        Order order = new Order(0, orderedProducts, new Date(Calendar.getInstance().getTime().getTime()), moneySpent,
                pointsSpent, pointsEntriesRedeemedFrom, customer, pointsEntry.getNumOfPoints());
        return orderRepo.save(order);
    }

    @Override
    public Order getOrder(long id) {
        log.info("Running getOrder(" + id + ") in OrderServiceImpl...");
        Optional<Order> opOrder = orderRepo.findById(id);
        if (opOrder.isPresent()) {
            return opOrder.get();
        } else {
            log.error("Invalid order id: " + id + "!");
            throw new OrderException(OrderExceptionMessages.ORDER_NOT_FOUND);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        log.info("Running getAllOrders() in OrderServiceImpl...");
        return orderRepo.findAll();
    }

    @Override
    public List<Order> getOrdersByCustomer(String phoneNumber) {
        log.info("Running getOrdersByCustomer(" + phoneNumber + ") in OrderServiceImpl...");
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
        log.info("Running calculatePointsEarned(" + orderedProducts.toString() + ", " + moneySpent
                + ", " + pointsSpent + ") in OrderServiceImpl...");

        float pointsConvertedToMoney = pointsSpent * pointsConstants.WORTH_OF_ONE_POINT;
        float ratioOfMoneySpent = moneySpent / (moneySpent + pointsConvertedToMoney);

        int pointsEarned = 0;

        for (OrderedProduct orderedProduct : orderedProducts) {
            log.info("Calculating points to be added for the product: " + orderedProduct.getProduct().toString()
                    + " with quantity: " + orderedProduct.getQuantity());
            int productsPoints = orderedProduct.getProduct().getPointsValue() * orderedProduct.getQuantity();
            log.info("Adding product points: " + productsPoints + " to total points earned in order.");

            pointsEarned += productsPoints * ratioOfMoneySpent;
        }

        return pointsEarned;
    }

    @Override
    public List<PointsEntry> redeemPoints(int pointsSpent, Customer customer) {
        log.info("Running redeemPoints(" + pointsSpent + ", " + customer.toString() + ") in OrderServiceImpl...");
        List<PointsEntry> pointsEntriesRedeemedFrom = new ArrayList<>();

        log.info("Retrieving unexpired points entries...");
        List<PointsEntry> pointsEntries = pointsEntryService.getNonExpiredPointsEntriesByCustomer(customer);

        log.info("Redeeming points...");
        for (PointsEntry pointsEntry : pointsEntries) {
            log.info("Checking points entry: " + pointsEntry.toString() + "...");
            log.info("Points left to redeem: " + pointsSpent);
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
                log.info("Ignoring entry due to havning zero points...");
                continue;
            } else if (pointsSpent >= points) {
                log.info("Setting points in entry to: 0...");
                pointsSpent -= points;
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(), 0);

                if (points > 0) {
                    pointsEntriesRedeemedFrom.add(pointsEntry);
                }
            } else {
                log.info("Setting points in entry to: " + points + "...");
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
        log.info("Running deleteOrder(" + id + ") in OrderServiceImpl...");
        log.info("Checking order id exists...");
        Optional<Order> opOrder = orderRepo.findById(id);
        if (opOrder.isPresent()) {
            log.info("Deleting order...");
            orderRepo.deleteById(id);
        } else {
            log.error("Invalid order id: " + id + "!");
            throw new OrderException(OrderExceptionMessages.ORDER_NOT_FOUND);
        }
    }

    @Override
    public List<OrderedProduct> retrieveOrderedProducts(List<OrderedProductDto> orderedProductsDto) {
        log.info("Running retrieveOrderedProducts(" + orderedProductsDto.toString() + ") in OrderServiceImpl...");
        List<OrderedProduct> orderedProducts = new ArrayList<>();
        for (OrderedProductDto orderedProductDto : orderedProductsDto) {
            log.info("Retrieving product with id: " + orderedProductDto.productId());
            Product product = productService.getProduct(orderedProductDto.productId());
            OrderedProduct orderedProduct = new OrderedProduct(product, orderedProductDto.quantity(), 0);

            orderedProducts.add(orderedProduct);
        }

        return orderedProducts;
    }
}
