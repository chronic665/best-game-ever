package com.netent.application.bestgameever;

import com.netent.application.bestgameever.control.LoginService;
import com.netent.application.bestgameever.dto.LoginResponse;
import com.netent.application.bestgameever.dto.PlayResponse;
import com.netent.application.bestgameever.dto.ResponseType;
import com.netent.application.bestgameever.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameRestController {

    private final LoginService loginService;

    @Autowired
    public GameRestController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestParam String username,
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

    @PostMapping(value = "play")
    public PlayResponse play(@RequestParam String username) {

        throw new RuntimeException("Not implemented yet");
    }

}
