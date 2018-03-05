package pl.wojek11530.tictactoe.services;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import pl.wojek11530.tictactoe.converters.GameCommandToGame;
import pl.wojek11530.tictactoe.converters.GameToGameCommand;
import pl.wojek11530.tictactoe.domain.Game;
import pl.wojek11530.tictactoe.domain.Player;
import pl.wojek11530.tictactoe.domain.SignOfPlayer;
import pl.wojek11530.tictactoe.repositories.GameRepository;
import pl.wojek11530.tictactoe.repositories.PlayerRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(GameServiceImpl.class)
public class GameServiceImplTest {

    GameServiceImpl gameService;

    @Mock
    GameRepository gameRepository;

    @Mock
    PlayerRepository playerRepository;

    @Mock
    GameToGameCommand gameToGameCommand;

    @Mock
    GameCommandToGame gameCommandToGame;

    @Before
    public void setUp() throws Exception {
        PowerMock.mockStatic(Math.class);
        EasyMock.expect(Math.random()).andReturn(0.1).anyTimes();
        PowerMock.replay(Math.class);


        MockitoAnnotations.initMocks(this);

        gameService = new GameServiceImpl(gameRepository,gameCommandToGame,gameToGameCommand, playerRepository);
    }

    @Test
    public void putSign() {
        Player player = new Player();
        player.setSignOfPlayer(SignOfPlayer.CROSS);

        Game game = new Game();
        game.setId(1L);
        game.setCurrentPlayer(player);

        Optional<Game> gameOptional = Optional.of(game);
        when(gameRepository.findById(anyLong())).thenReturn(gameOptional);

        gameService.putSign(1L,2,2);

        assertEquals(game.getGameBoard()[1][1], game.getCurrentPlayer().getSignOfPlayer().getSign());

    }

    @Test
    public void saveGameCommand() {
    }

    @Test
    public void findById() {
    }

    @Test
    public void findCommandById() {
    }

    @Test
    public void getRandomlyAFirstPlayer() {
        //given
        Player player1 = new Player();
        player1.setName("player1");
        player1.setSignOfPlayer(SignOfPlayer.CROSS);

        Player player2 = new Player();
        player2.setName("player2");
        player2.setSignOfPlayer(SignOfPlayer.CIRCLE);

        Game game = new Game();
        game.setId(1L);

        game.setPlayer1(player1);
        game.setPlayer2(player2);

        Optional<Game> gameOptional = Optional.of(game);

        when(gameRepository.findById(anyLong())).thenReturn(gameOptional);

        //when
        gameService.setRandomlyAFirstPlayer(1L);

        //than
        assertEquals(player1.getName(), game.getCurrentPlayer().getName());
        assertEquals(player1.getSignOfPlayer(), game.getCurrentPlayer().getSignOfPlayer());
        assertEquals(player1, game.getCurrentPlayer());

    }

    @Test
    public void testCheckIfAnyRowHasTheSameSignsNullCase() {
        //given
        Player player1 = new Player();
        player1.setName("player1");
        player1.setSignOfPlayer(SignOfPlayer.CROSS);

        Player player2 = new Player();
        player2.setName("player2");
        player2.setSignOfPlayer(SignOfPlayer.CIRCLE);

        Game game = new Game();
        game.setId(1L);

        game.setPlayer1(player1);
        game.setPlayer2(player2);

        game.getGameBoard()[0][0] = game.getPlayer1().getSignOfPlayer().getSign();

        Optional<Game> gameOptional = Optional.of(game);

        when(gameRepository.findById(anyLong())).thenReturn(gameOptional);

        //when
        Player result = gameService.checkIfAnyRowHasTheSameSigns(1L);

        //than
        assertEquals(result, null);

    }

    @Test
    public void testCheckIfAnyRowHasTheSameSignsNotNullCase() {
        //given
        Player player1 = new Player();
        player1.setName("player1");
        player1.setSignOfPlayer(SignOfPlayer.CROSS);

        Player player2 = new Player();
        player2.setName("player2");
        player2.setSignOfPlayer(SignOfPlayer.CIRCLE);

        Game game = new Game();
        game.setId(1L);

        game.setPlayer1(player1);
        game.setPlayer2(player2);

        game.getGameBoard()[0][0] = game.getPlayer1().getSignOfPlayer().getSign();
        game.getGameBoard()[0][1] = game.getPlayer1().getSignOfPlayer().getSign();
        game.getGameBoard()[0][2] = game.getPlayer1().getSignOfPlayer().getSign();

        Optional<Game> gameOptional = Optional.of(game);

        when(gameRepository.findById(anyLong())).thenReturn(gameOptional);

        //when
        Player result = gameService.checkIfAnyRowHasTheSameSigns(1L);

        //than
        assertEquals(result, player1);

    }
}