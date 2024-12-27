package org.aston.depositservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DepositException extends RuntimeException {

    private HttpStatus status;

    public DepositException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public DepositException(String message) {
        super(message);
    }
}

