package org.aston.depositservice.mapper;

import org.aston.depositservice.dto.request.NewDepositRequestDto;
import org.aston.depositservice.dto.response.CloseDepositResponseDto;
import org.aston.depositservice.dto.response.NewDepositResponseDto;
import org.aston.depositservice.dto.response.ProlongDepositResponseDto;
import org.aston.depositservice.persistance.entity.Deposit;
import org.aston.depositservice.persistance.entity.DepositAccount;
import org.aston.depositservice.persistance.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface DepositMapper {
    @Mapping(target = "account", source = "depositAccount")
    @Mapping(target = "curPercent", source = "dto.percentRate")
    @Mapping(target = "autorenStatus", source = "autorenStatus")
    @Mapping(target = "startDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "endDate", expression = "java(java.time.Instant.now().plus(Integer.parseInt(dto.getTimeDays()), java.time.temporal.ChronoUnit.DAYS))")
    @Mapping(target = "closeDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    Deposit dtoToDeposit(NewDepositRequestDto dto, Product product, DepositAccount depositAccount,
                         boolean depositStatus, boolean autorenStatus, String customerId, BigDecimal initialAmount);

    @Mapping(target = "depositStatus", source = "deposit.depositStatus")
    @Mapping(target = "depositId", expression = "java(deposit.getId())")
    @Mapping(target = "name", source = "productName")
    @Mapping(target = "startDate", expression = "java(deposit.getStartDate().toString())")
    @Mapping(target = "endDate", expression = "java(deposit.getEndDate().toString())")
    @Mapping(target = "capitalization", expression = "java(deposit.getProduct().getCapitalization())")
    NewDepositResponseDto depositToDto(Deposit deposit, String productName);

    @Mapping(source = "id", target = "depositId")
    @Mapping(source = "product.id", target = "depositProductId")
    @Mapping(source = "account.mainNum", target = "depositAccountNumber")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "endDate", target = "closedAt")
    @Mapping(source = "product.capitalization", target = "typeCapitalization")
    @Mapping(source = "autorenStatus", target = "autorenewalStatus")
    ProlongDepositResponseDto depositToProlongDepositResponseDto(Deposit deposit);

    @Mapping(source = "account.mainNum", target = "accountId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "depositStatus", target = "depositStatus")
    @Mapping(target = "closedDate", expression = "java(java.time.format.DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ssXXX\").format(deposit.getCloseDate().atZone(java.time.ZoneId.systemDefault())))")
    CloseDepositResponseDto depositToCloseDepositResponseDto(Deposit deposit);
}