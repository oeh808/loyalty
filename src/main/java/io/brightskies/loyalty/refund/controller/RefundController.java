package io.brightskies.loyalty.refund.controller;

import io.brightskies.loyalty.refund.DTO.ReFundDTO;
import io.brightskies.loyalty.refund.entity.Refund;
import io.brightskies.loyalty.refund.service.RefundService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Refund", description = "Controller for handling mappings for Refunds")
@RequestMapping("/refunds")
public class RefundController {
    private RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    public Refund createRefund(@RequestBody ReFundDTO reFundDTO) {
        return refundService.createRefund(reFundDTO);
    }

}
