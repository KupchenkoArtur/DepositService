package org.aston.depositservice.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.aston.depositservice.exception.DepositException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.aston.depositservice.configuration.ApplicationConstant.INVALID_DATA;
import static org.aston.depositservice.configuration.ApplicationConstant.INVALID_UUID;
import static org.aston.depositservice.configuration.ApplicationConstant.MISSING_PARAMETER;
import static org.aston.depositservice.configuration.ApplicationConstant.MISSING_REQUEST_HEADERS;
import static org.aston.depositservice.configuration.ApplicationConstant.UNSUPPORTED_DATA_TYPE;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {

    List<String> typeFields = List.of("productId", "customerId", "percentRate",
            "timeDays", "capitalization", "replenishment", "withdrawal");

    @ExceptionHandler({DepositException.class})
    public ResponseEntity<String> handleDepositException(DepositException e) {
        log.error(INVALID_DATA + ": {} {}", e.getMessage(), e.getStackTrace());
        return ResponseEntity
                .status((e.getStatus() != null) ? e.getStatus() : HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(INVALID_DATA + ": {} {}", e.getMessage(), e.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(INVALID_DATA);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

            if (typeFields.contains(fieldName) && (UNSUPPORTED_DATA_TYPE.equals(errorMessage))) {
                status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            }

            errors.put(fieldName, errorMessage);
        }
        return new ResponseEntity<>(errors, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(INVALID_DATA + ": {} {}", e.getMessage(), e.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(INVALID_UUID + e.getMessage());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<String> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.error(INVALID_DATA + ": {} {}", e.getMessage(), e.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(INVALID_DATA);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.error(MISSING_REQUEST_HEADERS + ": {} {}", e.getMessage(), e.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(MISSING_REQUEST_HEADERS);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(MISSING_PARAMETER + ": {} {}", e.getMessage(), e.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(MISSING_PARAMETER);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error(UNSUPPORTED_DATA_TYPE + ": {} {}", e.getMessage(), e.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(UNSUPPORTED_DATA_TYPE);
    }
}
