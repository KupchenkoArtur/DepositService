package org.aston.depositservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AutorenewalStatusResponseDto {

    private String message = "Статус автопродления успешно обновлен.";

    private boolean autorenewalStatus;
}
