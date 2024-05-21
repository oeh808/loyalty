package io.brightskies.loyalty.refund.controller;

import io.brightskies.loyalty.refund.DTO.ReFundDTO;
import io.brightskies.loyalty.refund.entity.Refund;
import io.brightskies.loyalty.refund.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@Tag(name = "Refund", description = "Controller for handling mappings for Refunds")
@RequestMapping("/refunds")
public class RefundController {
    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @Operation(description = "POST endpoint for creating a refund" +
            "\n\n Returns the refund created.", summary = "Create a Refund")
    @PostMapping
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Must conform to required properties of ReFundDTO")
    public Refund createRefund(@Valid @RequestBody ReFundDTO reFundDTO) {
        log.info("Recieved: POST request to /refunds");
        return refundService.createRefund(reFundDTO);
    }

}
