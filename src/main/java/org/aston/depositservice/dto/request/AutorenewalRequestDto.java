package org.aston.depositservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AutorenewalRequestDto {

    @NotNull(message = "Входной параметр autorenewal отсутствует")
    private Boolean autorenewal;
}
