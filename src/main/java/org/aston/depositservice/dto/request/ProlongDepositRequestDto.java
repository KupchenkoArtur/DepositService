package org.aston.depositservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.aston.depositservice.configuration.ApplicationConstant.MISSING_PARAMETER;
import static org.aston.depositservice.configuration.ApplicationConstant.REGEXP_UUID;
import static org.aston.depositservice.configuration.ApplicationConstant.RENEWAL_TERMS_DAYS_MUST_BE_POSITIVE_NUMBER;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProlongDepositRequestDto {

    @NotNull
    @NotBlank(message = MISSING_PARAMETER)
    @Pattern(regexp = REGEXP_UUID)
    private String depositId;

    @NotNull
    @Min(value = 0, message = RENEWAL_TERMS_DAYS_MUST_BE_POSITIVE_NUMBER)
    private Integer renewalTermsDays;
}
