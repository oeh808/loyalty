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
import io.brightskies.loyalty.refund.exception.RefundException;
import io.brightskies.loyalty.refund.exception.RefundExceptionMessages;
import io.brightskies.loyalty.refund.repo.RefundRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@AllArgsConstructor
public class RefundServiceImpl implements RefundService {
    private final OrderService orderService;
    private final OrderRepo orderRepo;
    private final RefundRepo refundRepo;
    private final CustomerService customerService;
    private final PointsEntryService pointsEntryService;
    private final PointsConstants pointsConstants;

    @Override
    public Refund createRefund(ReFundDTO reFundDTO) {
        Customer customer = customerService.getCustomer(reFundDTO.getPhoneNumber());
        Order order = orderService.getOrder(reFundDTO.getOrderId());

        List<RefundedProduct> refundedProducts = validateOrder(order, reFundDTO.getProductRefund());

        Refund refund = calculateAndCreateRefund(order, customer, refundedProducts);

        updateOrderAndCustomer(order, customer, refund);

        return refund;
    }

    private List<RefundedProduct> validateOrder(Order order, List<ProductRefundDTO> productRefunds) {
        List<RefundedProduct> refundedProducts = new ArrayList<>();
        List<OrderedProduct> orderedProducts = order.getOrderedProducts();
        List<Long> productIds = orderedProducts.stream()
                .map(OrderedProduct::getProduct)
                .map(Product::getId)
                .toList();
        for (ProductRefundDTO productRefund : productRefunds) {
            if (!productIds.contains(productRefund.getProductId())) {
                throw new OrderException(OrderExceptionMessages.PRODUCT_NOT_FOUND_IN_ORDER);
            }
            OrderedProduct orderedProduct = orderedProducts.stream()
                    .filter(op -> op.getProduct().getId() == productRefund.getProductId())
                    .findFirst()
                    .orElseThrow(() -> new OrderException(OrderExceptionMessages.PRODUCT_NOT_FOUND_IN_ORDER));
            orderedProduct.setRefundedQuantity(orderedProduct.getRefundedQuantity() + productRefund.getQuantity());
            refundedProducts.add(new RefundedProduct(orderedProduct.getProduct(), productRefund.getQuantity()));
            if (productRefund.getQuantity() > (orderedProduct.getQuantity() - orderedProduct.getRefundedQuantity())) {
                throw new OrderException(OrderExceptionMessages.ORDER_ALREADY_REFUNDED);
            }
        }
        return refundedProducts;
    }

    private Refund calculateAndCreateRefund(Order order, Customer customer, List<RefundedProduct> refundedProducts) {
        float totalRefundedMoney = calculateTotalRefundedMoney(refundedProducts);
        float alreadyRefunded = calculateAlreadyRefunded(order);
        float moneyRefunded = 0;
        int pointsRefunded = 0;
        int pointsReduction = 0;

        if (alreadyRefunded >= order.getPointsSpent() * pointsConstants.WORTH_OF_ONE_POINT) {
            moneyRefunded = totalRefundedMoney;
            pointsReduction = (int) ((moneyRefunded / order.getMoneySpent()) * order.getPointsEarned());
        } else if (alreadyRefunded + totalRefundedMoney <= order.getPointsSpent()
                * pointsConstants.WORTH_OF_ONE_POINT) {
            pointsRefunded = (int) (totalRefundedMoney / pointsConstants.WORTH_OF_ONE_POINT);
        } else {
            pointsRefunded = (int) ((order.getPointsSpent() * pointsConstants.WORTH_OF_ONE_POINT - alreadyRefunded)
                    / pointsConstants.WORTH_OF_ONE_POINT);
            moneyRefunded = totalRefundedMoney - pointsRefunded * pointsConstants.WORTH_OF_ONE_POINT;
            pointsReduction = (int) ((moneyRefunded / order.getMoneySpent()) * order.getPointsEarned());
        }
        int actualPointsRefunded = updateCustomerPoints(order, customer, pointsRefunded - pointsReduction);
        Date refundDate = new Date(Calendar.getInstance().getTime().getTime());
        return new Refund(0, customer, refundedProducts, order, moneyRefunded, actualPointsRefunded, refundDate);
    }

    private float calculateTotalRefundedMoney(List<RefundedProduct> refundedProducts) {
        return refundedProducts.stream()
                .map(rp -> rp.getQuantity() * rp.getProduct().getPrice())
                .reduce(0f, Float::sum);
    }

