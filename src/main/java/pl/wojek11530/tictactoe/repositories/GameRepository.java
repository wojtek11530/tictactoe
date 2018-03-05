package pl.wojek11530.tictactoe.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.wojek11530.tictactoe.domain.Game;


public interface GameRepository extends CrudRepository<Game, Long> {
}
