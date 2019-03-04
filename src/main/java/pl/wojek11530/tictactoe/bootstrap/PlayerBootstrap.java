package pl.wojek11530.tictactoe.bootstrap;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.wojek11530.tictactoe.domain.Difficulty;
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
        Player EasyAI = new Player("Easy AI",false);
        EasyAI.setDifficulty(Difficulty.EASY);
        playerRepository.save(EasyAI);

        Player MediumAI = new Player("Medium AI",false);
        MediumAI.setDifficulty(Difficulty.MEDIUM);
        playerRepository.save(MediumAI);

        Player DifficultAI = new Player("Difficult AI",false);
        DifficultAI.setDifficulty(Difficulty.DIFFICULT);
        playerRepository.save(DifficultAI);

        Player ImpossibleAI = new Player("Impossible AI",false);
        ImpossibleAI.setDifficulty(Difficulty.IMPOSSIBLE);
        playerRepository.save(ImpossibleAI);
    }
}
