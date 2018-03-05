package pl.wojek11530.tictactoe.converters;

import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pl.wojek11530.tictactoe.commands.GameCommand;
import pl.wojek11530.tictactoe.domain.Game;

@Component
public class GameCommandToGame implements Converter<GameCommand, pl.wojek11530.tictactoe.domain.Game> {

    @Synchronized
    @Nullable
    @Override
    public pl.wojek11530.tictactoe.domain.Game convert(GameCommand source) {
        if (source == null) {
            return null;
        }

        final Game game = new Game();
        game.setId(source.getId());
        game.setCurrentPlayer(source.getCurrentPlayer());
        game.setPlayer1(source.getPlayer1());
        game.setPlayer2(source.getPlayer2());
        game.setWinner(source.getWinner());
        return game;
    }
}
