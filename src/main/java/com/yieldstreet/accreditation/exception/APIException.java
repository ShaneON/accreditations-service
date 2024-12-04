package com.yieldstreet.accreditation.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIException extends Exception {
    private final String message;
}