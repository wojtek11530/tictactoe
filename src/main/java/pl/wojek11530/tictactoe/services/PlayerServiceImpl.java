package pl.wojek11530.tictactoe.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.wojek11530.tictactoe.commands.PlayerCommand;
import pl.wojek11530.tictactoe.converters.PlayerCommandToPlayer;
import pl.wojek11530.tictactoe.converters.PlayerToPlayerCommand;
import pl.wojek11530.tictactoe.domain.Player;
import pl.wojek11530.tictactoe.repositories.PlayerRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerToPlayerCommand playerToPlayerCommand;
    private final PlayerCommandToPlayer playerCommandToPlayer;

    public PlayerServiceImpl(PlayerRepository playerRepository, PlayerToPlayerCommand playerToPlayerCommand, PlayerCommandToPlayer playerCommandToPlayer) {
        this.playerRepository = playerRepository;
        this.playerToPlayerCommand = playerToPlayerCommand;
        this.playerCommandToPlayer = playerCommandToPlayer;
    }

    @Override
    public Set<PlayerCommand> listAllCommandPlayers() {
        return StreamSupport.stream(playerRepository.findAll()
                .spliterator(), false)
                .filter(player -> player.isReal())
                .map(playerToPlayerCommand::convert)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Player> getPlayerList() {
        Set<Player> playerSet = new HashSet<>();
        playerRepository.findAll().iterator().forEachRemaining(playerSet::add);

        return playerSet.stream()
                .filter(player -> player.isReal()).collect(Collectors.toSet());
    }

    @Override
    public PlayerCommand savePlayerCommand(PlayerCommand command) {

        command.setReal(true);
        Player detachedPlayer = playerCommandToPlayer.convert(command);

        Player savedPLayer = playerRepository.save(detachedPlayer);
        log.debug("Saved GameId:" + savedPLayer.getId());
        return playerToPlayerCommand.convert(savedPLayer);

    }

    @Override
    public Player getNotRealPLayer() {
        Set<Player> playerSet = new HashSet<>();
        playerRepository.findAll().iterator().forEachRemaining(playerSet::add);

        Player AIPlayer = null;

        for (Player player:playerSet){
            if (!player.isReal()){
                AIPlayer = player;
            }
        }
        return AIPlayer;
    }
}
