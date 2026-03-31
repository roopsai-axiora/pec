package com.axiora.pec.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PecException extends RuntimeException {

    private final HttpStatus status;

    public PecException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
