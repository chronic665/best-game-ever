package com.netent.application.bestgameever.boundary;

import com.netent.application.bestgameever.control.GameService;
import com.netent.application.bestgameever.dto.ResultPage;
import com.netent.application.bestgameever.entity.RoundResult;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotEmpty;

@RestController
@Validated
public class GameRestController extends ValidatedRestController {

    private final GameService gameService;

    @Autowired
    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * REST mapping for playing. User must have callend the /login route before being able to play.
     * @param username
     * @return
     */
    @PostMapping(value = "/plays/{username}")
    public ResponseEntity<String> play(@PathVariable("username") String username) {
        String roundId = gameService.play(username);
        return new ResponseEntity<>(roundId, HttpStatus.OK);
    }

    /**
     * REST mapping to listen on for game events for a user. Provides an infinite stream of server side events.
     * @param username
     * @param roundId - optional: if provided only a certain round (or HTTP 404 if not existing) will be
     *                returned and the stream will be closed afterwards
     * @return
     */
    @GetMapping(value = "/plays/{username}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Publisher<ResultPage> subscribe(@PathVariable("username") final String username,
                                           @RequestParam(value = "roundId", required = false) final String roundId) {
        return gameService.subscribeToResults(username, roundId);
    }

    @ExceptionHandler({ ResourceNotFoundException.class })
    public ResponseEntity handleExceptions() {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }


}
