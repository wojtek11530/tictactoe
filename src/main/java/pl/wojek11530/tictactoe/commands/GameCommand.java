package pl.wojek11530.tictactoe.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.wojek11530.tictactoe.domain.Player;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
public class GameCommand {
    private Long id;
    private static final String DEFAULT_BOARD_CHARACTER = " ";

    @NotNull
    private Player player1;
    @NotNull
    private Player player2;
    private Player currentPlayer;
    private Player winner;


}
