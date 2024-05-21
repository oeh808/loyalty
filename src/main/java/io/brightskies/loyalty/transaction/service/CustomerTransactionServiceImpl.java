package io.brightskies.loyalty.transaction.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.customer.service.CustomerService;
import io.brightskies.loyalty.order.OrderedProduct;
import io.brightskies.loyalty.order.entity.Order;
import io.brightskies.loyalty.order.service.OrderService;
import io.brightskies.loyalty.refund.entity.RefundedProduct;
import io.brightskies.loyalty.refund.service.RefundService;
import io.brightskies.loyalty.transaction.dto.CustomerTransactionReadingDto;
import io.brightskies.loyalty.transaction.entity.CustomerTransaction;
import io.brightskies.loyalty.transaction.mapper.CustomerTransactionMapper;
import io.brightskies.loyalty.transaction.other.TransactionProduct;
import io.brightskies.loyalty.transaction.repo.CustomerTransactionRepo;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CustomerTransactionServiceImpl implements CustomerTransactionService {
    private CustomerTransactionRepo customerTransactionRepo;
    private CustomerService customerService;
    private CustomerTransactionMapper customerTransactionMapper;
    private OrderService orderService;
    private RefundService refundService;

    public CustomerTransactionServiceImpl(CustomerTransactionRepo customerTransactionRepo,
            CustomerService customerService, CustomerTransactionMapper customerTransactionMapper,
            OrderService orderService, RefundService refundService) {
        this.customerTransactionRepo = customerTransactionRepo;
        this.customerService = customerService;
        this.customerTransactionMapper = customerTransactionMapper;
        this.orderService = orderService;
        this.refundService = refundService;
    }

    @Override
    public List<CustomerTransactionReadingDto> getCustomerTransactionsByCustomer(String phoneNumber) {
        log.info("Running getCustomerTransactionsByCustomer(" + phoneNumber + ") in CustomerTransactionServiceImpl...");
        Customer customer = customerService.getCustomer(phoneNumber);

        log.info("Searching for transaction...");
        List<CustomerTransaction> transactions = customerTransactionRepo.findByCustomer(customer);

        log.info("Retrieving products associated with transaction...");
        List<List<TransactionProduct>> transactionProductsList = getProducts(transactions);

        List<CustomerTransactionReadingDto> dtos = customerTransactionMapper.toDto(transactions,
                transactionProductsList);
        return dtos;
    }

    // --- Helper Functions ---
    private List<List<TransactionProduct>> getProducts(List<CustomerTransaction> customerTransactions) {
        log.info("Running getProducts(" + customerTransactions.toString() + ") in CustomerTransactionServiceImpl...");
        List<List<TransactionProduct>> transactionProductsList = new ArrayList<>();

        for (CustomerTransaction customerTransaction : customerTransactions) {
            List<TransactionProduct> transactionProducts = new ArrayList<>();

            if (customerTransaction.getTransactionType().equals("order")) {
                Order order = orderService.getOrder(customerTransaction.getTransactionId());
                List<OrderedProduct> orderedProducts = order.getOrderedProducts();

                for (OrderedProduct orderedProduct : orderedProducts) {
                    TransactionProduct transactionProduct = new TransactionProduct(orderedProduct.getProduct(),
                            orderedProduct.getQuantity());

                    transactionProducts.add(transactionProduct);
                }
            } else { // Must be a refund
                List<RefundedProduct> refundedProducts = refundService
                        .getRefundedProducts(customerTransaction.getTransactionId());

                for (RefundedProduct refundedProduct : refundedProducts) {
                    TransactionProduct transactionProduct = new TransactionProduct(refundedProduct.getProduct(),
                            refundedProduct.getQuantity());

                    transactionProducts.add(transactionProduct);
                }
            }

            transactionProductsList.add(transactionProducts);
        }
        return transactionProductsList;
    }
}
