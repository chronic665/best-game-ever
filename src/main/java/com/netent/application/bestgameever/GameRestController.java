package com.netent.application.bestgameever;

import com.netent.application.bestgameever.dto.LoginResponse;
import com.netent.application.bestgameever.dto.PlayResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameRestController {

    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public LoginResponse login(@RequestParam String username, @RequestParam(required = false) boolean useExisting) {

        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping(value = "play")
    public PlayResponse play(@RequestParam String username) {

        throw new RuntimeException("Not implemented yet");
    }

}
