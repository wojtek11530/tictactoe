package pl.wojek11530.tictactoe.converters;



import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import pl.wojek11530.tictactoe.commands.PlayerCommand;
import pl.wojek11530.tictactoe.domain.Player;

@Component
public class PlayerCommandToPlayer implements Converter<PlayerCommand, Player>{
    @Synchronized
    @Nullable
    @Override
    public Player convert(PlayerCommand source) {
        if (source == null) {
            return null;
        }

        final Player player = new Player();
        player.setName(source.getName());
        player.setReal(source.isReal());
        player.setId(source.getId());
        player.setNumberOfDefeats(source.getNumberOfDefeats());
        player.setNumberOfDraws(source.getNumberOfDraws());
        player.setNumberOfWins(source.getNumberOfWins());
        player.setNumberOfGames(source.getNumberOfGames());
        player.setSignOfPlayer(source.getSignOfPlayer());

        return player;
    }
}
