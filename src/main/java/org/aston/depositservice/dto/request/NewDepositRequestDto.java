package org.aston.depositservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.aston.depositservice.annotation.ValidIncomingParameters;

import static org.aston.depositservice.configuration.ApplicationConstant.REGEXP_PERCENT_RATE;
import static org.aston.depositservice.configuration.ApplicationConstant.REGEXP_UUID;
import static org.aston.depositservice.configuration.ApplicationConstant.REGEX_TIME_DAYS;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewDepositRequestDto {

    @ValidIncomingParameters(regexp = REGEXP_UUID)
    private String productId;

    @ValidIncomingParameters(regexp = REGEXP_PERCENT_RATE)
    private String percentRate;

    @ValidIncomingParameters(regexp = REGEX_TIME_DAYS)
    private String timeDays;

    @ValidIncomingParameters(regexp = REGEXP_UUID)
    private String accountId;
}
