package org.aston.depositservice.mapper;

import org.aston.depositservice.persistance.entity.Deposit;
import org.aston.depositservice.persistance.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "operDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "operType", constant = "21")
    @Mapping(target = "dbKt", constant = "2")
    @Mapping(target = "totalBalance", constant = "0")
    @Mapping(target = "operSum", source = "percent")
    @Mapping(target = "percBalance", source = "updatedPercBalance")
    @Mapping(target = "deposit", source = "deposit")
    Transaction mapDepositToTransaction(Deposit deposit, BigDecimal percent, BigDecimal curBalance, BigDecimal updatedPercBalance);
}
