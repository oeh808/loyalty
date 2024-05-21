package io.brightskies.loyalty.refund.service;

import io.brightskies.loyalty.refund.DTO.ReFundDTO;
import io.brightskies.loyalty.refund.entity.Refund;
import io.brightskies.loyalty.refund.entity.RefundedProduct;

import java.util.List;

public interface RefundService {
    Refund createRefund(ReFundDTO reFundDTO);
    List<RefundedProduct> getRefundedProducts(long refundId);
    Refund getRefund(long refundId);
}
