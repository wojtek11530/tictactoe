package pl.wojek11530.tictactoe.services;

import org.springframework.stereotype.Service;
import pl.wojek11530.tictactoe.commands.PlayerCommand;
import pl.wojek11530.tictactoe.domain.Player;

import java.util.Set;



public interface PlayerService {

    Set<PlayerCommand> listAllCommandPlayers();

    Set<Player> getPlayerList();

    PlayerCommand savePlayerCommand(PlayerCommand command);

    Player getNotRealPLayer();
}
