package com.netent.application.bestgameever.boundary;

import com.netent.application.bestgameever.control.GameService;
import com.netent.application.bestgameever.exception.UserDoesNotExistException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.netent.application.bestgameever.testutils.UuidMatcher.matchesUuidPattern;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GameRestControllerPlayTest {

    private static final String MOCK_USERNAME = "test";
    private static final String PLAY_URL = "/plays";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService mockGameService;


    @Test
    public void givenUsername_whenPostToPlay_thenReturnsHttp200AndUUID() throws Exception {
        given(mockGameService.play(MOCK_USERNAME)).willReturn(UUID.randomUUID().toString());
        this.mockMvc.perform(
                post(PLAY_URL + "/" + MOCK_USERNAME)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(matchesUuidPattern()));
    }

    @Test
    public void givenEmptyUsername_whenPostToPlay_thenReturnsHttp404() throws Exception {
        given(mockGameService.play(MOCK_USERNAME)).willReturn(UUID.randomUUID().toString());
        this.mockMvc.perform(
                post(PLAY_URL + "/")
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenNoUsername_whenPostToPlay_thenReturnsHttp404() throws Exception {
        given(mockGameService.play(MOCK_USERNAME)).willReturn(UUID.randomUUID().toString());
        this.mockMvc.perform(post(PLAY_URL))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenNonExistentUsername_whenPostToPlay_thenReturnsHttp400() throws Exception {
        given(mockGameService.play(MOCK_USERNAME)).willThrow(new UserDoesNotExistException(MOCK_USERNAME));
        this.mockMvc.perform(
                    post(PLAY_URL + "/" + MOCK_USERNAME))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("No user with this username exists. Please login first!"));

    }

}