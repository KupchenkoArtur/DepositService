package org.aston.depositservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.aston.depositservice.dto.response.AllProductsShortInfoResponseDto;
import org.aston.depositservice.dto.response.DepositProductInfoResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

import static org.aston.depositservice.configuration.ApplicationConstant.INVALID_DATA;
import static org.aston.depositservice.configuration.ApplicationConstant.REGEXP_UUID;

@Tag(name = "Контроллер для работы с депозитными продуктами клиента", description = "Deposit API version v1")
public interface DepositProductController {

    @Operation(
            summary = "Получить список с краткой информацией о депозитных продуктах",
            description = "Получает список депозитных продуктов",
            tags = {"deposit"})
    ResponseEntity<List<AllProductsShortInfoResponseDto>> getAllProductsShortInfoList();

    @Operation(
            summary = "Просмотр подробной информации по депозитному продукту",
            description = "Находит депозитный продукт по его id и возвращает полную информацию о продукте",
            tags = {"deposit"})
    ResponseEntity<DepositProductInfoResponseDto> getDepositProductInfoByDepositId(
            @PathVariable("productId")
            @Pattern(regexp = REGEXP_UUID, message = INVALID_DATA) String productId);
}
