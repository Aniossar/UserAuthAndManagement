package com.UserAuthAndManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "User not found")
public class BadAuthException extends RuntimeException {

    public BadAuthException(String message) {
        super(message);
    }

}
