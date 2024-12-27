package org.aston.depositservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewDepositResponseDto {

    private boolean depositStatus;
    private UUID depositId;
    private String name;
    private String startDate;
    private String endDate;
    private Integer capitalization;
    private boolean autorenStatus;
}
