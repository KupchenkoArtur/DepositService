package org.aston.depositservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.aston.depositservice.dto.request.AutorenewalRequestDto;
import org.aston.depositservice.dto.request.CloseDepositRequestDto;
import org.aston.depositservice.dto.request.NewDepositRequestDto;
import org.aston.depositservice.dto.request.ProlongDepositRequestDto;
import org.aston.depositservice.dto.response.ActiveDepositListResponseDto;
import org.aston.depositservice.dto.response.AutorenewalStatusResponseDto;
import org.aston.depositservice.dto.response.CloseDepositResponseDto;
import org.aston.depositservice.dto.response.NewDepositResponseDto;
import org.aston.depositservice.dto.response.ProlongDepositResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

import static org.aston.depositservice.configuration.ApplicationConstant.HEADER_KEY_CUSTOMER_ID;
import static org.aston.depositservice.configuration.ApplicationConstant.HEADER_KEY_CUSTOMER_STATUS;
import static org.aston.depositservice.configuration.ApplicationConstant.INVALID_UUID;
import static org.aston.depositservice.configuration.ApplicationConstant.REGEXP_UUID;

@Tag(name = "Контроллер для работы с депозитами", description = "Deposits API version v1")
public interface DepositsController {

    @Operation(
            summary = "Получить список действующих депозитов",
            description = "Получает список действующих депозитов по идентификатору клиента",
            tags = {"deposit"})
    ResponseEntity<List<ActiveDepositListResponseDto>> getActiveDepositList(@RequestHeader(HEADER_KEY_CUSTOMER_ID) String customerId);

    @Operation(
            summary = "Включение и отключение автопродления депозита",
            description = "Меняет статус автопродления депозита",
            tags = {"deposit"})
    ResponseEntity<AutorenewalStatusResponseDto> depositAutorenewal(
            @Valid @RequestBody AutorenewalRequestDto autorenewalRequestDto,
            @PathVariable @Pattern(regexp = REGEXP_UUID) String depositId,
            @RequestHeader(HEADER_KEY_CUSTOMER_ID) String customerId
    );

    @Operation(
            summary = "Создание нового депозита",
            description = "Создание нового депозита, доступное авторизованным пользователям",
            tags = {"deposit"})
    ResponseEntity<NewDepositResponseDto> createDeposit(@RequestHeader(HEADER_KEY_CUSTOMER_ID) String customerId,
                                                        @RequestHeader(HEADER_KEY_CUSTOMER_STATUS) String customerStatus,
                                                        @RequestBody @Valid NewDepositRequestDto newDepositRequestDto);

    @Operation(
            summary = "Закрытие депозита",
            description = "Закрытие депозита по идентификатору",
            tags = {"deposit"})
    ResponseEntity<CloseDepositResponseDto> closeDeposit(@PathVariable @Pattern(regexp = REGEXP_UUID, message = INVALID_UUID) String depositId,
                                                         @RequestBody @Valid CloseDepositRequestDto closeDepositRequestDto,
                                                         @RequestHeader(HEADER_KEY_CUSTOMER_ID) String customerId);

    @Operation(
            summary = "Продлить действующий депозит",
            description = "Получает данные о депозите и продлевает его на определенное количество месяцев и дней",
            tags = {"deposit"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Успешный запрос на продление депозитов",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProlongDepositResponseDto.class))),
    })
    ResponseEntity<ProlongDepositResponseDto> prolongDeposit(
            @Valid @RequestBody ProlongDepositRequestDto prolongDepositRequestDto);
}