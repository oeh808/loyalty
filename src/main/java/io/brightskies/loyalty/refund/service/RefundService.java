package io.brightskies.loyalty.refund.service;

import io.brightskies.loyalty.refund.DTO.ReFundDTO;
import io.brightskies.loyalty.refund.entity.Refund;

public interface RefundService {
    Refund createRefund(ReFundDTO reFundDTO);
}
