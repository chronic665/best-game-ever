package com.netent.application.bestgameever.boundary;

import com.netent.application.bestgameever.control.GameService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

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
}
