package com.UserAuthAndManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "This login or email is already registered")
public class ExistingLoginEmailRegisterException extends RuntimeException {

    public ExistingLoginEmailRegisterException(String message) {
        super(message);
    }

}
