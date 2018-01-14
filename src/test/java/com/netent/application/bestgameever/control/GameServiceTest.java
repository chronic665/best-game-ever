package com.netent.application.bestgameever.control;

import com.netent.application.bestgameever.entity.ResultType;
import com.netent.application.bestgameever.entity.User;
import com.netent.application.bestgameever.exception.UserDoesNotExistException;
import com.netent.application.bestgameever.framework.GameFramework;
import com.netent.application.bestgameever.repo.GameRepository;
import com.netent.application.bestgameever.repo.PersonRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    private static final String MOCK_USERNAME = "user";
    @Mock
    private GameRepository gameRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private GameFramework gameFramework;

    @InjectMocks
    private GameService cut;

    @Test
    public void givenNonExistingUsername_whenPlaying_throwException() {
        given(personRepository.find(anyString())).willReturn(null);
        Assertions.assertThrows(UserDoesNotExistException.class, () -> cut.play(MOCK_USERNAME));
        verify(personRepository,times(0)).updateBalance(anyString(), any());
        verify(gameRepository,times(0)).storeRound(any(), anyString());
    }

    @Test
    public void givenValidUsername_whenPlaying_returnUUID() {
        final String roundId = UUID.randomUUID().toString();
        given(personRepository.find(MOCK_USERNAME)).willReturn(new User(MOCK_USERNAME, 1000));
        given(personRepository.updateBalance(eq(MOCK_USERNAME), any())).willReturn((double) 1000);
        given(gameRepository.storeRound(any(), eq(MOCK_USERNAME))).willReturn(roundId);

        String result = cut.play(MOCK_USERNAME);
        assertThat(result, is(roundId));

        verify(personRepository,times(1)).find(eq(MOCK_USERNAME));
        verify(personRepository,times(1)).updateBalance(eq(MOCK_USERNAME), any());
        verify(gameRepository,times(1)).storeRound(any(), eq(MOCK_USERNAME));
    }

    @Test
    public void givenValidUsernameAndLowFunds_whenPlayingAndLosing_updateBalanceWithNewFunds() {
        final String roundId = UUID.randomUUID().toString();
        given(gameFramework.throwDice()).willReturn(ResultType.LOSE);
        given(gameFramework.calculateAmount(any(), true)).willReturn(Double.valueOf(-10));
        given(gameFramework.getFillUpAmount()).willReturn(Double.valueOf(1000));
        given(gameFramework.outOfFunds(any())).willReturn(true);
        given(personRepository.find(MOCK_USERNAME)).willReturn(new User(MOCK_USERNAME, Double.valueOf(-500)));
        given(personRepository.updateBalance(eq(MOCK_USERNAME), eq(Double.valueOf(-10)))).willReturn(Double.valueOf(-510));
        given(gameRepository.storeRound(any(), eq(MOCK_USERNAME))).willReturn(roundId);

        String result = cut.play(MOCK_USERNAME);
        assertThat(result, is(roundId));

        verify(personRepository,times(1)).find(eq(MOCK_USERNAME));
        verify(personRepository,times(1)).updateBalance(eq(MOCK_USERNAME), eq(Double.valueOf(-10)));
        verify(personRepository,times(1)).updateBalance(eq(MOCK_USERNAME), eq(Double.valueOf(1000)));
        verify(gameRepository,times(1)).storeRound(eq(ResultType.LOSE), eq(MOCK_USERNAME));
        verify(gameRepository,times(1)).storeRound(eq(ResultType.FILL_UP_BALANCE), eq(MOCK_USERNAME));
    }
}