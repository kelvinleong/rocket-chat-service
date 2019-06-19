package com.mangoim.chat.service.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InternalException extends ResponseStatusException {

    public InternalException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
