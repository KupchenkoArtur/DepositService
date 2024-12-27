package org.aston.depositservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CloseDepositResponseDto {

    private String accountId;
    private String productName;
    private boolean depositStatus;
    private String closedDate;
}
