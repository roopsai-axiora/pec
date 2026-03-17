package com.axiora.pec.common.exception;

import org.springframework.http.HttpStatus;

public class PecException extends RuntimeException {

    private final HttpStatus status;

    public PecException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
