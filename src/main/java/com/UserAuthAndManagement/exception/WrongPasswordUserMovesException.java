package com.UserAuthAndManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Wrong old password")
public class WrongPasswordUserMovesException extends RuntimeException {

    public WrongPasswordUserMovesException(String message) {
        super(message);
    }

}
