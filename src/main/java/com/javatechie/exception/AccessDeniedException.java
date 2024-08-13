package com.javatechie.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;


@Slf4j
public class AccessDeniedException extends RrsException {
    public AccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
