package com.netent.application.bestgameever.boundary;

import com.netent.application.bestgameever.dto.PlayResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

@RestController
@Validated
public class GameRestController extends ValidatedRestController {

    @PostMapping(value = "play")
    public PlayResponse play(@RequestParam @NotEmpty String username) {

        throw new RuntimeException("Not implemented yet");
    }

}