    private float calculateAlreadyRefunded(Order order) {
        return order.getOrderedProducts().stream()
                .map(op -> op.getRefundedQuantity() * op.getProduct().getPrice())
                .reduce(0f, Float::sum);
    }

    private int updateCustomerPoints(Order order, Customer customer, int pointsToRefund) {
        int actualPointsRefunded = 0;
        List<PointsEntry> pointsEntries = order.getEntries();

        for (PointsEntry pointsEntry : pointsEntries) {
            if (pointsToRefund == 0) {
                break;
            }
            if (pointsToRefund > 0) {
                actualPointsRefunded += pointsToRefund;
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(),
                        pointsEntry.getNumOfPoints() + pointsToRefund);
                pointsToRefund = 0;
                break;
            }
            if (pointsEntry.getNumOfPoints() == 0) {
                continue;
            }
            if (-pointsToRefund > pointsEntry.getNumOfPoints()) {
                actualPointsRefunded += pointsEntry.getNumOfPoints();
                pointsToRefund -= pointsEntry.getNumOfPoints();
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(), 0);
            } else {
                actualPointsRefunded += pointsToRefund;
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(),
                        pointsEntry.getNumOfPoints() + pointsToRefund);
                pointsToRefund = 0;
            }
        }

        if (pointsToRefund != 0) {
            actualPointsRefunded += refundRemainingPointsToCustomer(customer, pointsToRefund);
        }

        return actualPointsRefunded;
    }

    private int refundRemainingPointsToCustomer(Customer customer, int pointsToRefund) {
        List<PointsEntry> customerPointsEntries = pointsEntryService.getNonExpiredPointsEntriesByCustomer(customer);
        int actualPointsRefunded = 0;

        if (pointsToRefund > 0 && customerPointsEntries.isEmpty()) {
            Date pointsExpiryDate = new Date(Calendar.getInstance().getTime().getTime());
            DateUtils.addMonths(pointsExpiryDate, pointsConstants.MONTHS_UNTIL_EXPIRY);
            actualPointsRefunded = pointsToRefund;
            PointsEntry newPointsEntry = new PointsEntry(0, actualPointsRefunded, pointsExpiryDate, customer, false);
            pointsEntryService.createPointsEntry(newPointsEntry);
        } else if (pointsToRefund > 0) {
            PointsEntry firstEntry = customerPointsEntries.get(0);
            actualPointsRefunded = pointsToRefund;
            pointsEntryService.updatePointsInEntry(firstEntry.getId(),
                    firstEntry.getNumOfPoints() + actualPointsRefunded);
        } else {
            for (PointsEntry customerPointsEntry : customerPointsEntries) {
                if (pointsToRefund == 0) {
                    break;
                }
                if (customerPointsEntry.getNumOfPoints() == 0) {
                    continue;
                }
                if (-pointsToRefund > customerPointsEntry.getNumOfPoints()) {
                    actualPointsRefunded += customerPointsEntry.getNumOfPoints();
                    pointsToRefund -= customerPointsEntry.getNumOfPoints();
                    pointsEntryService.updatePointsInEntry(customerPointsEntry.getId(), 0);
                } else {
                    actualPointsRefunded += pointsToRefund;
                    pointsEntryService.updatePointsInEntry(customerPointsEntry.getId(),
                            customerPointsEntry.getNumOfPoints() + pointsToRefund);
                    pointsToRefund = 0;
                }
            }
        }

        return actualPointsRefunded;
    }

    private void updateOrderAndCustomer(Order order, Customer customer, Refund refund) {
        order.setOrderedProducts(order.getOrderedProducts());
        orderRepo.save(order);
        refundRepo.save(refund);
        customerService.updateCustomerPointsTotal(customer.getId(),
                customer.getTotalPoints() + refund.getPointsRefunded());
    }

    @Override
    public List<RefundedProduct> getRefundedProducts(long refundId) {
        Refund refund = refundRepo.findById(refundId)
                .orElseThrow(() -> new RefundException(RefundExceptionMessages.REFUND_NOT_FOUND));
        return refund.getProductsRefunded();
    }

    @Override
    public Refund getRefund(long refundId) {
        return refundRepo.findById(refundId)
                .orElseThrow(() -> new RefundException(RefundExceptionMessages.REFUND_NOT_FOUND));
    }
}
