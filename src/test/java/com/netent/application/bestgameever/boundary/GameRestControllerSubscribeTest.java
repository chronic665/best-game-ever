package com.netent.application.bestgameever.boundary;

import com.google.gson.Gson;
import com.netent.application.bestgameever.control.GameService;
import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.RoundResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GameRestControllerSubscribeTest {

    private static final String MOCK_USERNAME = "test";
    private static final String SUBSCRIBE_URL = "/subscribe";

    @Mock
    private GameService mockGameService;

    private WebTestClient client;

    @Before
    public void setup() {
        client = WebTestClient.bindToController(new GameRestController(mockGameService))
                .configureClient()
                .baseUrl("/")
                .build();
    }

    @Test
    public void givenValidUsername_whenGetToSubscribe_thenReturnsHttpStream() {
        given(mockGameService.subscribeToResults(eq(MOCK_USERNAME), anyString()))
                .willReturn(Flux.empty());

        this.client.get().uri(SUBSCRIBE_URL + "/" + MOCK_USERNAME)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM)
        ;
    }

    @Test
    public void givenNoUsername_whenGetToSubscribe_thenReturnsHttp404() {
        this.client.get().uri(SUBSCRIBE_URL + "/")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void givenValidUsernameAndInvalidRoundId_whenGetToSubscribe_thenReturnsHttp404() {
        String roundId = "4711";
        given(mockGameService.subscribeToResults(eq(MOCK_USERNAME), anyString()))
                .willThrow(new ResourceNotFoundException("No round with id " + roundId + " for user " + MOCK_USERNAME + " was found"));
        this.client.get()
                .uri(SUBSCRIBE_URL + "/" + MOCK_USERNAME + "?roundId=" + roundId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void givenValidUsernameAndValidRoundId_whenGetToSubscribe_thenReturnsHttp200AndRoundResult() {
        final String roundId = "4711";
        given(mockGameService.subscribeToResults(eq(MOCK_USERNAME), eq(roundId))).willReturn(
                Flux.generate(
                        () -> null,
                        (state, sink) -> {
                            RoundResult roundResult = new RoundResult(roundId, ResultType.LOSE);
                            sink.next(roundResult);
                            sink.complete();
                            return roundResult;
                        }
                )
        );
        this.client.get()
                .uri(SUBSCRIBE_URL + "/" + MOCK_USERNAME + "?roundId=" + roundId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .consumeWith(entityExchangeResult -> {
                        final String jsonString = new String(entityExchangeResult.getResponseBody()).substring(5).trim();
                        final RoundResult content = new Gson().fromJson(jsonString, RoundResult.class);
                        assertThat(content.getRoundId(), is(roundId));
                        assertThat(content.getResult(), is(ResultType.LOSE));
                        assertThat(content.getTimestamp(), is(notNullValue()));
                    });
    }
}
