package com.netent.application.bestgameever.boundary;

import com.netent.application.bestgameever.control.LoginService;
import com.netent.application.bestgameever.dto.LoginResponse;
import com.netent.application.bestgameever.dto.ResponseType;
import com.netent.application.bestgameever.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

@RestController
public class LoginRestController extends ValidatedRestController {
    private final LoginService loginService;

    @Autowired
    public LoginRestController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestParam @NotEmpty() String username,
                                               @RequestParam(required = false, defaultValue = "false") boolean useExisting) {
        try {
            loginService.login(username, useExisting);
        } catch(UserAlreadyExistsException e) {
            return httpResponse(ResponseType.W);
        } catch(RuntimeException e) {
            return httpResponse(ResponseType.E);
        }
        return httpResponse(ResponseType.I);
    }

    private ResponseEntity<LoginResponse> httpResponse(final ResponseType responseType) {
        return new ResponseEntity<>(new LoginResponse(responseType), responseType.getCode());
    }

}
