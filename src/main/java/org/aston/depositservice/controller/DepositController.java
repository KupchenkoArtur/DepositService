package org.aston.depositservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aston.depositservice.dto.response.ActiveDepositResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import static org.aston.depositservice.configuration.ApplicationConstant.HEADER_KEY_CUSTOMER_ID;

@Tag(name = "Контроллер для работы с депозитами клиента", description = "Deposit API version v1")
public interface DepositController {

    @Operation(
            summary = "Просмотр подробной информации о действующем депозите",
            description = "Получает подробную информацию о депозите по индентификатору депозита",
            tags = {"deposit"})
    ResponseEntity<ActiveDepositResponseDto> getActiveCustomerDeposit(@RequestHeader(HEADER_KEY_CUSTOMER_ID) String customerId,
                                                                      @RequestParam("deposit_id") String depositId);
}
