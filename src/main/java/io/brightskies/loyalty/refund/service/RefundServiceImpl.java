package io.brightskies.loyalty.refund.service;

import io.brightskies.loyalty.constants.PointsConstants;
import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.service.CustomerService;
import io.brightskies.loyalty.order.OrderedProduct;
import io.brightskies.loyalty.order.entity.Order;
import io.brightskies.loyalty.order.exception.OrderException;
import io.brightskies.loyalty.order.exception.OrderExceptionMessages;
import io.brightskies.loyalty.order.repo.OrderRepo;
import io.brightskies.loyalty.order.service.OrderService;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import io.brightskies.loyalty.pointsEntry.service.PointsEntryService;
import io.brightskies.loyalty.product.entity.Product;
import io.brightskies.loyalty.refund.DTO.ProductRefundDTO;
import io.brightskies.loyalty.refund.DTO.ReFundDTO;
import io.brightskies.loyalty.refund.entity.Refund;
import io.brightskies.loyalty.refund.entity.RefundedProduct;
import io.brightskies.loyalty.refund.repo.RefundRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Math.max;

@Service
@AllArgsConstructor
public class RefundServiceImpl implements RefundService {
    private final OrderService orderService;
    private OrderRepo orderRepo;
    private RefundRepo refundRepo;
    private CustomerService customerService;
    private PointsEntryService pointsEntryService;
    private PointsConstants pointsConstants;

