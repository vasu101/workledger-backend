package com.workledger.core.common.exception;


import lombok.Getter;

@Getter
public class InvalidStateException extends RuntimeException {

    private final String currentState;
    private final String expectedState;

    public InvalidStateException(String message) {
        super(message);
        this.currentState = null;
        this.expectedState = null;
    }

    public InvalidStateException(String message, String currentState, String expectedState) {
        super(message);
        this.currentState = currentState;
        this.expectedState = expectedState;
    }
}
