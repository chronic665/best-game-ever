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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
        verify(gameRepository,times(0)).storeRound(any(), anyString(), anyBoolean());
    }

    @Test
    public void givenValidUsername_whenPlaying_returnUUID() {
        final String roundId = UUID.randomUUID().toString();
        given(personRepository.find(MOCK_USERNAME)).willReturn(new User(MOCK_USERNAME, 1000));
        given(personRepository.updateBalance(eq(MOCK_USERNAME), any())).willReturn((double) 1000);
        given(gameRepository.storeRound(any(), eq(MOCK_USERNAME), anyBoolean())).willReturn(roundId);

        String result = cut.play(MOCK_USERNAME);
        assertThat(result, is(roundId));

        verify(personRepository,times(1)).find(eq(MOCK_USERNAME));
        verify(personRepository,times(1)).updateBalance(eq(MOCK_USERNAME), any());
        verify(gameRepository,times(1)).storeRound(any(), eq(MOCK_USERNAME), anyBoolean());
    }

    @Test
    public void givenValidUsernameAndLowFunds_whenPlayingAndLosing_updateBalanceWithNewFunds() {
        final String roundId = UUID.randomUUID().toString();
        given(gameFramework.throwDice()).willReturn(ResultType.LOSE);
        given(gameFramework.calculateAmount(ResultType.LOSE, false)).willReturn(Double.valueOf(-10));
        given(gameFramework.getFillUpAmount()).willReturn(Double.valueOf(1000));
        given(gameFramework.outOfFunds(any())).willReturn(true);
        given(personRepository.find(MOCK_USERNAME)).willReturn(new User(MOCK_USERNAME, Double.valueOf(-500)));
        given(personRepository.updateBalance(eq(MOCK_USERNAME), eq(Double.valueOf(-10)))).willReturn(Double.valueOf(-510));
        given(gameRepository.storeRound(any(), eq(MOCK_USERNAME), anyBoolean())).willReturn(roundId);

        String result = cut.play(MOCK_USERNAME);
        assertThat(result, is(roundId));

        verify(personRepository,times(1)).find(eq(MOCK_USERNAME));
        verify(personRepository,times(1)).updateBalance(eq(MOCK_USERNAME), eq(Double.valueOf(-10)));
        verify(personRepository,times(1)).updateBalance(eq(MOCK_USERNAME), eq(Double.valueOf(1000)));
        verify(gameRepository,times(1)).storeRound(eq(ResultType.LOSE), eq(MOCK_USERNAME), anyBoolean());
        verify(gameRepository,times(1)).storeRound(eq(ResultType.FILL_UP_BALANCE), eq(MOCK_USERNAME), anyBoolean());
    }


    @Test
    public void givenValidUsername_whenPlayingAndLosingButFreeRound_callFreeRoundMethodsandReturnUUID() {
        final String roundId = UUID.randomUUID().toString();

        given(gameFramework.throwDice()).willAnswer(new Answer<ResultType>() {
            int call = 0;
            @Override
            public ResultType answer(InvocationOnMock invocationOnMock) throws Throwable {
                if(call == 0) {
                    call++;
                    return ResultType.FREE_ROUND;
                }
                else if(call == 1) {
                    return ResultType.LOSE;
                }
                throw new RuntimeException("Too many calls");
            }
        });
        given(personRepository.find(MOCK_USERNAME)).willReturn(new User(MOCK_USERNAME, 1000d));
        given(personRepository.updateBalance(eq(MOCK_USERNAME), any())).willReturn(990d);
        given(gameRepository.storeRound(any(), eq(MOCK_USERNAME), anyBoolean())).willReturn(roundId);

        String result = cut.play(MOCK_USERNAME);
        assertThat(result, is(roundId));

        verify(gameFramework,times(2)).throwDice();
        verify(gameFramework,times(1)).calculateAmount(any(), eq(false));
        verify(gameFramework,times(1)).calculateAmount(any(), eq(true));

        verify(personRepository,times(1)).find(eq(MOCK_USERNAME));
        verify(personRepository,times(2)).updateBalance(eq(MOCK_USERNAME), eq(0d));

        verify(gameRepository,times(1)).storeRound(ResultType.FREE_ROUND, MOCK_USERNAME, false);
        verify(gameRepository,times(1)).storeRound(ResultType.LOSE, MOCK_USERNAME, true);
    }
}