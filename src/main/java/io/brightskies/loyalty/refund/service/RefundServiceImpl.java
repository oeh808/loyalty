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
import io.brightskies.loyalty.refund.DTO.ProductRefundDTO;
import io.brightskies.loyalty.refund.DTO.ReFundDTO;
import io.brightskies.loyalty.refund.entity.Refund;
import io.brightskies.loyalty.refund.entity.RefundedProduct;
import io.brightskies.loyalty.refund.repo.RefundRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.max;

@Service
@AllArgsConstructor
public class RefundServiceImpl implements RefundService {
    private OrderRepo orderRepo;
    private RefundRepo refundRepo;
    private CustomerService customerService;
    private PointsEntryService pointsEntryService;

    @Autowired
    private PointsConstants pointsConstants;

    @Override
    public Refund createRefund(ReFundDTO reFundDTO) {

        // validation

        Customer customer;
        customer = customerService.getCustomer(reFundDTO.getPhoneNumber());

        Order order;
        try {
            order = orderRepo.findById(reFundDTO.getOrderId()).get();
        } catch (CustomerException ex) {
            throw new OrderException("Order not found");
        }

        // chcek if the order is already refunded
        List<OrderedProduct> orderedProducts = order.getOrderedProducts();

        List<Long> productIds = orderedProducts.stream().map(OrderedProduct::getProduct).map(Product::getId).collect(Collectors.toList());
        float totalRefundedMony = 0;
        float AlreadyRefunded = 0;
        List<RefundedProduct> refundedProducts = new ArrayList<>();
        for (ProductRefundDTO refundedProduct : reFundDTO.getProductRefund()) {
            if (!productIds.contains(refundedProduct.getProductId())) {
                throw new OrderException("Product not found in order");
            }
            for (OrderedProduct orderedProduct : orderedProducts) {
                System.out.println("refundedProduct" + refundedProduct);
                System.out.println("orderedProduct" + orderedProduct);

                if (refundedProduct.getProductId() == orderedProduct.getProduct().getId() && refundedProduct.getQuantity() > (orderedProduct.getQuantity() - orderedProduct.getRefundedQuantity())) {
                    throw new OrderException("Order already refunded");
                } else if (refundedProduct.getProductId() == orderedProduct.getProduct().getId()) {

                    orderedProduct.setRefundedQuantity(orderedProduct.getRefundedQuantity() + refundedProduct.getQuantity());
                    AlreadyRefunded += orderedProduct.getRefundedQuantity() * orderedProduct.getProduct().getPrice();
                    totalRefundedMony += (refundedProduct.getQuantity() * orderedProduct.getProduct().getPrice());
                    RefundedProduct refundedProduct1 = new RefundedProduct(orderedProduct.getProduct(), refundedProduct.getQuantity());
                    System.out.println("refundedProduct1" + refundedProduct1);
                    refundedProducts.add(refundedProduct1);
                }
            }

        }
        // calculate refund amount
        float moneyRefunded = 0;
        int pointsRefunded = 0;
        int pointsReduction = 0;

        if (AlreadyRefunded >= order.getPointsSpent() * pointsConstants.WORTH_OF_ONE_POINT) {
            // money refund
            moneyRefunded = totalRefundedMony;

            pointsReduction = (int) ((moneyRefunded / order.getMoneySpent()) * order.getPointsEarned());

        } else if (AlreadyRefunded + totalRefundedMony <= order.getPointsSpent() * pointsConstants.WORTH_OF_ONE_POINT) {
            // points refund
            pointsRefunded = (int) (totalRefundedMony / pointsConstants.WORTH_OF_ONE_POINT);
        } else {
            //money and points refund
            pointsRefunded = (int) ((order.getPointsSpent() * pointsConstants.WORTH_OF_ONE_POINT - AlreadyRefunded) * pointsConstants.WORTH_OF_ONE_POINT);
            moneyRefunded = totalRefundedMony - pointsRefunded * pointsConstants.WORTH_OF_ONE_POINT;
            pointsReduction = (int) ((moneyRefunded / order.getMoneySpent()) * order.getPointsEarned());
        }


        // return points to customer
        boolean pointsReturned = false;
        int shouldRefund = pointsRefunded - pointsReduction;
        int actualPointsRefunded = 0;

        List<PointsEntry> pointsEntries = order.getEntries();
        System.out.println("pointsEntries: " + pointsEntries);
        for (PointsEntry pointsEntry : pointsEntries) {
            if (shouldRefund == 0) {
                break;
            }
            if (pointsEntry.getNumOfPoints() > 0 && -1*(shouldRefund) > pointsEntry.getNumOfPoints()) {
                actualPointsRefunded+=pointsEntry.getNumOfPoints();
                shouldRefund-=pointsEntry.getNumOfPoints();
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(), 0);

            }else{
                actualPointsRefunded+=shouldRefund;
                pointsEntryService.updatePointsInEntry(pointsEntry.getId(), pointsEntry.getNumOfPoints()+shouldRefund);
                shouldRefund=0;

            }

        }
        List<PointsEntry> customerPointsEntries = pointsEntryService.getNonExpiredPointsEntriesByCustomer(customer);
        PointsEntry pointsEntry;

        if (shouldRefund!=0 && customerPointsEntries.isEmpty()) {
            Date pointsExpiryDate = new Date(Calendar.getInstance().getTime().getTime());
            DateUtils.addMonths(pointsExpiryDate, pointsConstants.MONTHS_UNTIL_EXPIRY);
            actualPointsRefunded = max(shouldRefund, 0);
            pointsEntry = new PointsEntry(0, actualPointsRefunded, pointsExpiryDate, customer, false);
            pointsEntry.setCustomer(customer);
            pointsEntryService.createPointsEntry(pointsEntry);
        } else if (shouldRefund!=0 && (shouldRefund) >= 0) {

            System.out.println(" refund first  pocket: ");
            actualPointsRefunded = shouldRefund;
            pointsEntryService.updatePointsInEntry(customerPointsEntries.get(0).getId(), customerPointsEntries.get(0).getNumOfPoints() + actualPointsRefunded);
        } else if (shouldRefund!=0){
            for (PointsEntry customerPointsEntry : customerPointsEntries) {
                if(shouldRefund==0){
                    break;
                }
                if (customerPointsEntry.getNumOfPoints() > 0 && -1*(shouldRefund) > customerPointsEntry.getNumOfPoints()) {
                    actualPointsRefunded+=customerPointsEntry.getNumOfPoints();
                    shouldRefund-=customerPointsEntry.getNumOfPoints();
                    pointsEntryService.updatePointsInEntry(customerPointsEntry.getId(), 0);

                }else{
                        actualPointsRefunded+=shouldRefund;

                    System.out.println(" refund  pocket: ");
                     pointsEntryService.updatePointsInEntry(customerPointsEntry.getId(),customerPointsEntry.getNumOfPoints()+shouldRefund);

                    shouldRefund=0;
                }
            }
        }


        Date RefundDate = new Date(Calendar.getInstance().getTime().getTime());

        // create refund
        Refund refund = new Refund(0, customer, refundedProducts, order, moneyRefunded, actualPointsRefunded, RefundDate);

        System.out.println("refund: " + refund);

        // update database
        refund = refundRepo.save(refund);

        // update customer points
        customerService.updateCustomerPointsTotal(customer.getId(), customer.getTotalPoints() + actualPointsRefunded);

        // update order
        order.setOrderedProducts(orderedProducts);
        orderRepo.save(order);

        return refund;
    }
}