    @Override
    public Refund createRefund(ReFundDTO reFundDTO) {

        // validation
        Customer customer = customerService.getCustomer(reFundDTO.getPhoneNumber());
        Order order = orderService.getOrder(reFundDTO.getOrderId());

        // chcek if the order is already refunded

        List<OrderedProduct> orderedProducts = order.getOrderedProducts();
        List<Long> productIds = orderedProducts.stream().map(OrderedProduct::getProduct).map(Product::getId).toList();
        float totalRefundedMony = 0;
        float AlreadyRefunded = 0;
        List<RefundedProduct> refundedProducts = new ArrayList<>();
        for (ProductRefundDTO refundedProduct : reFundDTO.getProductRefund()) {
            if (!productIds.contains(refundedProduct.getProductId())) {
                throw new OrderException(OrderExceptionMessages.PRODUCT_NOT_FOUND_IN_ORDER);
            }
            for (OrderedProduct orderedProduct : orderedProducts) {
                if (refundedProduct.getProductId() == orderedProduct.getProduct().getId() && refundedProduct.getQuantity() > (orderedProduct.getQuantity() - orderedProduct.getRefundedQuantity())) {
                    throw new OrderException(OrderExceptionMessages.ORDER_ALREADY_REFUNDED);
                } else if (refundedProduct.getProductId() == orderedProduct.getProduct().getId()) {
                    AlreadyRefunded += orderedProduct.getRefundedQuantity() * orderedProduct.getProduct().getPrice();
                    totalRefundedMony += (refundedProduct.getQuantity() * orderedProduct.getProduct().getPrice());
                    orderedProduct.setRefundedQuantity(orderedProduct.getRefundedQuantity() + refundedProduct.getQuantity());
                    RefundedProduct refundedProduct1 = new RefundedProduct(orderedProduct.getProduct(), refundedProduct.getQuantity());
                    refundedProducts.add(refundedProduct1);
                }
            }
        }
        System.out.println("AlreadyRefunded = " + AlreadyRefunded);
        // calculate refund amount
        float moneyRefunded = 0;
        int pointsRefunded = 0;
        int pointsReduction = 0;
        if (AlreadyRefunded >= order.getPointsSpent() * pointsConstants.WORTH_OF_ONE_POINT) {
            System.out.println("totalRefundedMony = " + totalRefundedMony);
            // money refund
            moneyRefunded = totalRefundedMony;
            pointsReduction = (int) ((moneyRefunded / order.getMoneySpent()) * order.getPointsEarned());
        } else if (AlreadyRefunded + totalRefundedMony <= order.getPointsSpent() * pointsConstants.WORTH_OF_ONE_POINT) {
            // points refund
            pointsRefunded = (int) (totalRefundedMony / pointsConstants.WORTH_OF_ONE_POINT);
            System.out.println("pointsRefunded = " + pointsRefunded);

        } else {
            System.out.println("totalRefundedMony pointsReduction ");
            //money and points refund
            pointsRefunded = (int) ((order.getPointsSpent() * pointsConstants.WORTH_OF_ONE_POINT - AlreadyRefunded) / pointsConstants.WORTH_OF_ONE_POINT);
            moneyRefunded = totalRefundedMony - pointsRefunded * pointsConstants.WORTH_OF_ONE_POINT;
            pointsReduction = (int) ((moneyRefunded / order.getMoneySpent()) * order.getPointsEarned());
        }

        // return points to customer in the oldest pocket that used in this order
        int shouldRefund = pointsRefunded - pointsReduction;
        int actualPointsRefunded = 0;
        List<PointsEntry> pointsEntries = order.getEntries();
        for (PointsEntry pointsEntry : pointsEntries) {
            if (shouldRefund == 0) {
                break;
            }
            if (shouldRefund > 0) {
                actualPointsRefunded += shouldRefund;
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(), pointsEntry.getNumOfPoints() + shouldRefund);
                shouldRefund = 0;
                break;
            }
            if (pointsEntry.getNumOfPoints() == 0) {
                continue;
            }
            if (-1 * (shouldRefund) > pointsEntry.getNumOfPoints()) {
                actualPointsRefunded += pointsEntry.getNumOfPoints();
                shouldRefund -= pointsEntry.getNumOfPoints();
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(), 0);

            } else {
                actualPointsRefunded += shouldRefund;
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(), pointsEntry.getNumOfPoints() + shouldRefund);
                shouldRefund = 0;
            }
        }

        // if user has no pocket that used in order create new pocket for him
        List<PointsEntry> customerPointsEntries = pointsEntryService.getNonExpiredPointsEntriesByCustomer(customer);
        PointsEntry pointsEntry;
        if (shouldRefund != 0 && customerPointsEntries.isEmpty()) {
            Date pointsExpiryDate = new Date(Calendar.getInstance().getTime().getTime());
            DateUtils.addMonths(pointsExpiryDate, pointsConstants.MONTHS_UNTIL_EXPIRY);
            actualPointsRefunded = max(shouldRefund, 0);
            pointsEntry = new PointsEntry(0, actualPointsRefunded, pointsExpiryDate, customer, false);
            pointsEntry.setCustomer(customer);
            pointsEntryService.createPointsEntry(pointsEntry);
        } else if (shouldRefund != 0 && (shouldRefund) >= 0) {
            // if user has pocket that u
            actualPointsRefunded = shouldRefund;
            pointsEntryService.updatePointsInEntry(customerPointsEntries.get(0).getId(), customerPointsEntries.get(0).getNumOfPoints() + actualPointsRefunded);
        } else if (shouldRefund != 0) {
            for (PointsEntry customerPointsEntry : customerPointsEntries) {
                if (shouldRefund == 0) {
                    break;
                }
                if (customerPointsEntry.getNumOfPoints() == 0) {
                    continue;
                }
                if (-1 * (shouldRefund) > customerPointsEntry.getNumOfPoints()) {
                    actualPointsRefunded += customerPointsEntry.getNumOfPoints();
                    shouldRefund -= customerPointsEntry.getNumOfPoints();
                    pointsEntryService.updatePointsInEntry(customerPointsEntry.getId(), 0);
                } else {
                    actualPointsRefunded += shouldRefund;
                    pointsEntryService.updatePointsInEntry(customerPointsEntry.getId(), customerPointsEntry.getNumOfPoints() + shouldRefund);
                    shouldRefund = 0;
                }
            }
        }
        Date RefundDate = new Date(Calendar.getInstance().getTime().getTime());
        // create refund
        Refund refund = new Refund(0, customer, refundedProducts, order, moneyRefunded, actualPointsRefunded, RefundDate);

        // update database
        refund = refundRepo.save(refund);

        // update customer points
        customerService.updateCustomerPointsTotal(customer.getId(), customer.getTotalPoints() + actualPointsRefunded);

        // update order
        order.setOrderedProducts(orderedProducts);
        orderRepo.save(order);

        return refund;
    }

    @Override
    public List<RefundedProduct> getRefundedProducts(long refundId) {
        Refund refund = refundRepo.findById(refundId).orElseThrow(() -> new OrderException("Refund not found"));
        return refund.getProductsRefunded();
    }

    @Override
    public Refund getRefund(long refundId) {
        return refundRepo.findById(refundId).orElseThrow(() -> new OrderException("Refund not found"));
    }
}
