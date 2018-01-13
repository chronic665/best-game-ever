package com.netent.application.bestgameever;

import com.netent.application.bestgameever.control.LoginService;
import com.netent.application.bestgameever.dto.LoginResponse;
import com.netent.application.bestgameever.dto.PlayResponse;
import com.netent.application.bestgameever.dto.ResponseType;
import com.netent.application.bestgameever.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotEmpty;

@RestController
@Validated
public class GameRestController {

    private final LoginService loginService;

    @Autowired
    public GameRestController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestParam @NotEmpty() String username,
                               @RequestParam(required = false, defaultValue = "false") boolean useExisting) {
        try {
            loginService.login(username);
        } catch(UserAlreadyExistsException e) {
            if(!useExisting) {
                return httpResponse(ResponseType.W);
            }
        } catch(RuntimeException e) {
            return httpResponse(ResponseType.E);
        }
        return httpResponse(ResponseType.I);
    }

    private ResponseEntity<LoginResponse> httpResponse(final ResponseType responseType) {
        return new ResponseEntity<>(new LoginResponse(responseType), responseType.getCode());
    }

    /**
     * This method catches all ConstraintViolationExceptions thrown from the Validation integration and transforms the
     * RuntimeException that would result in a HTTP 5xx into a HTTP 400 response. No atual implementation needed, the magic
     * is done by annotations
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST,
            reason = "Your did not provide a valid input. Please make sure you provide a not-empty request parameter 'username'")
    public void handleValidationError() {}

    @PostMapping(value = "play")
    public PlayResponse play(@RequestParam @NotEmpty String username) {

        throw new RuntimeException("Not implemented yet");
    }

}
