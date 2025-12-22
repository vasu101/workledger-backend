package com.workledger.core.common.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BusinessValidationException extends RuntimeException {

    private final List<String> validationErrors;

    public BusinessValidationException(String message) {
        super(message);
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(message);
    }

    public BusinessValidationException(List<String> validationErrors) {
        super("Business validation failed");
        this.validationErrors = validationErrors;
    }
}
