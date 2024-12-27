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
public class AllProductsShortInfoResponseDto {
    private String id;
    private String name;
    private String currency;
    private BigDecimal amountMin;
    private BigDecimal amountMax;
    private Integer dayMin;
    private Integer dayMax;
    private Integer capitalization;
    private Boolean replenishment;
    private Short withdrawal;
    private Boolean revocable;
    private BigDecimal percentRate;
}
