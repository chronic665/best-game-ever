package com.netent.application.bestgameever.boundary;

import com.netent.application.bestgameever.control.GameService;
import com.netent.application.bestgameever.dto.PlayResponse;
import com.netent.application.bestgameever.entity.RoundResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotEmpty;

@RestController
@Validated
public class GameRestController extends ValidatedRestController {

    private final GameService gameService;

    @Autowired
    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping(value = "/play")
    public ResponseEntity<String> play(@RequestParam @NotEmpty String username) {
        String roundId = gameService.play(username);
        return new ResponseEntity<>(roundId, HttpStatus.OK);
    }

    @GetMapping(value = "/subscribe/{username}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<RoundResult> subscribe(@PathVariable("username") final String username) {
        return gameService.subscribeToResults(username, null);
    }

}
