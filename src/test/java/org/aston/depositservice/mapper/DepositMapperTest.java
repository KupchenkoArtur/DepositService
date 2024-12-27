package org.aston.depositservice.mapper;

import org.aston.depositservice.dto.request.NewDepositRequestDto;
import org.aston.depositservice.dto.response.CloseDepositResponseDto;
import org.aston.depositservice.dto.response.NewDepositResponseDto;
import org.aston.depositservice.persistance.entity.Deposit;
import org.aston.depositservice.persistance.entity.DepositAccount;
import org.aston.depositservice.persistance.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.aston.depositservice.data.DepositData.CUSTOMER_ID;
import static org.aston.depositservice.data.DepositData.createDeposit;
import static org.aston.depositservice.data.DepositData.createDepositAccount;
import static org.aston.depositservice.data.DepositData.createDepositProduct;
import static org.aston.depositservice.data.DepositData.createNewDepositRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DepositMapperTest {

    private DepositMapper depositMapper;

    @BeforeEach
    void setUp() {
        depositMapper = Mappers.getMapper(DepositMapper.class);
    }

    @Test
    void givenNewDepositRequestDto_whenToDtoToDeposit_thenDeposit() {
        NewDepositRequestDto dto = createNewDepositRequestDto();
        DepositAccount depositAccount = createDepositAccount();

        Product depositProduct = createDepositProduct();
        depositProduct.setId(UUID.randomUUID());

        Deposit deposit = depositMapper.dtoToDeposit(dto, depositProduct, depositAccount,
                true, true, CUSTOMER_ID, depositProduct.getAmountMin());

        assertNotNull(deposit);
        assertEquals(UUID.fromString(CUSTOMER_ID), deposit.getCustomerId());
        assertEquals(depositProduct, deposit.getProduct());
        assertEquals(true, deposit.getDepositStatus());
        assertEquals(true, deposit.getAutorenStatus());
    }

    @Test
    void givenDeposit_whenToDepositToDto_thenNewDepositResponseDto() {
        Deposit deposit = createDeposit();

        String productName = "Test Product";

        NewDepositResponseDto responseDto = depositMapper.depositToDto(deposit, productName);

        assertNotNull(responseDto);
        assertEquals(deposit.getDepositStatus(), responseDto.isDepositStatus());
        assertEquals(deposit.getId(), responseDto.getDepositId());
        assertEquals(productName, responseDto.getName());
        assertEquals(deposit.getStartDate().toString(), responseDto.getStartDate());
        assertEquals(deposit.getEndDate().toString(), responseDto.getEndDate());
        assertEquals(0, responseDto.getCapitalization());
        assertEquals(deposit.getAutorenStatus(), responseDto.isAutorenStatus());
    }

    @Test
    void givenDeposit_whenToCloseDepositResponseDto_thenCorrectMapping() {
        Deposit deposit = createDeposit();
        Product product = new Product();

        deposit.setProduct(product);
        deposit.setDepositStatus(false);
        deposit.setCloseDate(Instant.now());
        product.setName("Продукт");

        DepositAccount account = new DepositAccount();
        account.setMainNum("12345678901234567890");
        deposit.setAccount(account);

        CloseDepositResponseDto responseDto = depositMapper.depositToCloseDepositResponseDto(deposit);

        assertNotNull(responseDto);
        assertEquals(account.getMainNum(), responseDto.getAccountId());
        assertEquals(product.getName(), responseDto.getProductName());
        assertEquals(deposit.getDepositStatus(), responseDto.isDepositStatus());

        String expectedFormattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX")
                .format(deposit.getCloseDate().atZone(ZoneId.systemDefault()));

        assertEquals(expectedFormattedDate, responseDto.getClosedDate());
    }
}

