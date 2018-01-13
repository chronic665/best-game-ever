package com.netent.application.bestgameever.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@Validated
public abstract class ValidatedRestController {

    /**
     * This method catches all ConstraintViolationExceptions thrown from the Validation integration and transforms the
     * RuntimeException that would result in a HTTP 5xx into a HTTP 400 response. No atual implementation needed, the magic
     * is done by annotations
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST,
            reason = "Your did not provide a valid input. Please make sure you provide a not-empty request parameter 'username'")
    public void handleValidationError() {}
}
