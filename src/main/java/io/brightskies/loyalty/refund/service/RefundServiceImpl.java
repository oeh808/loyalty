package io.brightskies.loyalty.refund.service;

import io.brightskies.loyalty.constants.PointsConstants;
import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.exception.CustomerException;
import io.brightskies.loyalty.customer.service.CustomerService;
import io.brightskies.loyalty.order.OrderedProduct;
import io.brightskies.loyalty.order.entity.Order;
import io.brightskies.loyalty.order.exception.OrderException;
import io.brightskies.loyalty.order.repo.OrderRepo;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.service.PointsEntryService;
import io.brightskies.loyalty.product.entity.Product;
import io.brightskies.loyalty.refund.DTO.ReFundDTO;
import io.brightskies.loyalty.refund.entity.Refund;
import io.brightskies.loyalty.refund.repo.RefundRepo;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RefundServiceImpl implements RefundService {
    private OrderRepo orderRepo;
    private RefundRepo refundRepo;
    private CustomerService customerService;
    private PointsEntryService pointsEntryService;

    @Override
    public Refund createRefund(ReFundDTO reFundDTO) {
        // validation

        Customer customer;
        try {
            customer = customerService.getCustomer(reFundDTO.getPhoneNumber());
        } catch (CustomerException ex) {
            throw new CustomerException("Customer not found");
        }
        Order order;
        try {
            order = orderRepo.findById(reFundDTO.getOrderId()).get();
        } catch (CustomerException ex) {
            throw new OrderException("Order not found");
        }

        // chcek if the order is already refunded
        List<OrderedProduct> orderedProducts = order.getOrderedProducts();

        Set<Long> productIds = new HashSet<>();
        List<Long> orderIDs = orderedProducts.stream().map(OrderedProduct::getProduct).map(Product::getId)
                .collect(Collectors.toList());
        float totalRefundedMony = 0;
        int totalRefundedPoints = 0;
        for (OrderedProduct refundedProduct : reFundDTO.getOrderedProducts()) {
            if (!orderIDs.contains(refundedProduct.getProduct().getId())) {
                throw new OrderException("Product not found in order");
            }
            for (OrderedProduct orderedProduct : orderedProducts) {

                if (refundedProduct.getProduct().getId() == orderedProduct.getProduct().getId() && refundedProduct
                        .getQuantity() > (orderedProduct.getQuantity() - orderedProduct.getRefundedQuantity())) {
                    throw new OrderException("Order already refunded");
                } else {
                    orderedProduct
                            .setRefundedQuantity(orderedProduct.getRefundedQuantity() + refundedProduct.getQuantity());
                }
            }
            totalRefundedMony += (refundedProduct.getQuantity() * refundedProduct.getProduct().getPrice());
            totalRefundedPoints += (refundedProduct.getQuantity() * refundedProduct.getProduct().getPointsValue());
        }

        // calculate refund amount
        float moneyRefunded = 0;
        int pointsRefunded = 0;
        if (order.getMoneySpent() > 0 && order.getPointsSpent() == 0) {
            moneyRefunded = totalRefundedMony;
        } else if (order.getMoneySpent() == 0 && order.getPointsSpent() > 0) {
            pointsRefunded = (int) totalRefundedPoints;
        } else {
            int moneySpentPrecent = (int) (totalRefundedMony / (totalRefundedMony + totalRefundedPoints)
                    * PointsConstants.WORTH_OF_ONE_POINT);
            int pointsSpentPrecent = 100 - moneySpentPrecent;

            moneyRefunded = totalRefundedMony * moneySpentPrecent / 100;
            pointsRefunded = (int) totalRefundedPoints * pointsSpentPrecent / 100;
        }

        Date RefundDate = new Date(Calendar.getInstance().getTime().getTime());
        Date pointsExpiryDate = new Date(Calendar.getInstance().getTime().getTime());
        DateUtils.addMonths(pointsExpiryDate, PointsConstants.MONTHS_UNTIL_EXPIRY);
        PointsEntry pointsEntry = new PointsEntry(0, 0, pointsExpiryDate, null);

        // create refund
        Refund refund = new Refund(customer, order, reFundDTO.getOrderedProducts(), moneyRefunded, pointsRefunded,
                RefundDate);

        // update database
        refund = refundRepo.save(refund);

        // update point
        customer = customerService.updateCustomerPointsTotal(customer.getId(),
                customer.getTotalPoints() + pointsRefunded);
        pointsEntry.setCustomer(customer);
        pointsEntry = pointsEntryService.createPointsEntry(pointsEntry);
        // update order

        order.setOrderedProducts(orderedProducts);
        orderRepo.save(order);

        return refund;
    }
}
