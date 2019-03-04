package pl.wojek11530.tictactoe.services;

import pl.wojek11530.tictactoe.commands.GameCommand;
import pl.wojek11530.tictactoe.domain.Game;
import pl.wojek11530.tictactoe.domain.Player;

import java.util.Set;

public interface GameService {

    GameCommand saveGameCommand(GameCommand command);

    Game findById(Long l);

    GameCommand findCommandById(Long l);

    void setRandomlyAFirstPlayer(Long gameId);

    String playNextMove(Long gameId, int x, int y);

    Game repeatAGame(Long gameId);

    void deleteById(Long gameId);

    Set<Game> getGameList();

    String playNextAIMove(Long gameId);


}
