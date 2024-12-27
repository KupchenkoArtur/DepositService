package org.aston.depositservice.data;

import org.aston.depositservice.dto.request.NewDepositRequestDto;
import org.aston.depositservice.dto.response.ActiveDepositListResponseDto;
import org.aston.depositservice.dto.response.ActiveDepositResponseDto;
import org.aston.depositservice.dto.response.DepositProductInfoResponseDto;
import org.aston.depositservice.persistance.entity.Deposit;
import org.aston.depositservice.persistance.entity.DepositAccount;
import org.aston.depositservice.persistance.entity.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

public class DepositData {

    public static final String CURRENCY_NAME = "RUB";
    public static final String CUSTOMER_ID = "123e4567-e89b-12d3-a456-426614174000";
    public static final String TEST_DEPOSIT_ID = "123e4567-e89b-12d3-a456-426614174000";
    public static final String TEST_PRODUCT_ID = "b1a2c3d4-e5f6-4890-abcd-ef1234567890";
    public static final String INCORRECT_TEST_DEPOSIT_ID = "123e4567-e89b-12d3-a456-426614174111";
    public static final String DEPOSIT_CONTROLLER_URL = "/api/v1/deposit";
    public static final String PRODUCT_NAME = "Bobr";
    public static final BigDecimal AMOUNT_MIN = BigDecimal.valueOf(100000);
    public static final BigDecimal AMOUNT_MAX = BigDecimal.valueOf(10000000);
    public static final BigDecimal PERCENT_RATE = BigDecimal.valueOf(17.3);
    public static final String DEPOSIT_ALREADY_CLOSED = "Депозит уже закрыт";
    public static final String INVALID_DATA = "Некорректный запрос";

    public static ActiveDepositListResponseDto createActiveDepositListResponseDto() {
        return ActiveDepositListResponseDto.builder()
                .depositProductId("1")
                .productName(PRODUCT_NAME)
                .currencyName(CURRENCY_NAME)
                .currentBalance(BigDecimal.valueOf(12455.00))
                .closedAt("2024-08-19")
                .depositAccount("123e4567-e89b-12d3-a456-426614174001")
                .depositId("123e4567-e89b-12d3-a456-426614174593")
                .build();
    }

    public static ActiveDepositResponseDto createActiveDepositResponseDto() {
        return ActiveDepositResponseDto.builder()
                .name("Lis")
                .currency("RUB")
                .timeLimited(true)
                .revocable(true)
                .capitalization(1)
                .withdrawal((short) 2)
                .productStatus(false)
                .autorenStatus(true)
                .initialAmount(BigDecimal.valueOf(1000000.55))
                .curBalance(BigDecimal.valueOf(50000))
                .percBalance(BigDecimal.valueOf(500))
                .startDate("04.03.2024")
                .endDate("04.03.2027")
                .autorenewStatus(true)
                .percentRate(BigDecimal.valueOf(10))
                .mainNum("12345678901234567890")
                .percNum("12345678901234567890")
                .mAccountId("12345678901234567891")
                .pAccountId("12345678901234567892")
                .build();
    }

    public static DepositProductInfoResponseDto createDepositProductInfoResponseDto() {
        return DepositProductInfoResponseDto.builder()
                .id("123e4567-e89b-12d3-a456-426614174001")
                .name(PRODUCT_NAME)
                .currency(CURRENCY_NAME)
                .amountMin(AMOUNT_MIN)
                .amountMax(AMOUNT_MAX)
                .productStatus(true)
                .autorenStatus(false)
                .dayMax(365)
                .dayMin(30)
                .timeLimited(true)
                .capitalization(1)
                .replenishment(true)
                .withdrawal((short) 0.5)
                .revocable(true)
                .penalty(new BigDecimal("0.5"))
                .percentRate(new BigDecimal("8"))
                .build();
    }

    public static NewDepositRequestDto createNewDepositRequestDto() {
        return NewDepositRequestDto.builder()
                .productId(UUID.randomUUID().toString())
                .percentRate("12.5")
                .timeDays("90")
                .accountId(UUID.randomUUID().toString())
                .build();
    }

    public static Product createDepositProduct() {
        return Product.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .name("Test Product")
                .capitalization(0)
                .build();
    }

    public static DepositAccount createDepositAccount() {
        return DepositAccount.builder()
                .mainNum("31230")
                .percNum("31212330")
                .mAccountId(UUID.randomUUID())
                .pAccountId(UUID.randomUUID())
                .transactions(Collections.emptySet())
                .build();
    }

    public static Deposit createDeposit() {
        Instant now = Instant.now();
        Product product = createDepositProduct();
        DepositAccount depositAccount = createDepositAccount();

        return Deposit.builder()
                .id(UUID.randomUUID())
                .product(product)
                .customerId(UUID.fromString("ff9a7874-40f8-42a1-ada4-bd49626bd3cc"))
                .account(depositAccount)
                .initialAmount(BigDecimal.valueOf(1000))
                .startDate(now)
                .endDate(now.plus(30, ChronoUnit.DAYS))
                .closeDate(null)
                .depositStatus(true)
                .autorenStatus(true)
                .curPercent(BigDecimal.valueOf(5))
                .transactions(new HashSet<>())
                .build();
    }
}