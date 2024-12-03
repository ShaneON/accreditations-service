package com.yieldstreet.accreditation.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class APIException extends Exception {
    private final String message;
}
