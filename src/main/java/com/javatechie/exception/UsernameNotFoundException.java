package com.javatechie.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class UsernameNotFoundException extends RrsException {

    public UsernameNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
