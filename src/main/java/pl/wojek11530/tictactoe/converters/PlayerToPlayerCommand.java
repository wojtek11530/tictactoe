package pl.wojek11530.tictactoe.converters;



import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import pl.wojek11530.tictactoe.commands.PlayerCommand;
import pl.wojek11530.tictactoe.domain.Player;

@Component
public class PlayerToPlayerCommand implements Converter<Player, PlayerCommand>{
    @Synchronized
    @Nullable
    @Override
    public PlayerCommand convert(Player source) {
        if (source == null) {
            return null;
        }

        final PlayerCommand playerCommand = new PlayerCommand();
        playerCommand.setId(source.getId());
        playerCommand.setName(source.getName());
        playerCommand.setNumberOfDefeats(source.getNumberOfDefeats());
        playerCommand.setNumberOfDraws(source.getNumberOfDraws());
        playerCommand.setNumberOfWins(source.getNumberOfWins());
        playerCommand.setNumberOfGames(source.getNumberOfGames());
        playerCommand.setSignOfPlayer(source.getSignOfPlayer());

        return playerCommand;
    }
}
