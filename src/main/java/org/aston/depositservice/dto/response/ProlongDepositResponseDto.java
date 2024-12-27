package org.aston.depositservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProlongDepositResponseDto {

    private UUID depositId;
    private UUID depositProductId;
    private String closedAt;
    private String depositAccountNumber;
    private String productName;
    private Integer typeCapitalization;
    private Boolean autorenewalStatus;
}