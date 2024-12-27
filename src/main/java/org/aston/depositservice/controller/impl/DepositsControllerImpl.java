package org.aston.depositservice.controller.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.aston.depositservice.controller.DepositsController;
import org.aston.depositservice.dto.request.AutorenewalRequestDto;
import org.aston.depositservice.dto.request.CloseDepositRequestDto;
import org.aston.depositservice.dto.request.NewDepositRequestDto;
import org.aston.depositservice.dto.request.ProlongDepositRequestDto;
import org.aston.depositservice.dto.response.ActiveDepositListResponseDto;
import org.aston.depositservice.dto.response.AutorenewalStatusResponseDto;
import org.aston.depositservice.dto.response.CloseDepositResponseDto;
import org.aston.depositservice.dto.response.NewDepositResponseDto;
import org.aston.depositservice.dto.response.ProlongDepositResponseDto;
import org.aston.depositservice.service.DepositService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.aston.depositservice.configuration.ApplicationConstant.HEADER_KEY_CUSTOMER_ID;
import static org.aston.depositservice.configuration.ApplicationConstant.HEADER_KEY_CUSTOMER_STATUS;
import static org.aston.depositservice.configuration.ApplicationConstant.INVALID_UUID;
import static org.aston.depositservice.configuration.ApplicationConstant.REGEXP_UUID;
import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/deposit/deposits")
public class DepositsControllerImpl implements DepositsController {

    private final DepositService depositService;

    @Override
    @GetMapping
    public ResponseEntity<List<ActiveDepositListResponseDto>> getActiveDepositList(
            @RequestHeader(HEADER_KEY_CUSTOMER_ID) String customerId
    ) {
        return ResponseEntity.ok(depositService.getActiveDepositList(customerId));
    }

    @Override
    @PatchMapping("/{depositId}/autoRenewal")
    public ResponseEntity<AutorenewalStatusResponseDto> depositAutorenewal(
            @Valid @RequestBody AutorenewalRequestDto autorenewalRequestDto,
            @PathVariable("depositId") @Pattern(regexp = REGEXP_UUID) String depositId,
            @RequestHeader(HEADER_KEY_CUSTOMER_ID) String customerId
    ) {
        return ResponseEntity.ok(depositService.changeAutorenewalStatus(depositId, autorenewalRequestDto, customerId));
    }

    @Override
    @PostMapping
    public ResponseEntity<NewDepositResponseDto> createDeposit(@RequestHeader(HEADER_KEY_CUSTOMER_ID) String customerId,
                                                               @RequestHeader(HEADER_KEY_CUSTOMER_STATUS) String customerStatus,
                                                               @RequestBody @Valid NewDepositRequestDto newDepositRequestDto) {
        return new ResponseEntity<>(depositService.createDeposit(newDepositRequestDto, customerStatus, customerId), CREATED);
    }

    @Override
    @PatchMapping("/{depositId}")
    public ResponseEntity<CloseDepositResponseDto> closeDeposit(
            @PathVariable @Pattern(regexp = REGEXP_UUID, message = INVALID_UUID) String depositId,
            @RequestBody @Valid CloseDepositRequestDto closeDepositRequestDto,
            @RequestHeader(HEADER_KEY_CUSTOMER_ID) String customerId
    ) {

        return ResponseEntity.ok(depositService.closeDeposit(depositId, closeDepositRequestDto, customerId));
    }

    @Override
    @PostMapping("/prolongConditions")
    public ResponseEntity<ProlongDepositResponseDto> prolongDeposit(
            @Valid @RequestBody ProlongDepositRequestDto prolongDepositRequestDto) {
        return ResponseEntity.status(CREATED).body(depositService.prolongDeposit(prolongDepositRequestDto));
    }
}