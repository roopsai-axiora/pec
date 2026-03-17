package com.axiora.pec.common.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends PecException {

    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email,
                HttpStatus.CONFLICT);
    }
}