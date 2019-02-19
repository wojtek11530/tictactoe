package pl.wojek11530.tictactoe.bootstrap;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.wojek11530.tictactoe.domain.Player;
import pl.wojek11530.tictactoe.repositories.PlayerRepository;

@Slf4j
@Component
public class PlayerBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final PlayerRepository playerRepository;

    public PlayerBootstrap(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Player AIPlayer = new Player("AI Player",false);
        AIPlayer. setId(0L);
        playerRepository.save(AIPlayer);
    }
}
