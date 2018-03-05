package pl.wojek11530.tictactoe.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.wojek11530.tictactoe.domain.Player;

public interface PlayerRepository extends CrudRepository<Player, Long> {
}
