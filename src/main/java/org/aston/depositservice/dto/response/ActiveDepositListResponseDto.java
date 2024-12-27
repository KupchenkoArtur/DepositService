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
public class ActiveDepositListResponseDto {
    private String depositProductId;
    private String productName;
    private String currencyName;
    private BigDecimal currentBalance;
    private String closedAt;
    private String depositAccount;
    private String depositId;
}
