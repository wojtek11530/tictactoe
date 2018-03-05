package pl.wojek11530.tictactoe.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.wojek11530.tictactoe.domain.Player;

@Setter
@Getter
@NoArgsConstructor
public class GameCommand {
    private Long id;
    private static final String DEFAULT_BOARD_CHARACTER = " ";
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Player winner;


}
