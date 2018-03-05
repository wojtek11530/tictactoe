package pl.wojek11530.tictactoe.converters;

import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pl.wojek11530.tictactoe.commands.GameCommand;
import pl.wojek11530.tictactoe.domain.Game;

@Component
public class GameToGameCommand implements Converter<pl.wojek11530.tictactoe.domain.Game, GameCommand> {

    @Synchronized
    @Nullable
    @Override
    public GameCommand convert(Game source) {
        if (source == null) {
            return null;
        }

        final GameCommand gameCommand = new GameCommand();
        gameCommand.setId(source.getId());
        gameCommand.setCurrentPlayer(source.getCurrentPlayer());
        gameCommand.setPlayer1(source.getPlayer1());
        gameCommand.setPlayer2(source.getPlayer2());
        gameCommand.setWinner(source.getWinner());
        return gameCommand;
    }
}
