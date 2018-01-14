package com.netent.application.bestgameever.boundary;

import com.netent.application.bestgameever.control.LoginService;
import com.netent.application.bestgameever.exception.UserAlreadyExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginRestControllerTest {

    private static final String MOCK_USERNAME = "test";
    public static final String LOGIN_URL = "/login";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService mockLoginService;


    @Test
    public void givenPostRequest_whenLoginWithNewUsername_ShouldReturnHttpOKAndResponseTypeI() throws Exception {
        given(mockLoginService.login(MOCK_USERNAME)).willReturn(true);
        this.mockMvc.perform(
                    post(LOGIN_URL)
                    .param("username", MOCK_USERNAME)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{'type': 'I'}",false));
    }


    @Test
    public void givenPostRequest_whenLoginWithExistingUsernameAndUseExistingFalse_ShouldReturnHttpForbiddenAndResponseTypeW() throws Exception {
        given(mockLoginService.login(MOCK_USERNAME)).willThrow(new UserAlreadyExistsException());
        this.mockMvc.perform(
                    post(LOGIN_URL)
                    .param("username", MOCK_USERNAME)
                )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().json("{'type': 'W'}",false));
    }

    @Test
    public void givenPostRequest_whenLoginWithExistingUsernameAndUseExistingTrue_ShouldReturnHttpOKAndResponseTypeI() throws Exception {
        given(mockLoginService.login(MOCK_USERNAME)).willThrow(new UserAlreadyExistsException());
        this.mockMvc.perform(
                    post(LOGIN_URL)
                    .param("username", MOCK_USERNAME)
                    .param("useExisting", "true")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{'type': 'I'}",false));
    }

    @Test
    public void givenPostRequestWithValidRequest_whenDownstreamError_ShouldReturnHttp503AndResponseTypeE() throws Exception {
        given(mockLoginService.login(anyString())).willThrow(new RuntimeException());
        this.mockMvc.perform(
                    post(LOGIN_URL)
                    .param("username", MOCK_USERNAME)
                )
                .andDo(print())
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().json("{'type': 'E'}",false));
    }

    @Test
    public void givenPostRequestWithoutParams_ShouldReturnHttp400() throws Exception {
        this.mockMvc.perform(
                    post(LOGIN_URL)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPostRequestWithEmptyUsername_ShouldReturnHttp400() throws Exception {
        this.mockMvc.perform(
                    post(LOGIN_URL)
                    .param("username", "")
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}