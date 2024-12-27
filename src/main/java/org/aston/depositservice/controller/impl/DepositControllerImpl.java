package org.aston.depositservice.controller.impl;

import lombok.AllArgsConstructor;
import org.aston.depositservice.controller.DepositController;
import org.aston.depositservice.dto.response.ActiveDepositResponseDto;
import org.aston.depositservice.service.impl.DepositServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.aston.depositservice.configuration.ApplicationConstant.DEPOSIT_ID;
import static org.aston.depositservice.configuration.ApplicationConstant.HEADER_KEY_CUSTOMER_ID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/deposit")
public class DepositControllerImpl implements DepositController {

    private final DepositServiceImpl depositService;

    @Override
    @GetMapping("/info")
    public ResponseEntity<ActiveDepositResponseDto> getActiveCustomerDeposit(
            @RequestHeader(HEADER_KEY_CUSTOMER_ID) String customerId,
            @RequestParam(DEPOSIT_ID) String depositId
    ) {
        return ResponseEntity.ok(depositService.getActiveCustomerDeposit(customerId, depositId));
    }
}