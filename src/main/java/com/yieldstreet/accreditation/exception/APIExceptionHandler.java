package com.yieldstreet.accreditation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(APIException.class)
    public ResponseEntity<String> handleException(APIException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            errors.put(fieldName, error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleValidationEnumException(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        String message = null;
        if(ex.getMessage().contains("AccreditationType"))
            message = "Accepted values are: BY_INCOME, BY_NET_WORTH";
        else if (ex.getMessage().contains("AccreditationOutcome"))
            message = "Accepted values are: CONFIRMED, FAILED, EXPIRED";
        errors.put("error", message);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}