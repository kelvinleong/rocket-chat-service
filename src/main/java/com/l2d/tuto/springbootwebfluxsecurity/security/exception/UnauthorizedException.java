package com.l2d.tuto.springbootwebfluxsecurity.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author duc-d
 */
public class UnauthorizedException extends ResponseStatusException {

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
