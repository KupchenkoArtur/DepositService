package org.aston.depositservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DepositProductInfoResponseDto {

    private String id;
    private String name;
    private String currency;
    private BigDecimal amountMin;
    private BigDecimal amountMax;
    private Boolean productStatus;
    private Boolean autorenStatus;
    private Integer dayMax;
    private Integer dayMin;
    private Boolean timeLimited;
    private Integer capitalization;
    private Boolean replenishment;
    private Short withdrawal;
    private Boolean revocable;
    private BigDecimal penalty;
    private BigDecimal percentRate;
}
