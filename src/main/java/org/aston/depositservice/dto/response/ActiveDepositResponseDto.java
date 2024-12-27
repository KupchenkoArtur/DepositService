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
public class ActiveDepositResponseDto {
    private String name;
    private String currency;
    private Boolean timeLimited;
    private Boolean revocable;
    private Integer capitalization;
    private Short withdrawal;
    private Boolean productStatus;
    private Boolean autorenStatus;
    private BigDecimal initialAmount;
    private BigDecimal curBalance;
    private BigDecimal percBalance;
    private String startDate;
    private String endDate;
    private Boolean autorenewStatus;
    private BigDecimal percentRate;
    private String mainNum;
    private String percNum;
    private String mAccountId;
    private String pAccountId;
}
