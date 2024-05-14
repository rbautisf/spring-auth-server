package com.nowhere.springauthserver.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Target all Controllers within specific packages
@ControllerAdvice(basePackages = "com.nowhere.springauthserver.api")
public class DefaultErrorHandler {
    private final Logger logger =LoggerFactory.getLogger(DefaultErrorHandler.class);


    @ExceptionHandler(InternalError.class)
    public ResponseEntity<String> handleException(InternalError e) {
        logger.error(String.format("An error occurred while processing the request: %s", e.getMessage()), e);
        // try to get the error code from the exception
        return ResponseEntity.status(500).body("An error occurred while processing the request");
    }

}
