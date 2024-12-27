package org.aston.depositservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import static org.aston.depositservice.configuration.ApplicationConstant.MISSING_PARAMETER;

@Getter
@Setter
public class CloseDepositRequestDto {

    @NotNull(message = MISSING_PARAMETER)
    private Boolean depositStatus;
}
